# ⚡ Quick Start - Test du Système d'Événements

## 🎯 Objectif
Tester rapidement le système de publication/consommation d'événements en 5 minutes.

---

## 🚀 Étape 1 : Démarrer l'Infrastructure (30 secondes)

### Option A : PowerShell (Windows)
```powershell
cd c:\Users\rrhor\OneDrive\Bureau\test
docker-compose up -d postgres
```

### Option B : Bash (Linux/Mac)
```bash
cd /path/to/test
docker-compose up -d postgres
```

**Vérification :**
```bash
docker ps | findstr postgres
# Doit afficher le container postgres running
```

---

## 🚀 Étape 2 : Démarrer l'Application (1 minute)

### PowerShell
```powershell
mvn spring-boot:run
```

**Attendre le message :**
```
Started GarageManagementApplication in X seconds
```

---

## 🚀 Étape 3 : Tester la Publication d'Événement (10 secondes)

### PowerShell
```powershell
$body = @{
    modeleId = "650e8400-e29b-41d4-a716-446655440001"
    brand = "Renault Zoe E-Tech Electric"
    anneeFabrication = 2024
    typeCarburant = "ELECTRIQUE"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/garages/550e8400-e29b-41d4-a716-446655440001/vehicules" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body
```

### cURL (Bash)
```bash
curl -X POST http://localhost:8080/api/v1/garages/550e8400-e29b-41d4-a716-446655440001/vehicules \
  -H "Content-Type: application/json" \
  -d '{
    "modeleId": "650e8400-e29b-41d4-a716-446655440001",
    "brand": "Renault Zoe E-Tech Electric",
    "anneeFabrication": 2024,
    "typeCarburant": "ELECTRIQUE"
  }'
```

---

## 🚀 Étape 4 : Observer les Logs (Immédiat)

Dans la console où l'application tourne, vous devriez voir :

```log
✅ PUBLICATION (Thread principal) :
[INFO] VehiculeService - 🚗 Création d'un nouveau véhicule pour le garage 550e8400...
[INFO] SpringDomainEventPublisher - 📢 Publication d'un événement domaine: VehiculeCreatedEvent
[INFO] VehiculeService - ✅ Véhicule créé avec succès: [UUID]

⏳ CONSOMMATION ASYNCHRONE (Thread event-consumer-X) :
[INFO] VehiculeEventListener - 🚗 [CONSUMER] Réception d'un événement VehiculeCreatedEvent
[INFO] VehiculeEventListener -    → Véhicule ID: [UUID]
[INFO] VehiculeEventListener -    → Garage ID: 550e8400-e29b-41d4-a716-446655440001
[INFO] VehiculeEventListener -    → Marque: Renault Zoe E-Tech Electric
[INFO] VehiculeEventListener -    → Année: 2024
[INFO] VehiculeEventListener -    → Carburant: ELECTRIQUE
[INFO] VehiculeEventListener - ⚙️  Traitement de l'événement en cours...
[INFO] VehiculeEventListener - 📧 Envoi de notification pour le nouveau véhicule
[INFO] VehiculeEventListener - 📊 Mise à jour des statistiques: +1 véhicule
[INFO] VehiculeEventListener - 🔄 Synchronisation avec le système externe
[INFO] VehiculeEventListener - ✅ Événement traité avec succès
```

---

## ✅ Vérification du Succès

### 1. Réponse API (Immédiate)
```json
{
  "id": "uuid-du-vehicule",
  "garageId": "550e8400-e29b-41d4-a716-446655440001",
  "modeleId": "650e8400-e29b-41d4-a716-446655440001",
  "brand": "Renault Zoe E-Tech Electric",
  "anneeFabrication": 2024,
  "typeCarburant": "ELECTRIQUE",
  "createdAt": "2024-11-28T10:30:00"
}
```

### 2. Logs de Publication
✅ Message `📢 Publication d'un événement domaine`

### 3. Logs de Consommation
✅ Message `🚗 [CONSUMER] Réception d'un événement`  
✅ Message `⚙️  Traitement de l'événement en cours...`  
✅ Message `✅ Événement traité avec succès`

