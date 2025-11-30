Param(
  [string]$BaseUrl = "http://localhost:8082",
  [int]$VehicleCount = 3,
  [int]$MaxAttempt = 120,
  [string]$ReportPath = "validation-report.md"
)

function Invoke-Api {
  param([string]$Method, [string]$Url, [string]$Body)
  if ($Body) {
    return curl -Method $Method -Uri $Url -ContentType 'application/json' -Body $Body
  } else {
    return curl -Method $Method -Uri $Url
  }
}

Write-Host "Checking health..." -ForegroundColor Cyan
$health = Invoke-Api GET "$BaseUrl/actuator/health"
Write-Host "Health:" $health.StatusCode
if ($health.StatusCode -ne 200) { Write-Error "Service not healthy"; exit 1 }

# Start collecting report data
$report = [ordered]@{
  Date = (Get-Date).ToString("yyyy-MM-dd HH:mm:ss")
  BaseUrl = $BaseUrl
  HealthStatus = $health.StatusCode
  GarageId = $null
  VehiclesRequested = $VehicleCount
  VehiclesCreated = 0
  KafkaConsumerLogsFound = $false
  CapacityMaxAttempt = $MaxAttempt
  CapacityEnforced = $false
  CapacityEnforcedAt = $null
}

Write-Host "Creating garage..." -ForegroundColor Cyan
$garagePayload = @'
{
  "name": "Garage Central",
  "address": { "rue": "1 Rue Test", "ville": "Rabat", "codePostal": "10000", "pays": "MA" },
  "telephone": "+212600000001",
  "email": "garage.central@renault.ma",
  "horairesOuverture": {
    "MONDAY": [ { "startTime": "08:00:00", "endTime": "12:00:00" }, { "startTime": "14:00:00", "endTime": "18:00:00" } ]
  }
}
'@
$garageResp = Invoke-Api POST "$BaseUrl/api/v1/garages" $garagePayload
if ($garageResp.StatusCode -ne 201) { Write-Error "Garage creation failed: $($garageResp.StatusCode)"; exit 1 }
$garageJson = $garageResp.Content | ConvertFrom-Json
$garageId = $garageJson.id
Write-Host "Garage created:" $garageId -ForegroundColor Green
$report.GarageId = $garageId

Write-Host "Creating vehicles ($VehicleCount)..." -ForegroundColor Cyan
$vehPayloadTemplate = {
@"
{
  "modeleId": "$(New-Guid)",
  "brand": "RENAULT",
  "anneeFabrication": 2024,
  "typeCarburant": "ESSENCE"
}
"@
}

for ($i = 1; $i -le $VehicleCount; $i++) {
  $vehPayload = & $vehPayloadTemplate
  $vehResp = Invoke-Api POST "$BaseUrl/api/v1/garages/$garageId/vehicules" $vehPayload
  if ($vehResp.StatusCode -ne 201) { Write-Error "Vehicle $i creation failed: $($vehResp.StatusCode)"; exit 1 }
  $vehJson = $vehResp.Content | ConvertFrom-Json
  Write-Host ("Vehicle {0} created: {1}" -f $i, $vehJson.id) -ForegroundColor Green
  $report.VehiclesCreated++
}

Write-Host "Listing vehicles for garage..." -ForegroundColor Cyan
$listResp = Invoke-Api GET "$BaseUrl/api/v1/garages/$garageId/vehicules"
if ($listResp.StatusCode -ne 200) { Write-Error "List vehicles failed: $($listResp.StatusCode)"; exit 1 }
$listJson = $listResp.Content | ConvertFrom-Json
Write-Host ("Vehicles count: {0}" -f ($listJson | Measure-Object).Count) -ForegroundColor Green
$report.VehiclesInGarageCount = ($listJson | Measure-Object).Count

Write-Host "Checking Kafka topic 'vehicule.created' in Kafka UI (manual): http://localhost:8090" -ForegroundColor Yellow

Write-Host "Recent consumer logs (app):" -ForegroundColor Cyan
docker logs renault_garage_app --since 5m | Select-String "KAFKA CONSUMER|VehiculeCreatedEvent|Message acquitté"
if ($LASTEXITCODE -eq 0) { $report.KafkaConsumerLogsFound = $true }

Write-Host ("Capacity stress: increasing until 400 or {0} attempts..." -f $MaxAttempt) -ForegroundColor Cyan
$failDetected = $false
for ($i = ($VehicleCount+1); $i -le $MaxAttempt; $i++) {
  $vehPayload = & $vehPayloadTemplate
  $vehResp = Invoke-Api POST "$BaseUrl/api/v1/garages/$garageId/vehicules" $vehPayload
  if ($vehResp.StatusCode -eq 400) { 
    $failDetected = $true; 
    Write-Host ("Capacity rule enforced at vehicle #{0}." -f $i) -ForegroundColor Green
    $report.CapacityEnforced = $true
    $report.CapacityEnforcedAt = $i
    break 
  }
}
if (-not $failDetected) { 
  Write-Warning "Capacity threshold not reached within max attempts (expected 400)." 
} 

Write-Host "Validation complete." -ForegroundColor Green

