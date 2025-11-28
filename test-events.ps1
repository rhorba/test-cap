# Script PowerShell de test du système de publication/consommation d'événements

Write-Host "🚀 Test du système de publication/consommation d'événements" -ForegroundColor Green
Write-Host "===========================================================" -ForegroundColor Green
Write-Host ""

# 1. Démarrer PostgreSQL
Write-Host "1️⃣  Démarrage de PostgreSQL..." -ForegroundColor Cyan
docker-compose up -d postgres

# Attendre que PostgreSQL soit prêt
Write-Host "⏳ Attente du démarrage de PostgreSQL..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# 2. Lancer l'application en mode développement
Write-Host ""
Write-Host "2️⃣  Démarrage de l'application Spring Boot..." -ForegroundColor Cyan
Write-Host "   📝 Surveillez les logs pour voir les événements !" -ForegroundColor Yellow
Write-Host ""

# Lancer l'application dans un job en arrière-plan
$job = Start-Job -ScriptBlock {
    Set-Location "c:\Users\rrhor\OneDrive\Bureau\test"
    mvn spring-boot:run
}

# Attendre que l'application démarre
Write-Host "⏳ Attente du démarrage de l'application..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

# 3. Créer un véhicule pour déclencher l'événement
Write-Host ""
Write-Host "3️⃣  Création d'un véhicule pour déclencher l'événement..." -ForegroundColor Cyan
Write-Host ""

$body = @{
    modeleId = "650e8400-e29b-41d4-a716-446655440001"
    brand = "Renault Zoe E-Tech"
    anneeFabrication = 2024
    typeCarburant = "ELECTRIQUE"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/garages/550e8400-e29b-41d4-a716-446655440001/vehicules" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body
    
    Write-Host ""
    Write-Host "✅ Véhicule créé avec succès !" -ForegroundColor Green
    Write-Host "   ID: $($response.id)" -ForegroundColor White
    Write-Host "   Marque: $($response.brand)" -ForegroundColor White
    Write-Host ""
} catch {
    Write-Host ""
    Write-Host "❌ Erreur lors de la création du véhicule: $_" -ForegroundColor Red
    Write-Host ""
}

Write-Host "📊 Dans les logs de l'application, vous devriez voir :" -ForegroundColor Cyan
Write-Host "   📢 [PUBLISHER] Publication d'un événement domaine: VehiculeCreatedEvent" -ForegroundColor White
Write-Host "   🚗 [CONSUMER] Réception d'un événement VehiculeCreatedEvent" -ForegroundColor White
Write-Host "   ⚙️  Traitement de l'événement en cours..." -ForegroundColor White
Write-Host "   📧 Envoi de notification" -ForegroundColor White
Write-Host "   📊 Mise à jour des statistiques" -ForegroundColor White
Write-Host "   🔄 Synchronisation avec système externe" -ForegroundColor White
Write-Host "   ✅ Événement traité avec succès" -ForegroundColor White
Write-Host ""

Write-Host "💡 Pour voir les logs de l'application, utilisez:" -ForegroundColor Yellow
Write-Host "   Receive-Job -Job `$job -Keep" -ForegroundColor Gray
Write-Host ""
Write-Host "🛑 Pour arrêter l'application:" -ForegroundColor Yellow
Write-Host "   Stop-Job -Job `$job; Remove-Job -Job `$job" -ForegroundColor Gray
Write-Host ""

# Afficher les derniers logs
Write-Host "📜 Derniers logs de l'application:" -ForegroundColor Cyan
Write-Host "-----------------------------------" -ForegroundColor Gray
Start-Sleep -Seconds 2
Receive-Job -Job $job -Keep | Select-Object -Last 30

# Stocker le job ID pour que l'utilisateur puisse l'arrêter
$global:SpringBootJob = $job
Write-Host ""
Write-Host "✅ Job sauvegardé dans `$SpringBootJob" -ForegroundColor Green
