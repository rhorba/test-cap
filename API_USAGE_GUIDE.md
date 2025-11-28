# 📖 Guide d'Utilisation de l'API - Renault Garage Service

## 🚀 Démarrage Rapide

### 1. Démarrer PostgreSQL avec Docker

```bash
docker-compose up -d
```

### 2. Vérifier que PostgreSQL est démarré

```bash
docker ps
```

### 3. Compiler le projet

```bash
mvn clean install
```

### 4. Lancer l'application

```bash
mvn spring-boot:run
```

Ou créer un JAR et l'exécuter:

```bash
mvn clean package
java -jar target/garage-service-1.0.0.jar
```

### 5. L'application sera disponible sur

- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs (OpenAPI):** http://localhost:8080/api-docs
- **Actuator Health:** http://localhost:8080/actuator/health

### 6. Pour arrêter PostgreSQL

```bash
docker-compose down
```

---

## 📡 Endpoints de l'API

### 1. CRÉER UN GARAGE

**Endpoint:** `POST /api/v1/garages`

**Exemple cURL:**

```bash
curl -X POST http://localhost:8080/api/v1/garages \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Renault Paris Centre",
    "address": {
      "rue": "123 Avenue des Champs-Élysées",
      "ville": "Paris",
      "codePostal": "75008",
      "pays": "France"
    },
    "telephone": "+33140256789",
    "email": "paris.centre@renault.fr",
    "horairesOuverture": {
      "MONDAY": [
        {
          "startTime": "08:00:00",
          "endTime": "12:00:00"
        },
        {
          "startTime": "14:00:00",
          "endTime": "18:00:00"
        }
      ],
      "TUESDAY": [
        {
          "startTime": "08:00:00",
          "endTime": "18:00:00"
        }
      ],
      "WEDNESDAY": [
        {
          "startTime": "08:00:00",
          "endTime": "18:00:00"
        }
      ],
      "THURSDAY": [
        {
          "startTime": "08:00:00",
          "endTime": "18:00:00"
        }
      ],
      "FRIDAY": [
        {
          "startTime": "08:00:00",
          "endTime": "18:00:00"
        }
      ]
    }
  }'
```

**Réponse attendue (201 Created):**

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Renault Paris Centre",
  "address": {
    "rue": "123 Avenue des Champs-Élysées",
    "ville": "Paris",
    "codePostal": "75008",
    "pays": "France"
  },
  "telephone": "+33140256789",
  "email": "paris.centre@renault.fr",
  "horairesOuverture": {...},
  "nombreVehicules": 0,
  "capaciteRestante": 50,
  "createdAt": "2024-11-28T10:30:00",
  "updatedAt": "2024-11-28T10:30:00"
}
```

---

### 2. RÉCUPÉRER UN GARAGE PAR ID

**Endpoint:** `GET /api/v1/garages/{garage_id}`

**Exemple cURL:**

```bash
curl -X GET http://localhost:8080/api/v1/garages/a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**Réponse attendue (200 OK):**

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Renault Paris Centre",
  "address": {...},
  "telephone": "+33140256789",
  "email": "paris.centre@renault.fr",
  "horairesOuverture": {...},
  "nombreVehicules": 0,
  "capaciteRestante": 50,
  "createdAt": "2024-11-28T10:30:00",
  "updatedAt": "2024-11-28T10:30:00"
}
```

---

### 3. LISTER TOUS LES GARAGES (avec pagination et tri)

**Endpoint:** `GET /api/v1/garages`

**Paramètres de requête:**
- `page`: Numéro de page (défaut: 0)
- `size`: Nombre d'éléments par page (défaut: 20)
- `sortBy`: Champ de tri (défaut: name)
- `direction`: Direction du tri (ASC/DESC, défaut: ASC)

**Exemples cURL:**

```bash
# Page 0, 20 éléments par page, tri par nom ascendant
curl -X GET "http://localhost:8080/api/v1/garages?page=0&size=20&sortBy=name&direction=ASC"

# Tri par ville
curl -X GET "http://localhost:8080/api/v1/garages?page=0&size=10&sortBy=address.ville&direction=ASC"
```

**Réponse attendue (200 OK):**

```json
{
  "garages": [
    {
      "id": "...",
      "name": "Renault Lyon Centre",
      ...
    },
    {
      "id": "...",
      "name": "Renault Paris Centre",
      ...
    }
  ],
  "currentPage": 0,
  "totalPages": 1,
  "totalElements": 2
}
```

---

### 4. METTRE À JOUR UN GARAGE

**Endpoint:** `PUT /api/v1/garages/{garage_id}`

**Exemple cURL:**

```bash
curl -X PUT http://localhost:8080/api/v1/garages/a1b2c3d4-e5f6-7890-abcd-ef1234567890 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Renault Paris Centre - Nouveau Nom",
    "telephone": "+33140259999"
  }'
```

**Note:** Seuls les champs fournis seront mis à jour

**Réponse attendue (200 OK):**

```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "name": "Renault Paris Centre - Nouveau Nom",
  "address": {...},
  "telephone": "+33140259999",
  "email": "paris.centre@renault.fr",
  ...
}
```

---

### 5. SUPPRIMER UN GARAGE

**Endpoint:** `DELETE /api/v1/garages/{garage_id}`

**Exemple cURL:**

```bash
curl -X DELETE http://localhost:8080/api/v1/garages/a1b2c3d4-e5f6-7890-abcd-ef1234567890
```

**Réponse attendue (204 No Content):** Aucun contenu

---

## ⚠️ Gestion des Erreurs

### Erreur de validation (400 Bad Request)

**Exemple - Créer un garage avec un email invalide:**

```bash
curl -X POST http://localhost:8080/api/v1/garages \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Garage",
    "address": {
      "rue": "123 Rue",
      "ville": "Paris",
      "codePostal": "75001",
      "pays": "France"
    },
    "telephone": "+33123456789",
    "email": "email-invalide",
    "horairesOuverture": {}
  }'
