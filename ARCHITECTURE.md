# 📐 Architecture Visuelle du Microservice

## 🏛️ Architecture Hexagonale (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────────────────┐
│                         INFRASTRUCTURE LAYER                         │
│                        (Adapters/Drivers)                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ┌────────────────────┐                   ┌──────────────────────┐  │
│  │  REST Controllers  │                   │  JPA Repositories    │  │
│  │                    │                   │                      │  │
│  │ GarageController   │                   │ GarageJpaEntity      │  │
│  │ VehiculeController │                   │ VehiculeJpaEntity    │  │
│  │ AccessoireCtrl     │                   │ AccessoireJpaEntity  │  │
│  └──────────┬─────────┘                   └──────────┬───────────┘  │
│             │                                         │              │
│             │ HTTP/REST                               │ JPA/SQL      │
│             │                                         │              │
└─────────────┼─────────────────────────────────────────┼──────────────┘
              │                                         │
              ▼                                         ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        APPLICATION LAYER                             │
│                      (Use Cases/Services)                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ┌────────────────────┐         ┌──────────────────────┐           │
│  │   Services         │         │      Mappers         │           │
│  │                    │         │                      │           │
│  │ GarageService      │◄────────┤ GarageMapper         │           │
│  │ VehiculeService    │         │ VehiculeMapper       │           │
│  │ AccessoireService  │         │ AccessoireMapper     │           │
│  └──────────┬─────────┘         └──────────────────────┘           │
│             │                                                        │
│             │                                                        │
└─────────────┼────────────────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          DOMAIN LAYER                                │
│                    (Business Logic/Core)                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │                      Aggregates                             │    │
│  │                                                              │    │
│  │  ┌──────────────┐         ┌─────────────┐                  │    │
│  │  │   Garage     │◄────────┤  Vehicule   │                  │    │
│  │  │ (Root)       │ 1    *  └──────┬──────┘                  │    │
│  │  │              │                │                          │    │
│  │  │ - name       │                │ 1                        │    │
│  │  │ - address    │                │                          │    │
│  │  │ - horaires   │                │ *                        │    │
│  │  │ - vehicules  │         ┌──────▼──────┐                  │    │
│  │  │              │         │ Accessoire  │                  │    │
│  │  │ MAX: 50      │         │             │                  │    │
│  │  └──────────────┘         └─────────────┘                  │    │
│  │                                                              │    │
│  └────────────────────────────────────────────────────────────┘    │
│                                                                       │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │                   Value Objects                             │    │
│  │                                                              │    │
│  │  ┌──────────────┐         ┌─────────────┐                  │    │
│  │  │   Address    │         │ OpeningTime │                  │    │
│  │  │              │         │             │                  │    │
│  │  │ - rue        │         │ - startTime │                  │    │
│  │  │ - ville      │         │ - endTime   │                  │    │
│  │  │ - codePostal │         │             │                  │    │
│  │  │ - pays       │         └─────────────┘                  │    │
│  │  └──────────────┘                                           │    │
│  │                                                              │    │
│  └────────────────────────────────────────────────────────────┘    │
│                                                                       │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │              Repository Interfaces (Ports)                  │    │
│  │                                                              │    │
│  │  «interface»          «interface»          «interface»      │    │
│  │  GarageRepository     VehiculeRepository   AccessoireRepo   │    │
│  │                                                              │    │
│  └────────────────────────────────────────────────────────────┘    │
│                                                                       │
│  ┌────────────────────────────────────────────────────────────┐    │
│  │                     Exceptions                              │    │
│  │                                                              │    │
│  │  - GarageNotFoundException                                  │    │
│  │  - CapaciteGarageDepasseeException                         │    │
│  │  - VehiculeNotFoundException                               │    │
│  │                                                              │    │
│  └────────────────────────────────────────────────────────────┘    │
│                                                                       │
└───────────────────────────────────────────────────────────────────────┘
```

## 🔄 Flux de Requête

### Exemple: Créer un Garage

```
1. Client HTTP
      │
      │ POST /api/v1/garages
      │ { "name": "Renault Paris", ... }
      ▼
2. GarageController
      │
      │ @PostMapping
      │ validate @Valid CreateGarageRequest
      ▼
3. GarageService
      │
      │ createGarage(request)
      ▼
4. GarageMapper
      │
      │ toDomain(request)
      ▼
5. Garage (Domain Entity)
      │
      │ new Garage(...) + business rules
      │ ✓ Validate email
      │ ✓ Validate horaires
      ▼
6. GarageRepository (Port)
      │
      │ save(garage)
      ▼
7. GarageRepositoryAdapter
      │
      │ toEntity(garage)
      ▼