# Generate Markdown report
$md = @()
$md += "# Garage Service Validation Report"
$md += ""
$md += ("- Date: {0}" -f $report.Date)
$md += ("- BaseUrl: {0}" -f $report.BaseUrl)
$md += ("- Health Status: {0}" -f $report.HealthStatus)
$md += ("- Garage ID: {0}" -f $report.GarageId)
$md += ("- Vehicles Requested: {0}" -f $report.VehiclesRequested)
$md += ("- Vehicles Created: {0}" -f $report.VehiclesCreated)
$md += ("- Vehicles In Garage Count: {0}" -f $report.VehiclesInGarageCount)
$md += ("- Kafka Consumer Logs Found: {0}" -f $report.KafkaConsumerLogsFound)
$md += ("- Capacity Max Attempt: {0}" -f $report.CapacityMaxAttempt)
$md += ("- Capacity Enforced: {0}" -f $report.CapacityEnforced)
$md += ("- Capacity Enforced At Attempt: {0}" -f $report.CapacityEnforcedAt)
$md += ""
$md += "## Vehicles By Model"
$vehByModelId = (New-Guid).Guid
$vehByModelResp = Invoke-Api GET ("{0}/api/v1/vehicules?modeleId={1}" -f $BaseUrl, $vehByModelId)
if ($vehByModelResp.StatusCode -eq 200) {
  $vehByModelJson = $vehByModelResp.Content | ConvertFrom-Json
  $vehByModelCount = ($vehByModelJson | Measure-Object).Count
  $md += ("- Query modeleId: {0}" -f $vehByModelId)
  $md += ("- Vehicles By Model Count: {0}" -f $vehByModelCount)
} else {
  $md += ("- Vehicles By Model query failed: {0}" -f $vehByModelResp.StatusCode)
}
$md += ""
$md += "## Garages List (Paginated)"
$garagesResp = Invoke-Api GET ("{0}/api/v1/garages?page=0&size=10&sort=name&direction=ASC" -f $BaseUrl)
if ($garagesResp.StatusCode -eq 200) {
  $garagesJson = $garagesResp.Content | ConvertFrom-Json
  $total = $garagesJson.totalElements
  $pageSize = $garagesJson.size
  $pageNumber = $garagesJson.page
  $returned = ($garagesJson.garages | Measure-Object).Count
  $md += ("- Total Garages: {0}" -f $total)
  $md += ("- Page: {0}" -f $pageNumber)
  $md += ("- Size: {0}" -f $pageSize)
  $md += ("- Returned: {0}" -f $returned)
  if ($returned -gt 0) { $md += ("- First Garage Name: {0}" -f $garagesJson.garages[0].name) }
} else {
  $md += ("- Garages list failed: {0}" -f $garagesResp.StatusCode)
}
$md += ""
$md += "## Accessories"
# Create accessory on first created vehicle if available
if ($vehicules.Count -gt 0) {
  $firstVeh = $vehicules[0]
  $createAccBody = @{ 
    nom = "GPS"
    description = "Navigation system"
    prix = 199.99
    type = "ELECTRONIQUE"
  } | ConvertTo-Json
  $accCreateResp = Invoke-Api POST ("{0}/api/v1/garages/{1}/vehicules/{2}/accessoires" -f $BaseUrl, $garageId, $firstVeh.id) $createAccBody
  if ($accCreateResp.StatusCode -eq 201) {
    $accJson = $accCreateResp.Content | ConvertFrom-Json
    $md += ("- Created Accessory ID: {0}" -f $accJson.id)
    $md += ("- Created Accessory Name: {0}" -f $accJson.nom)
    # List accessories
    $accListResp = Invoke-Api GET ("{0}/api/v1/garages/{1}/vehicules/{2}/accessoires" -f $BaseUrl, $garageId, $firstVeh.id)
    if ($accListResp.StatusCode -eq 200) {
      $accListJson = $accListResp.Content | ConvertFrom-Json
      $md += ("- Accessories Count: {0}" -f (($accListJson | Measure-Object).Count))
    }
    # Update accessory
    $updateAccBody = @{ 
      nom = "GPS Premium"
      description = "Advanced navigation"
      prix = 249.99
      type = "ELECTRONIQUE"
    } | ConvertTo-Json
    $accUpdateResp = Invoke-Api PUT ("{0}/api/v1/garages/{1}/vehicules/{2}/accessoires/{3}" -f $BaseUrl, $garageId, $firstVeh.id, $accJson.id) $updateAccBody
    $md += ("- Accessory Update Status: {0}" -f $accUpdateResp.StatusCode)
  } else {
    $md += ("- Accessory creation failed: {0}" -f $accCreateResp.StatusCode)
  }
} else {
  $md += "- No vehicles available to attach accessories"
}
$md += ""
$md += "## Garage Search by Fuel and Accessory"
$searchResp = Invoke-Api GET ("{0}/api/v1/garages/search?typeCarburant=ESSENCE&accessoireNom=GPS" -f $BaseUrl)
if ($searchResp.StatusCode -eq 200) {
  $searchJson = $searchResp.Content | ConvertFrom-Json
  $md += ("- Search Result Count: {0}" -f (($searchJson | Measure-Object).Count))
  if (($searchJson | Measure-Object).Count -gt 0) {
    $md += ("- First Result Garage ID: {0}" -f $searchJson[0].id)
    $md += ("- First Result Garage Name: {0}" -f $searchJson[0].name)
  }
} else {
  $md += ("- Search request failed: {0}" -f $searchResp.StatusCode)
}
$md += ""
$md += "## Next Steps"
$md += "- Inspect Kafka UI at http://localhost:8090 for topic vehicule.created."
$md += "- Verify app logs for consumer acknowledgments."
$md += "- Adjust MaxAttempt if capacity is higher than expected."

Set-Content -Path $ReportPath -Value ($md -join "`n") -Encoding UTF8
Write-Host ("Report written to {0}" -f (Resolve-Path $ReportPath)) -ForegroundColor Cyan