### 4. Base de Données
```sql
SELECT * FROM vehicules ORDER BY created_at DESC LIMIT 1;
-- Devrait montrer le véhicule créé
```

---

## 🔧 Scripts Automatiques

### PowerShell (Windows)
```powershell
.\test-events.ps1
```

### Bash (Linux/Mac)
```bash
./test-events.sh
```

Ces scripts automatisent :
1. ✅ Démarrage de PostgreSQL
2. ✅ Démarrage de l'application
3. ✅ Création d'un véhicule
4. ✅ Affichage des logs

---

## 🐛 Dépannage

### Problème 1 : PostgreSQL ne démarre pas
```powershell
# Vérifier les logs Docker
docker logs renault-postgres

# Redémarrer
docker-compose restart postgres
```

### Problème 2 : L'application ne démarre pas
```powershell
# Vérifier la compilation
mvn clean compile

# Vérifier le port 8080
netstat -ano | findstr :8080
```

### Problème 3 : L'événement n'est pas consommé
- ✅ Vérifier que `@EnableAsync` est dans `AsyncConfig.java`
- ✅ Vérifier les logs pour des exceptions
- ✅ Attendre 1-2 secondes (traitement asynchrone)

### Problème 4 : Erreur 404 Not Found
```powershell
# Vérifier que le garage existe
curl http://localhost:8080/api/v1/garages/550e8400-e29b-41d4-a716-446655440001
```

---

## 📊 Indicateurs de Succès

| Indicateur | Attendu | Vérification |
|------------|---------|--------------|
| HTTP Status | `201 Created` | Réponse API |
| Temps de réponse | < 200ms | Logs ou Postman |
| Événement publié | ✅ | Log `📢 Publication` |
| Événement consommé | ✅ | Log `🚗 [CONSUMER]` |
| Thread asynchrone | `event-consumer-X` | Nom du thread dans les logs |
| Véhicule en BDD | ✅ | Query SQL |

---

## 🎯 Cas de Test Additionnels

### Test 2 : Créer plusieurs véhicules rapidement
```powershell
1..5 | ForEach-Object {
    $body = @{
        modeleId = "650e8400-e29b-41d4-a716-446655440001"
        brand = "Renault Megane E-Tech $($_)"
        anneeFabrication = 2024
        typeCarburant = "ELECTRIQUE"
    } | ConvertTo-Json
    
    Invoke-RestMethod -Uri "http://localhost:8080/api/v1/garages/550e8400-e29b-41d4-a716-446655440001/vehicules" `
        -Method POST -ContentType "application/json" -Body $body
}
```

**Résultat attendu :**
- 5 véhicules créés
- 5 événements publiés
- 5 événements consommés (possiblement en parallèle)

### Test 3 : Vérifier la capacité max (50 véhicules)
```powershell
# Créer 50 véhicules
1..50 | ForEach-Object {
    # ... même code
}

# Le 51ème devrait échouer
# HTTP 400 : "Capacité maximale atteinte"
```

---

## 📚 Documentation Complète

- 📖 **EVENTS.md** - Architecture et détails techniques
- 📖 **README.md** - Guide complet du projet
- 📖 **EVENT_SYSTEM_DELIVERY.md** - Récapitulatif de livraison
- 📖 **API_USAGE_GUIDE.md** - Exemples d'utilisation de l'API

---

## ✅ Checklist de Test

- [ ] ✅ PostgreSQL démarré
- [ ] ✅ Application démarrée
- [ ] ✅ Véhicule créé via API
- [ ] ✅ Réponse HTTP 201
- [ ] ✅ Log de publication visible
- [ ] ✅ Log de consommation visible
- [ ] ✅ Traitement asynchrone confirmé (thread different)
- [ ] ✅ Notifications simulées (logs)
- [ ] ✅ Statistiques mises à jour (logs)
- [ ] ✅ Synchronisation externe (logs)
- [ ] ✅ Véhicule présent en BDD

---

## 🎉 Félicitations !

Si tous les indicateurs sont au vert, votre système de publication/consommation d'événements fonctionne parfaitement ! 🚀

---

**⏱️ Temps total : 5 minutes**  
**🎯 Difficulté : Débutant**  
**📦 Prérequis : Docker, Java 17, Maven**