8. SpringDataGarageRepository
      │
      │ JPA save()
      ▼
9. PostgreSQL
      │
      │ INSERT INTO garages...
      ▼
   ◄───── Response flows back ─────
      │
      │ GarageResponse DTO
      ▼
   Client receives 201 Created
```

## 📦 Structure des Packages

```
com.renault.garage/
│
├── GarageMicroserviceApplication.java    ← Main class
│
├── domain/                               ← DOMAIN LAYER
│   ├── model/
│   │   ├── Garage.java                  ← Aggregate Root
│   │   ├── Vehicule.java                ← Entity
│   │   ├── Accessoire.java              ← Entity
│   │   ├── Address.java                 ← Value Object (Record)
│   │   ├── OpeningTime.java             ← Value Object (Record)
│   │   ├── TypeCarburant.java           ← Enum
│   │   └── TypeAccessoire.java          ← Enum
│   │
│   ├── exception/
│   │   ├── GarageNotFoundException.java
│   │   ├── VehiculeNotFoundException.java
│   │   ├── AccessoireNotFoundException.java
│   │   └── CapaciteGarageDepasseeException.java
│   │
│   └── repository/                       ← PORTS (Interfaces)
│       ├── GarageRepository.java
│       ├── VehiculeRepository.java
│       └── AccessoireRepository.java
│
├── application/                          ← APPLICATION LAYER
│   ├── dto/
│   │   ├── CreateGarageRequest.java
│   │   ├── UpdateGarageRequest.java
│   │   ├── GarageResponse.java
│   │   ├── GarageListResponse.java
│   │   ├── AddressDTO.java
│   │   ├── OpeningTimeDTO.java
│   │   ├── Create/Update/Response...    (Vehicule & Accessoire)
│   │
│   ├── mapper/
│   │   ├── GarageMapper.java
│   │   ├── VehiculeMapper.java
│   │   └── AccessoireMapper.java
│   │
│   └── service/
│       ├── GarageService.java
│       ├── VehiculeService.java
│       └── AccessoireService.java
│
└── infrastructure/                       ← INFRASTRUCTURE LAYER
    ├── rest/                            ← REST Adapters
    │   ├── GarageController.java
    │   ├── VehiculeController.java
    │   ├── AccessoireController.java
    │   ├── GlobalExceptionHandler.java
    │   ├── ErrorResponse.java
    │   └── ValidationErrorResponse.java
    │
    ├── config/
    │   ├── OpenAPIConfig.java
    │   ├── WebConfig.java
    │   └── JacksonConfig.java
    │
    └── persistence/                      ← Persistence Adapters
        ├── jpa/
        │   ├── GarageJpaEntity.java
        │   ├── VehiculeJpaEntity.java
        │   ├── AccessoireJpaEntity.java
        │   ├── SpringDataGarageRepository.java
        │   ├── SpringDataVehiculeRepository.java
        │   └── SpringDataAccessoireRepository.java
        │
        └── adapter/
            ├── GarageRepositoryAdapter.java
            ├── VehiculeRepositoryAdapter.java
            └── AccessoireRepositoryAdapter.java
```

## 🔑 Principes Architecturaux Appliqués

### 1. **Hexagonal Architecture (Ports & Adapters)**
- ✅ Domaine isolé des détails techniques
- ✅ Ports = Interfaces du domaine
- ✅ Adapters = Implémentations infrastructure

### 2. **Domain-Driven Design (DDD)**
- ✅ Aggregate Root: Garage
- ✅ Entities: Vehicule, Accessoire
- ✅ Value Objects: Address, OpeningTime
- ✅ Domain Services
- ✅ Repository Pattern

### 3. **SOLID Principles**
- ✅ **S**ingle Responsibility
- ✅ **O**pen/Closed
- ✅ **L**iskov Substitution
- ✅ **I**nterface Segregation
- ✅ **D**ependency Inversion

### 4. **Clean Architecture**
- ✅ Indépendance des frameworks
- ✅ Testabilité
- ✅ Indépendance de la base de données
- ✅ Indépendance de l'UI

## 🎯 Avantages de cette Architecture

### ✅ Maintenabilité
- Code organisé par domaine métier
- Responsabilités clairement séparées
- Facile à comprendre et modifier

### ✅ Testabilité
- Domain isolé → tests unitaires simples
- Mocking facile des repositories (interfaces)
- Tests d'intégration séparés

### ✅ Évolutivité
- Changement de BDD sans toucher au domaine
- Ajout de nouveaux adapters (GraphQL, gRPC)
- Extension facile des fonctionnalités

### ✅ Réutilisabilité
- Domain model réutilisable
- Services métier indépendants
- DTOs découplés du domaine
