# 🚗 Renault Garage Management Service

Microservice de gestion des garages, véhicules et accessoires pour le réseau Renault.

## 📋 Table des matières

- [Contexte et Objectifs](#contexte-et-objectifs)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation et Démarrage](#installation-et-démarrage)
- [API Endpoints](#api-endpoints)
- [Modèle de Données](#modèle-de-données)
- [Tests](#tests)
- [Documentation API](#documentation-api)
- [Contraintes Métiers](#contraintes-métiers)
- [Système d'Événements](#système-dévénements)
- [Gestion des Erreurs](#gestion-des-erreurs)

## 🎯 Contexte et Objectifs

Renault souhaite développer un microservice pour gérer les informations relatives aux garages affiliés à son réseau. Ce système doit permettre :

### Fonctionnalités Principales

1. **Gestion des Garages**
   - Création, modification et suppression de garages
   - Récupération d'un garage spécifique par ID
   - Liste paginée avec tri (par nom, ville, etc.)

2. **Gestion des Véhicules**
   - Ajout, modification et suppression de véhicules
   - Lister les véhicules d'un garage
   - Lister tous les véhicules d'un modèle donné

3. **Gestion des Accessoires**
   - Ajout, modification et suppression d'accessoires
   - Lister les accessoires d'un véhicule

4. **Recherches Avancées**
   - Rechercher des garages par type de véhicule
   - Rechercher par disponibilité d'accessoires

## 🏗️ Architecture

### Architecture Hexagonale (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────┐
│                    REST API Layer                        │
│              (Controllers, Exception Handlers)            │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                  Application Layer                       │
│         (Services, DTOs, Mappers, Use Cases)            │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                    Domain Layer                          │
│     (Entities, Value Objects, Domain Services)          │
│            (Business Logic & Rules)                      │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│              Infrastructure Layer                        │
│    (JPA Repositories, Database Adapters, Config)        │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                    PostgreSQL                            │
└─────────────────────────────────────────────────────────┘
```

### Principes de Design

- **Domain-Driven Design (DDD)**: Modélisation centrée sur le domaine métier
- **Clean Architecture**: Indépendance des frameworks et de l'infrastructure
- **SOLID Principles**: Code maintenable et extensible
- **Testabilité**: Tests unitaires et d'intégration complets

## 🔧 Prérequis

- **Java 17** ou supérieur
- **Maven 3.8+**
- **Docker** et **Docker Compose** (pour PostgreSQL)
- **Git**

## 🚀 Installation et Démarrage

### 1. Cloner le repository

```bash
git clone https://github.com/renault/garage-service.git
cd garage-service
```

### 2. Démarrer PostgreSQL avec Docker

```bash
docker-compose up -d
```

Vérifier que PostgreSQL est bien démarré :

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

Ou créer un JAR et l'exécuter :

```bash
mvn clean package
java -jar target/garage-service-1.0.0.jar
```

### 5. Vérifier que l'application est démarrée

```bash
curl http://localhost:8080/actuator/health
```

Réponse attendue :
```json
{"status":"UP"}
```

## 📡 API Endpoints

### Gestion des Garages

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/v1/garages` | Créer un nouveau garage |
| GET | `/api/v1/garages/{id}` | Récupérer un garage par ID |
| GET | `/api/v1/garages` | Lister tous les garages (paginé) |
| PUT | `/api/v1/garages/{id}` | Mettre à jour un garage |
| DELETE | `/api/v1/garages/{id}` | Supprimer un garage |

### Paramètres de pagination et tri

- `page` : Numéro de page (défaut: 0)
- `size` : Nombre d'éléments par page (défaut: 20)
- `sortBy` : Champ de tri (défaut: name)
- `direction` : Direction du tri (ASC ou DESC)

**Exemple :**
```bash
GET /api/v1/garages?page=0&size=10&sortBy=name&direction=ASC
```

### Exemples de requêtes

#### Créer un garage

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
        {"startTime": "08:00:00", "endTime": "12:00:00"},
        {"startTime": "14:00:00", "endTime": "18:00:00"}
      ],
      "TUESDAY": [
        {"startTime": "08:00:00", "endTime": "18:00:00"}
      ]
    }
  }'
```

#### Récupérer un garage

```bash
curl -X GET http://localhost:8080/api/v1/garages/{garage_id}
```

#### Mettre à jour un garage

```bash
curl -X PUT http://localhost:8080/api/v1/garages/{garage_id} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Renault Paris Centre - Nouveau Nom",
    "telephone": "+33140259999"
  }'
```

## 💾 Modèle de Données

### Entités Principales

#### Garage
```java
{
  "id": "UUID",
  "name": "String (required)",
  "address": {
    "rue": "String (required)",
    "ville": "String (required)",
    "codePostal": "String (required)",
    "pays": "String (required)"
  },
  "telephone": "String (required)",
  "email": "String (required, unique)",
  "horairesOuverture": {
    "MONDAY": [
      {"startTime": "LocalTime", "endTime": "LocalTime"}
    ]
  },
  "nombreVehicules": "int (read-only)",
  "capaciteRestante": "int (read-only)",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

#### Vehicule
```java
{
  "id": "UUID",
  "garageId": "UUID",
  "modeleId": "UUID",
  "brand": "String (required)",
  "anneeFabrication": "int (required)",
  "typeCarburant": "ESSENCE | DIESEL | ELECTRIQUE | HYBRIDE | GPL",
  "createdAt": "LocalDateTime",
  "updatedAt": "LocalDateTime"
}
```

#### Accessoire
```java
{
  "id": "UUID",
  "vehiculeId": "UUID",
  "nom": "String (required)",
  "description": "String",
  "prix": "BigDecimal (required, >= 0)",
  "type": "INTERIEUR | EXTERIEUR | ELECTRONIQUE | SECURITE | CONFORT",
  "createdAt": "LocalDateTime"
}
```

### Schéma de Base de Données

```sql
garages (id, name, rue, ville, code_postal, pays, telephone, email, created_at, updated_at)
  └── garage_horaires (garage_id, day_of_week, horaires)
  └── vehicules (id, garage_id, modele_id, brand, annee_fabrication, type_carburant, created_at, updated_at)
      └── accessoires (id, vehicule_id, nom, description, prix, type, created_at)

modeles_vehicules (id, nom_modele, brand, description, specifications)
```

## 🧪 Tests

### Exécuter tous les tests

```bash
mvn test
```

### Tests unitaires uniquement

```bash
mvn test -Dtest=*Test
```

### Tests d'intégration

```bash
mvn test -Dtest=*IntegrationTest
```

### Couverture de code

```bash
mvn clean test jacoco:report
```

Le rapport sera généré dans `target/site/jacoco/index.html`

### Structure des tests

```
src/test/java/
├── domain/
│   ├── GarageTest.java
│   └── VehiculeTest.java
├── application/
│   └── GarageServiceTest.java
└── infrastructure/
    └── GarageControllerIntegrationTest.java
```

## 📚 Documentation API

### Swagger UI

L'application expose une documentation interactive Swagger UI :

```
http://localhost:8080/swagger-ui.html
```

### OpenAPI Specification

La spécification OpenAPI JSON est disponible à :

```
http://localhost:8080/api-docs
```

## ⚠️ Contraintes Métiers

### 1. Capacité Maximale des Garages

Chaque garage peut stocker **maximum 50 véhicules**.

**Validation :**
- Au niveau applicatif (Domain Layer)
- Au niveau base de données (Trigger PostgreSQL)

**Erreur retournée :**
```json
{
  "code": "CAPACITY_EXCEEDED",
  "message": "Le garage a atteint sa capacité maximale de 50 véhicules",
  "timestamp": "2024-11-28T10:30:00"
}
```

### 2. Partage des Modèles de Véhicules

Un même modèle de véhicule peut être stocké dans plusieurs garages via la table `modeles_vehicules`.

### 3. Informations Obligatoires

#### Garage
- ✅ name
- ✅ address (rue, ville, codePostal, pays)
- ✅ telephone (format: `+33XXXXXXXXX`)
- ✅ email (format valide et unique)
- ✅ horairesOuverture (Map<DayOfWeek, List<OpeningTime>>)

#### Véhicule
- ✅ brand
- ✅ anneeFabrication (1900 ≤ année ≤ année actuelle + 1)
- ✅ typeCarburant (ESSENCE, DIESEL, ELECTRIQUE, HYBRIDE, GPL)

#### Accessoire
- ✅ nom
- ✅ description
- ✅ prix (≥ 0)
- ✅ type (INTERIEUR, EXTERIEUR, ELECTRONIQUE, SECURITE, CONFORT)

## 📡 Système d'Événements

Le service implémente un **système de publication/consommation d'événements** basé sur le pattern **Publisher-Subscriber** pour découpler la logique métier des traitements annexes.

### Architecture des Événements

```
VehiculeService → DomainEventPublisher → Spring Events → VehiculeEventListener
                                                              ↓
                                              ┌───────────────┴────────────────┐
                                              │                                │
                                         Notifications                   Statistiques
                                         Emails/SMS                    Mise à jour
                                              │                                │
                                    Synchronisation                     Indexation
                                    Système externe                    Elasticsearch
```

### Événements Disponibles

#### `VehiculeCreatedEvent`

Publié automatiquement lors de la création d'un véhicule.

**Contenu de l'événement :**
```java
{
  "vehiculeId": "uuid",
  "garageId": "uuid",
  "brand": "Renault Clio",
  "anneeFabrication": 2024,
  "typeCarburant": "ESSENCE",
  "occurredOn": "2024-11-28T10:30:00"
}
```

### Consumer (Listener)

Le `VehiculeEventListener` traite les événements de manière **asynchrone** :

- ✅ **Notifications** : Envoi d'emails/SMS
- ✅ **Statistiques** : Mise à jour des métriques
- ✅ **Synchronisation** : Mise à jour de systèmes externes
- ✅ **Indexation** : Elasticsearch pour la recherche

### Configuration

**Pool de threads asynchrone :**
- Core Pool Size: **5 threads**
- Max Pool Size: **10 threads**
- Queue Capacity: **100 événements**

### Test du Système

#### Via Script PowerShell
```powershell
.\test-events.ps1
```

#### Via cURL
```bash
# 1. Créer un véhicule (déclenche l'événement)
curl -X POST http://localhost:8080/api/v1/garages/{garageId}/vehicules \
  -H "Content-Type: application/json" \
  -d '{
    "modeleId": "uuid",
    "brand": "Renault Zoe",
    "anneeFabrication": 2024,
    "typeCarburant": "ELECTRIQUE"
  }'

# 2. Observer les logs
# [INFO] 📢 Publication d'un événement domaine: VehiculeCreatedEvent
# [INFO] 🚗 [CONSUMER] Réception d'un événement VehiculeCreatedEvent
# [INFO] ⚙️  Traitement de l'événement en cours...
# [INFO] ✅ Événement traité avec succès
```

### Extensibilité

Pour ajouter un nouveau consumer :

```java
@Component
public class MyCustomListener {
    
    @Async
    @EventListener
    public void onVehiculeCreated(VehiculeCreatedEvent event) {
        // Votre logique de traitement
    }
}
```

**📚 Documentation détaillée :** Voir [EVENTS.md](EVENTS.md)

## 🔐 Gestion des Erreurs

### Codes d'erreur

| Code | Description | HTTP Status |
|------|-------------|-------------|
| `GARAGE_NOT_FOUND` | Garage introuvable | 404 |
| `CAPACITY_EXCEEDED` | Capacité du garage dépassée | 400 |
| `VALIDATION_ERROR` | Erreur de validation | 400 |
| `INVALID_ARGUMENT` | Argument invalide | 400 |
| `INTERNAL_SERVER_ERROR` | Erreur serveur | 500 |

### Format des réponses d'erreur

```json
{
  "code": "ERROR_CODE",
  "message": "Message d'erreur détaillé",
  "timestamp": "2024-11-28T10:30:00"
}
```

Pour les erreurs de validation :

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Erreur de validation des données",
  "errors": {
    "email": "Format d'email invalide",
    "telephone": "Format de téléphone invalide"
  },
  "timestamp": "2024-11-28T10:30:00"
}
```

## 🔍 Monitoring et Observabilité

### Spring Boot Actuator

Endpoints disponibles :

- `/actuator/health` - État de santé
- `/actuator/info` - Informations application
- `/actuator/metrics` - Métriques

**Exemple :**
```bash
curl http://localhost:8080/actuator/health
```

## 📦 Structure du Projet

```
renault-garage-service/
├── src/
│   ├── main/
│   │   ├── java/com/renault/garage/
│   │   │   ├── domain/
│   │   │   │   ├── model/           # Entités et Value Objects
│   │   │   │   ├── repository/      # Interfaces repository
│   │   │   │   └── exception/       # Exceptions métier
│   │   │   ├── application/
│   │   │   │   ├── service/         # Use cases
│   │   │   │   ├── dto/             # DTOs
│   │   │   │   └── mapper/          # Mappers
│   │   │   ├── infrastructure/
│   │   │   │   ├── persistence/     # JPA Entities & Repositories
│   │   │   │   ├── rest/            # Controllers
│   │   │   │   └── config/          # Configuration
│   │   │   └── GarageMicroserviceApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/        # Scripts Flyway
│   └── test/
├── docker-compose.yml
├── pom.xml
└── README.md
```

## 🛠️ Technologies Utilisées

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL 15**
- **Flyway** (migration de base de données)
- **Spring Validation** (Jakarta Bean Validation)
- **Springdoc OpenAPI** (documentation Swagger)
- **JUnit 5** & **Mockito** (tests)
- **Testcontainers** (tests d'intégration)
- **Maven** (build)
- **Docker** (conteneurisation)

## 🚀 Évolutions Futures

### Phase 2 - Sécurité
- [ ] Authentification OAuth2/JWT
- [ ] Autorisation basée sur les rôles (RBAC)
- [ ] Rate limiting

### Phase 3 - Performance
- [ ] Cache avec Redis
- [ ] Recherche full-text avec Elasticsearch
- [ ] Optimisation des requêtes N+1

### Phase 4 - Architecture
- [ ] Event Sourcing
- [ ] CQRS (séparation lecture/écriture)
- [ ] API GraphQL
- [ ] Webhooks

### Phase 5 - Observabilité
- [ ] Prometheus & Grafana
- [ ] Distributed Tracing (Jaeger)
- [ ] Logging centralisé (ELK Stack)

## 📞 Support

Pour toute question ou problème, contactez :
- Email: support@renault.fr
- Équipe: Renault IT Team

## 📄 License

Copyright © 2024 Renault. Tous droits réservés.
#   t e s t - c a p  
 