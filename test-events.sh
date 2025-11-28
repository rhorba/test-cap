#!/bin/bash
# Script de test manuel du système de publication/consommation d'événements

echo "🚀 Test du système de publication/consommation d'événements"
echo "==========================================================="
echo ""

# 1. Démarrer PostgreSQL
echo "1️⃣  Démarrage de PostgreSQL..."
docker-compose up -d postgres

# Attendre que PostgreSQL soit prêt
echo "⏳ Attente du démarrage de PostgreSQL..."
sleep 5

# 2. Lancer l'application en mode développement (avec les logs visibles)
echo ""
echo "2️⃣  Démarrage de l'application Spring Boot..."
echo "   📝 Surveillez les logs pour voir les événements !"
echo ""

# Lancer l'application (elle affichera les logs dans la console)
mvn spring-boot:run &
APP_PID=$!

# Attendre que l'application démarre
echo "⏳ Attente du démarrage de l'application..."
sleep 10

# 3. Créer un véhicule pour déclencher l'événement
echo ""
echo "3️⃣  Création d'un véhicule pour déclencher l'événement..."
echo ""

curl -X POST http://localhost:8080/api/v1/garages/550e8400-e29b-41d4-a716-446655440001/vehicules \
  -H "Content-Type: application/json" \
  -d '{
    "modeleId": "650e8400-e29b-41d4-a716-446655440001",
    "brand": "Renault Zoe E-Tech",
    "anneeFabrication": 2024,
    "typeCarburant": "ELECTRIQUE"
  }'

echo ""
echo ""
echo "✅ Véhicule créé !"
echo ""
echo "📊 Dans les logs de l'application, vous devriez voir :"
echo "   📢 [PUBLISHER] Publication d'un événement domaine: VehiculeCreatedEvent"
echo "   🚗 [CONSUMER] Réception d'un événement VehiculeCreatedEvent"
echo "   ⚙️  Traitement de l'événement en cours..."
echo "   📧 Envoi de notification"
echo "   📊 Mise à jour des statistiques"
echo "   🔄 Synchronisation avec système externe"
echo "   ✅ Événement traité avec succès"
echo ""
echo "Appuyez sur CTRL+C pour arrêter l'application"

# Garder le script en cours d'exécution
wait $APP_PID