```

**Réponse (400 Bad Request):**

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Erreur de validation des données",
  "errors": {
    "email": "Format d'email invalide"
  },
  "timestamp": "2024-11-28T10:30:00"
}
```

### Garage non trouvé (404 Not Found)

**Exemple:**

```bash
curl -X GET http://localhost:8080/api/v1/garages/00000000-0000-0000-0000-000000000000
```

**Réponse (404 Not Found):**

```json
{
  "code": "GARAGE_NOT_FOUND",
  "message": "Garage non trouvé avec l'ID: 00000000-0000-0000-0000-000000000000",
  "timestamp": "2024-11-28T10:30:00"
}
```

---

## 📚 Documentation Swagger

**URL:** http://localhost:8080/swagger-ui.html

Cette interface vous permet de :
- ✅ Visualiser tous les endpoints disponibles
- ✅ Tester les API directement depuis le navigateur
- ✅ Voir les schémas de requêtes et réponses
- ✅ Explorer la documentation complète

**URL de la spécification OpenAPI JSON:**  
http://localhost:8080/api-docs

---

## 🏥 Health Check (Spring Actuator)

**Endpoint:** `GET /actuator/health`

**Exemple cURL:**

```bash
curl -X GET http://localhost:8080/actuator/health
```

**Réponse attendue (200 OK):**

```json
{
  "status": "UP"
}
```

### Endpoints Actuator disponibles:

- `/actuator/health` - État de santé de l'application
- `/actuator/info` - Informations sur l'application
- `/actuator/metrics` - Métriques de l'application

---

## 🧪 Tests

### Exécuter tous les tests

```bash
mvn test
```

### Exécuter uniquement les tests unitaires

```bash
mvn test -Dtest=*Test
```

### Exécuter uniquement les tests d'intégration

```bash
mvn test -Dtest=*IntegrationTest
```

### Générer un rapport de couverture de code

```bash
mvn clean test jacoco:report
```

Le rapport sera disponible dans: `target/site/jacoco/index.html`

---

## 📁 Structure du Projet

```
renault-garage-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/renault/garage/
│   │   │       ├── domain/
│   │   │       │   ├── model/
│   │   │       │   ├── repository/
│   │   │       │   └── exception/
│   │   │       ├── application/
│   │   │       │   ├── service/
│   │   │       │   ├── dto/
│   │   │       │   └── mapper/
│   │   │       ├── infrastructure/
│   │   │       │   ├── persistence/
│   │   │       │   ├── rest/
│   │   │       │   └── config/
│   │   │       └── GarageMicroserviceApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   │           ├── V1__create_garage_tables.sql
│   │           └── V2__insert_sample_data.sql
│   └── test/
│       └── java/
│           └── com/renault/garage/
│               ├── domain/
│               ├── application/
│               └── infrastructure/
├── docker-compose.yml
├── pom.xml
├── README.md
└── API_USAGE_GUIDE.md
```

---

## 🏗️ Principes d'Architecture

### 1. Architecture Hexagonale (Ports & Adapters)
- **Domain Layer:** Logique métier pure
- **Application Layer:** Use cases et orchestration
- **Infrastructure Layer:** Détails techniques (BDD, API REST)

### 2. Domain-Driven Design (DDD)
- **Entités:** Garage, Vehicule, Accessoire
- **Value Objects:** Address, OpeningTime
- **Aggregate Root:** Garage

### 3. Clean Architecture
- ✅ Indépendance des frameworks
- ✅ Testabilité maximale
- ✅ Séparation des préoccupations

### 4. RESTful API Design
- ✅ Ressources bien définies
- ✅ Verbes HTTP appropriés
- ✅ Codes de statut corrects
- ✅ HATEOAS (optionnel)

### 5. Validation
- ✅ Bean Validation (Jakarta)
- ✅ Validation métier dans le domaine
- ✅ Gestion globale des erreurs

---

## 🚀 Évolutions Futures Possibles

1. ✨ Ajouter l'authentification et l'autorisation (OAuth2/JWT)
2. 💾 Implémenter un système de cache (Redis)
3. 📨 Ajouter des événements domain (Event Sourcing)
4. 🔄 Implémenter CQRS pour séparer lecture/écriture
5. 🔔 Ajouter des webhooks pour notifier les changements
6. 🔍 Implémenter la recherche full-text (Elasticsearch)
7. 📌 Ajouter le versioning d'API (v2, v3...)
8. 🎯 Implémenter GraphQL en parallèle de REST
9. 📊 Ajouter des métriques avancées (Prometheus/Grafana)
10. 🛡️ Implémenter Circuit Breaker (Resilience4j)

---

## 🐳 Docker & PostgreSQL

### Accéder à pgAdmin

1. Ouvrir dans le navigateur: http://localhost:5050
2. Se connecter avec:
   - **Email:** admin@renault.fr
   - **Password:** admin123

3. Ajouter un nouveau serveur:
   - **Name:** Renault Garage DB
   - **Host:** postgres
   - **Port:** 5432
   - **Database:** renault_garage_db
   - **Username:** renault_user
   - **Password:** renault_pass

### Commandes Docker utiles

```bash
# Démarrer les services
docker-compose up -d

# Voir les logs
docker-compose logs -f

# Arrêter les services
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v

# Redémarrer un service
docker-compose restart postgres
```

---

**📞 Support:** Pour toute question, consultez la documentation complète dans `README.md`

**✅ Projet prêt pour la production!** 🚀
