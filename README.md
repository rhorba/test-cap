# 🚗 Renault Garage Management Service

Microservice de gestion des garages, véhicules et accessoires pour le réseau Renault, avec architecture hexagonale et système d'événements Kafka.

## 📋 Table des matières

- [Vue d'ensemble](#-vue-densemble)
- [Architecture](#-architecture)
- [Démarrage rapide](#-démarrage-rapide)
- [Documentation API](#-documentation-api)
- [Modèle de données](#-modèle-de-données)
- [Système d'événements Kafka](#-système-dévénements-kafka)
- [Tests](#-tests)
- [Configuration](#-configuration)
- [Contraintes métiers](#-contraintes-métiers)
- [Monitoring](#-monitoring)
- [Évolutions futures](#-évolutions-futures)

---

## 🎯 Vue d'ensemble

### Contexte

Renault souhaite développer un microservice pour gérer les informations relatives aux garages affiliés à son réseau avec une architecture moderne, scalable et event-driven.

### Fonctionnalités principales

✅ **Gestion des garages** - CRUD complet avec pagination et tri  
✅ **Gestion des véhicules** - Association aux garages et modèles  
✅ **Gestion des accessoires** - Équipements des véhicules  
✅ **Système d'événements Kafka** - Communication asynchrone event-driven  
✅ **Recherches avancées** - Par type de véhicule, disponibilité, etc.  
✅ **Validation métier** - Capacité maximale, contraintes de données  
✅ **API REST** - Documentation Swagger/OpenAPI interactive

### Stack technique

- **Backend**: Java 17, Spring Boot 3.2.0
- **Base de données**: PostgreSQL 15
- **Messaging**: Apache Kafka 7.5.0 + Zookeeper
- **Architecture**: Hexagonale (Ports & Adapters) + DDD
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Conteneurisation**: Docker + Docker Compose
- **Documentation**: Swagger/OpenAPI 3.0

---

## 🏗️ Architecture

### Architecture hexagonale (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────┐
│                    REST API Layer                        │
│         Controllers + Exception Handlers                 │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                  Application Layer                       │
│         Services, DTOs, Mappers, Use Cases              │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                    Domain Layer                          │
│     Entities, Value Objects, Business Logic             │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│              Infrastructure Layer                        │
│    JPA Repositories, Kafka Adapters, Config             │
└─────────────────────────────────────────────────────────┘
                          ↓
┌──────────────────────┬──────────────────────────────────┐
│     PostgreSQL       │         Apache Kafka             │
└──────────────────────┴──────────────────────────────────┘
```

### Système d'événements

```
VehiculeService → KafkaDomainEventPublisher → Kafka Broker
                                                    ↓
                                            Topic: vehicule.created
                                                    ↓
                                          VehiculeKafkaConsumer
                                                    ↓
                        ┌───────────────────────────┼────────────────────┐
                        │                           │                    │
                   Notifications              Statistiques          Indexation
                   (Email/SMS)               (Métriques)         (Elasticsearch)
                        │                           │                    │
                 Synchronisation                 Logs                Analytics
                 (Systèmes externes)           (Audit)              (Reporting)
```

### Principes de design

- **Domain-Driven Design (DDD)**: Modélisation centrée sur le domaine métier
- **Clean Architecture**: Indépendance des frameworks et infrastructure
- **Event-Driven Architecture**: Communication asynchrone via Kafka
- **SOLID Principles**: Code maintenable et extensible
- **Test-Driven Development**: Couverture de tests complète

---

## 🚀 Démarrage rapide

### Prérequis

- **Java 17** ou supérieur
- **Maven 3.8+**
- **Docker** et **Docker Compose**
- **Git**

### Installation en 3 étapes

#### 1. Cloner le repository

```bash
git clone https://github.com/rhorba/test-cap.git
cd test-cap
```

#### 2. Démarrer l'infrastructure complète avec Docker

```bash
docker-compose up --build -d
```

Cette commande démarre automatiquement :
- ✅ **PostgreSQL** (port 5432) - Base de données principale
- ✅ **PgAdmin** (port 5050) - Interface de gestion PostgreSQL
- ✅ **Zookeeper** (port 2181) - Coordination Kafka
- ✅ **Kafka** (ports 9092-9093) - Message broker
- ✅ **Kafka UI** (port 8090) - Interface de monitoring Kafka
- ✅ **Application Spring Boot** (port 8080) - Microservice

#### 3. Vérifier le démarrage

```bash
# Vérifier l'état des conteneurs
docker-compose ps

# Tester l'API
curl http://localhost:8080/actuator/health

# Réponse attendue
{"status":"UP"}
```

### Accès aux interfaces

| Service | URL | Identifiants |
|---------|-----|--------------|
| **API REST** | http://localhost:8080 | - |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | - |
| **Kafka UI** | http://localhost:8090 | - |
| **PgAdmin** | http://localhost:5050 | admin@renault.fr / admin123 |

### Exemple rapide : Créer un garage

```bash
curl -X POST http://localhost:8080/api/v1/garages \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Garage Renault Paris",
    "address": {
      "rue": "123 Avenue des Champs",
      "ville": "Paris",
      "codePostal": "75008",
      "pays": "France"
    },
    "telephone": "+33123456789",
    "email": "paris@renault.fr",
    "horairesOuverture": {
      "MONDAY": [{"startTime": "08:00", "endTime": "18:00"}],
      "TUESDAY": [{"startTime": "08:00", "endTime": "18:00"}],
      "WEDNESDAY": [{"startTime": "08:00", "endTime": "18:00"}],
      "THURSDAY": [{"startTime": "08:00", "endTime": "18:00"}],
      "FRIDAY": [{"startTime": "08:00", "endTime": "18:00"}]
    }
  }'
```

### Arrêter l'infrastructure

```bash
# Arrêter les services
docker-compose down

# Arrêter et supprimer les volumes (réinitialisation complète)
docker-compose down -v
```

---

## 📚 Documentation API

### Swagger UI (Recommandé)

Interface interactive pour tester l'API :

```
http://localhost:8080/swagger-ui.html
```

### Endpoints principaux

#### Garages

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/v1/garages` | Créer un nouveau garage |
| GET | `/api/v1/garages/{id}` | Récupérer un garage par ID |
| GET | `/api/v1/garages` | Lister tous les garages (paginé) |
| PUT | `/api/v1/garages/{id}` | Mettre à jour un garage |
| DELETE | `/api/v1/garages/{id}` | Supprimer un garage |

#### Véhicules

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/v1/garages/{garageId}/vehicules` | Ajouter un véhicule |
| GET | `/api/v1/garages/{garageId}/vehicules` | Lister les véhicules d'un garage |
| GET | `/api/v1/garages/{garageId}/vehicules/{id}` | Récupérer un véhicule |
| PUT | `/api/v1/garages/{garageId}/vehicules/{id}` | Mettre à jour un véhicule |
| DELETE | `/api/v1/garages/{garageId}/vehicules/{id}` | Supprimer un véhicule |

#### Accessoires

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/api/v1/vehicules/{vehiculeId}/accessoires` | Ajouter un accessoire |
| GET | `/api/v1/vehicules/{vehiculeId}/accessoires` | Lister les accessoires |
| DELETE | `/api/v1/vehicules/{vehiculeId}/accessoires/{id}` | Supprimer un accessoire |

### Paramètres de pagination et tri

| Paramètre | Description | Défaut |
|-----------|-------------|--------|
| `page` | Numéro de page | 0 |
| `size` | Éléments par page | 20 |
| `sortBy` | Champ de tri | name |
| `direction` | ASC ou DESC | ASC |

**Exemple :**
```bash
GET /api/v1/garages?page=0&size=10&sortBy=name&direction=ASC
```

### OpenAPI Specification

Spécification JSON disponible à :
```
http://localhost:8080/api-docs
```

---

## 💾 Modèle de données

### Entités principales

#### Garage
```json
{
  "id": "UUID",
  "name": "string (required)",
  "address": {
    "rue": "string (required)",
    "ville": "string (required)",
    "codePostal": "string (required)",
    "pays": "string (required)"
  },
  "telephone": "string (required, format: +33XXXXXXXXX)",
  "email": "string (required, unique)",
  "horairesOuverture": {
    "MONDAY": [
      {"startTime": "HH:mm", "endTime": "HH:mm"}
    ]
  },
  "nombreVehicules": "int (read-only)",
  "capaciteRestante": "int (read-only, max: 50)",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

#### Vehicule
```json
{
  "id": "UUID",
  "garageId": "UUID (required)",
  "modeleId": "UUID (required)",
  "brand": "string (required)",
  "anneeFabrication": "int (1900-2026)",
  "typeCarburant": "ESSENCE | DIESEL | ELECTRIQUE | HYBRIDE | GPL",
  "nombreAccessoires": "int (read-only)",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

#### Accessoire
```json
{
  "id": "UUID",
  "vehiculeId": "UUID (required)",
  "nom": "string (required)",
  "description": "string",
  "prix": "decimal (>= 0)",
  "type": "INTERIEUR | EXTERIEUR | ELECTRONIQUE | SECURITE | CONFORT",
  "createdAt": "timestamp"
}
```

### Schéma de base de données

```sql
garages
├── id (UUID, PK)
├── name (VARCHAR)
├── rue, ville, code_postal, pays
├── telephone, email (UNIQUE)
├── created_at, updated_at
└── garage_horaires (JSON: day_of_week → horaires)

modeles_vehicules (catalogue partagé)
├── id (UUID, PK)
├── nom_modele (VARCHAR)
├── brand (VARCHAR)
└── description (TEXT)

vehicules
├── id (UUID, PK)
├── garage_id (UUID, FK → garages)
├── modele_id (UUID, FK → modeles_vehicules)
├── brand (VARCHAR)
├── annee_fabrication (INT)
├── type_carburant (ENUM)
└── created_at, updated_at

accessoires
├── id (UUID, PK)
├── vehicule_id (UUID, FK → vehicules)
├── nom, description (VARCHAR, TEXT)
├── prix (DECIMAL)
├── type (ENUM)
└── created_at
```

**Contraintes :**
- Un garage peut contenir **maximum 50 véhicules**
- Un modèle de véhicule peut être présent dans plusieurs garages
- Les emails de garage sont uniques dans le système

---

## 📡 Système d'événements Kafka

### Architecture du système

Le microservice implémente un système **event-driven** avec Apache Kafka pour découpler la logique métier des traitements annexes.

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

**Structure :**
```json
{
  "vehiculeId": "UUID",
  "garageId": "UUID",
  "brand": "string",
  "anneeFabrication": 2024,
  "typeCarburant": "ESSENCE|DIESEL|ELECTRIQUE|HYBRIDE|GPL",
  "occurredOn": "2025-11-28T10:30:00"
}
```

### Consumer (VehiculeKafkaConsumer)

Le consumer traite les événements de manière **asynchrone** et déclenche automatiquement :

- ✅ **Notifications** - Envoi d'emails/SMS aux parties concernées
- ✅ **Statistiques** - Mise à jour des métriques et analytics
- ✅ **Synchronisation** - Mise à jour des systèmes externes
- ✅ **Indexation** - Indexation dans Elasticsearch pour la recherche

### Configuration Kafka

**Topic:** `vehicule.created`  
**Partitions:** 3  
**Replication factor:** 1  
**Consumer group:** `garage-service-group`

**Bootstrap servers:**
- Docker interne: `kafka:9093`
- Localhost externe: `localhost:9092`

### Tester le système d'événements

#### 1. Créer un véhicule pour déclencher l'événement

```bash
curl -X POST http://localhost:8080/api/v1/garages/{garageId}/vehicules \
  -H "Content-Type: application/json" \
  -d '{
    "modeleId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12",
    "brand": "Renault",
    "anneeFabrication": 2024,
    "typeCarburant": "ESSENCE"
  }'
```

#### 2. Observer les logs de l'application

```bash
docker-compose logs -f app
```

**Logs attendus :**
```
[INFO] 📢 [KAFKA] Événement publié avec succès sur le topic 'vehicule.created' - partition: 2, offset: 0
[INFO] 🚗 [KAFKA CONSUMER] Réception d'un événement VehiculeCreatedEvent
[INFO]    📍 Partition: 2, Offset: 0
[INFO]    🚙 Véhicule ID: 8fe483cf-ca44-4a74-bab5-d377c7f83d55
[INFO]    🏢 Garage ID: 967c9022-0ff4-4157-98d3-3f9f5a1f41ba
[INFO]    🔧 Marque: Renault
[INFO]    📅 Année: 2024
[INFO]    ⛽ Carburant: ESSENCE
[INFO] ⚙️  [KAFKA] Traitement de l'événement en cours...
[INFO] 📧 [Notification] Envoi d'email pour le nouveau véhicule
[INFO] 📊 [Statistiques] Mise à jour: +1 véhicule Renault (ESSENCE)
[INFO] 🔄 [Synchronisation] Mise à jour du système externe
[INFO] 🔍 [Indexation] Indexation du véhicule dans Elasticsearch
[INFO] ✅ [KAFKA] Événement traité avec succès
```

#### 3. Consulter Kafka UI

Ouvrir http://localhost:8090 pour visualiser :
- Les topics Kafka
- Les messages publiés
- Les consumer groups
- Les offsets

---

## 🧪 Tests

### Exécuter tous les tests

```bash
mvn test
```

### Tests unitaires

```bash
mvn test -Dtest=*Test
```

### Tests d'intégration

```bash
mvn test -Dtest=*IntegrationTest
```

### Couverture de code (JaCoCo)

```bash
mvn clean test jacoco:report
```

Rapport disponible : `target/site/jacoco/index.html`

### Structure des tests

```
src/test/java/com/renault/garage/
├── domain/
│   ├── model/
│   │   ├── GarageTest.java
│   │   ├── VehiculeTest.java
│   │   └── AccessoireTest.java
│   └── service/
│       └── GarageDomainServiceTest.java
├── application/
│   └── service/
│       ├── GarageServiceTest.java
│       └── VehiculeServiceTest.java
└── infrastructure/
    ├── rest/
    │   └── GarageControllerIntegrationTest.java
    └── event/
        └── VehiculeKafkaIntegrationTest.java
```

---

## ⚙️ Configuration

### Profils Spring Boot

| Profil | Description | Usage |
|--------|-------------|-------|
| `default` | PostgreSQL + Kafka | `docker-compose up` |
| `test` | H2 in-memory, Kafka désactivé | Tests automatiques |

### Variables d'environnement

| Variable | Description | Défaut |
|----------|-------------|--------|
| `SPRING_DATASOURCE_URL` | URL de la base de données | `jdbc:postgresql://localhost:5432/renault_garage_db` |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Serveurs Kafka | `localhost:9092` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Stratégie DDL | `update` |

### Configuration Docker Compose

```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/renault_garage_db
      SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9093
    depends_on:
      - postgres
      - kafka
```

---

## ⚠️ Contraintes métiers

### 1. Capacité maximale des garages

**Règle:** Chaque garage peut stocker **maximum 50 véhicules**.

**Validation:**
- ✅ Niveau application (Domain Layer)
- ✅ Niveau base de données (constraint PostgreSQL)

**Erreur retournée (HTTP 400):**
```json
{
  "code": "CAPACITY_EXCEEDED",
  "message": "Le garage a atteint sa capacité maximale de 50 véhicules",
  "timestamp": "2025-11-28T10:30:00"
}
```

### 2. Partage des modèles de véhicules

Un même modèle de véhicule peut être présent dans plusieurs garages via la table `modeles_vehicules` (catalogue partagé).

### 3. Validations des données

#### Garage
- ✅ **name**: 3-255 caractères
- ✅ **telephone**: Format `+33XXXXXXXXX`
- ✅ **email**: Format valide et unique dans le système
- ✅ **horairesOuverture**: Au moins un jour avec horaires valides

#### Véhicule
- ✅ **brand**: Non vide, max 100 caractères
- ✅ **anneeFabrication**: 1900 ≤ année ≤ 2026
- ✅ **typeCarburant**: Valeur de l'enum

#### Accessoire
- ✅ **nom**: Non vide
- ✅ **prix**: ≥ 0
- ✅ **type**: Valeur de l'enum

### Format des erreurs

**Erreur de validation (HTTP 400):**
```json
{
  "code": "VALIDATION_ERROR",
  "message": "Erreur de validation des données",
  "errors": {
    "email": "Format d'email invalide",
    "telephone": "Format de téléphone invalide"
  },
  "timestamp": "2025-11-28T10:30:00"
}
```

**Erreur métier (HTTP 404):**
```json
{
  "code": "GARAGE_NOT_FOUND",
  "message": "Aucun garage trouvé avec l'ID: 123e4567-e89b-12d3-a456-426614174000",
  "timestamp": "2025-11-28T10:30:00"
}
```

---

## 🔍 Monitoring

### Spring Boot Actuator

Endpoints disponibles :

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | État de santé de l'application |
| `/actuator/info` | Informations sur l'application |
| `/actuator/metrics` | Métriques de performance |

**Exemple:**
```bash
curl http://localhost:8080/actuator/health

# Réponse
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "kafka": {"status": "UP"},
    "diskSpace": {"status": "UP"}
  }
}
```

### Kafka UI

Interface de monitoring Kafka : http://localhost:8090

Permet de visualiser :
- Topics et partitions
- Messages en temps réel
- Consumer groups et lag
- Cluster configuration

---

## 📦 Structure du projet

```
renault-garage-service/
├── src/
│   ├── main/
│   │   ├── java/com/renault/garage/
│   │   │   ├── domain/
│   │   │   │   ├── model/              # Entités, Value Objects
│   │   │   │   ├── repository/         # Interfaces repository (ports)
│   │   │   │   ├── service/            # Services domaine
│   │   │   │   └── exception/          # Exceptions métier
│   │   │   ├── application/
│   │   │   │   ├── service/            # Use cases, orchestration
│   │   │   │   ├── dto/                # Request/Response DTOs
│   │   │   │   └── mapper/             # Mappers Domain ↔ DTO
│   │   │   ├── infrastructure/
│   │   │   │   ├── persistence/
│   │   │   │   │   ├── jpa/           # Entités JPA
│   │   │   │   │   └── adapter/       # Implémentations repositories
│   │   │   │   ├── rest/              # Controllers REST
│   │   │   │   ├── event/             # Kafka publisher/consumer
│   │   │   │   └── config/            # Configuration Spring
│   │   │   └── GarageMicroserviceApplication.java
│   │   └── resources/
│   │       ├── application.yml         # Configuration principale
│   │       ├── application-test.yml    # Configuration tests
│   │       └── db/migration/           # Scripts Flyway (si activé)
│   └── test/                           # Tests unitaires et d'intégration
├── Dockerfile                          # Image Docker multi-stage
├── docker-compose.yml                  # Stack complète (app + infra)
├── pom.xml                             # Dépendances Maven
└── README.md                           # Ce fichier
```

---

## 🚀 Évolutions futures

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
#   t e s t - c a p 
 
 