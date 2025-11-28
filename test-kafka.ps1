# Script PowerShell pour tester le système Kafka

Write-Host "🚀 Test du système Kafka - Événements de Véhicules" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Green
Write-Host ""

# Étape 1: Vérifier Docker
Write-Host "1️⃣  Vérification de Docker..." -ForegroundColor Cyan
try {
    docker --version | Out-Null
    Write-Host "   ✅ Docker est installé" -ForegroundColor Green
} catch {
    Write-Host "   ❌ Docker n'est pas installé ou démarré" -ForegroundColor Red
    exit 1
}

# Étape 2: Démarrer l'infrastructure
Write-Host ""
Write-Host "2️⃣  Démarrage de l'infrastructure (Kafka + PostgreSQL)..." -ForegroundColor Cyan
docker-compose up -d

Write-Host "   ⏳ Attente du démarrage des services (30 secondes)..." -ForegroundColor Yellow
Start-Sleep -Seconds 30

# Vérifier que les services sont démarrés
$services = @("renault_zookeeper", "renault_kafka", "renault_postgres")
foreach ($service in $services) {
    $status = docker ps --filter "name=$service" --format "{{.Names}}: {{.Status}}"
    if ($status) {
        Write-Host "   ✅ $status" -ForegroundColor Green
    } else {
        Write-Host "   ❌ $service n'est pas démarré" -ForegroundColor Red
    }
}

# Étape 3: Afficher les URLs importantes
Write-Host ""
Write-Host "3️⃣  URLs importantes:" -ForegroundColor Cyan
Write-Host "   📊 Kafka UI:  http://localhost:8090" -ForegroundColor White
Write-Host "   🗄️  pgAdmin:  http://localhost:5050" -ForegroundColor White
Write-Host "   🚀 API:      http://localhost:8080" -ForegroundColor White
Write-Host "   📚 Swagger:  http://localhost:8080/swagger-ui.html" -ForegroundColor White

# Étape 4: Démarrer l'application Spring Boot
Write-Host ""
Write-Host "4️⃣  Démarrage de l'application Spring Boot..." -ForegroundColor Cyan
Write-Host "   📝 Surveillez les logs pour voir les événements Kafka !" -ForegroundColor Yellow
Write-Host ""

$job = Start-Job -ScriptBlock {
    Set-Location "c:\Users\rrhor\OneDrive\Bureau\test"
    mvn spring-boot:run
}

Write-Host "   ⏳ Attente du démarrage de l'application (20 secondes)..." -ForegroundColor Yellow
Start-Sleep -Seconds 20

# Étape 5: Créer un véhicule pour déclencher l'événement Kafka
Write-Host ""
Write-Host "5️⃣  Création d'un véhicule (déclenche événement Kafka)..." -ForegroundColor Cyan
Write-Host ""

$body = @{
    modeleId = "650e8400-e29b-41d4-a716-446655440001"
    brand = "Renault Zoe E-Tech Electric"
    anneeFabrication = 2024
    typeCarburant = "ELECTRIQUE"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/garages/550e8400-e29b-41d4-a716-446655440001/vehicules" `
        -Method POST `
        -ContentType "application/json" `
        -Body $body
    
    Write-Host "   ✅ Véhicule créé avec succès !" -ForegroundColor Green
    Write-Host "      ID: $($response.id)" -ForegroundColor White
    Write-Host "      Marque: $($response.brand)" -ForegroundColor White
    Write-Host "      Année: $($response.anneeFabrication)" -ForegroundColor White
    Write-Host ""
    
} catch {
    Write-Host "   ❌ Erreur lors de la création du véhicule" -ForegroundColor Red
    Write-Host "      $_" -ForegroundColor Red
    Write-Host ""
}

# Étape 6: Afficher les logs de l'application
Write-Host "6️⃣  Logs de l'application (dernières 40 lignes):" -ForegroundColor Cyan
Write-Host "──────────────────────────────────────────────────────────" -ForegroundColor Gray
Start-Sleep -Seconds 2
Receive-Job -Job $job -Keep | Select-Object -Last 40

# Étape 7: Instructions pour Kafka UI
Write-Host ""
Write-Host "7️⃣  Vérification dans Kafka UI:" -ForegroundColor Cyan
Write-Host "   1. Ouvrez http://localhost:8090" -ForegroundColor White
Write-Host "   2. Cliquez sur 'Topics'" -ForegroundColor White
Write-Host "   3. Sélectionnez 'vehicule.created'" -ForegroundColor White
Write-Host "   4. Onglet 'Messages' pour voir l'événement" -ForegroundColor White
Write-Host ""

Write-Host "📊 Dans les logs, vous devriez voir :" -ForegroundColor Cyan
Write-Host "   📢 [KAFKA PUBLISHER] Publication de l'événement" -ForegroundColor White
Write-Host "   ✅ [KAFKA] Événement publié avec succès - partition: X, offset: Y" -ForegroundColor White
Write-Host "   🚗 [KAFKA CONSUMER] Réception d'un événement VehiculeCreatedEvent" -ForegroundColor White
Write-Host "   📍 Partition: X, Offset: Y" -ForegroundColor White
Write-Host "   ⚙️  [KAFKA] Traitement de l'événement en cours..." -ForegroundColor White
Write-Host "   📧 [Notification] Envoi d'email" -ForegroundColor White
Write-Host "   📊 [Statistiques] Mise à jour" -ForegroundColor White
Write-Host "   🔄 [Synchronisation] Mise à jour du système externe" -ForegroundColor White
Write-Host "   🔍 [Indexation] Indexation dans Elasticsearch" -ForegroundColor White
Write-Host "   ✅ [KAFKA] Événement traité avec succès" -ForegroundColor White
Write-Host "   ✅ Message acquitté" -ForegroundColor White
Write-Host ""

# Étape 8: Commandes utiles
Write-Host "🛠️  Commandes utiles:" -ForegroundColor Cyan
Write-Host "   • Voir les logs en temps réel:" -ForegroundColor Yellow
Write-Host "     Receive-Job -Job `$job -Keep" -ForegroundColor Gray
Write-Host ""
Write-Host "   • Arrêter l'application:" -ForegroundColor Yellow
Write-Host "     Stop-Job -Job `$job; Remove-Job -Job `$job" -ForegroundColor Gray
Write-Host ""
Write-Host "   • Topics Kafka:" -ForegroundColor Yellow
Write-Host "     docker exec renault_kafka kafka-topics --list --bootstrap-server localhost:9092" -ForegroundColor Gray
Write-Host ""
Write-Host "   • Consumer groups:" -ForegroundColor Yellow
Write-Host "     docker exec renault_kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list" -ForegroundColor Gray
Write-Host ""
Write-Host "   • Consommer des messages (debug):" -ForegroundColor Yellow
Write-Host "     docker exec renault_kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic vehicule.created --from-beginning" -ForegroundColor Gray
Write-Host ""

# Stocker le job pour l'utilisateur
$global:SpringBootJob = $job
Write-Host "✅ Job Spring Boot sauvegardé dans `$SpringBootJob" -ForegroundColor Green
Write-Host ""

Write-Host "🎉 Test Kafka terminé ! Vérifiez les logs ci-dessus." -ForegroundColor Green
Write-Host ""
Write-Host "💡 Astuce: Ouvrez Kafka UI pour voir les messages en temps réel:" -ForegroundColor Yellow
Write-Host "   start http://localhost:8090" -ForegroundColor Gray
Write-Host ""
