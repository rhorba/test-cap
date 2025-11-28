# Microservice de Gestion des Garages Renault - Guide de Démarrage Rapide

## Installation

### 1. Installer PostgreSQL

Téléchargez et installez PostgreSQL depuis: https://www.postgresql.org/download/

Créez une base de données:
```sql
CREATE DATABASE renault_garage;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE renault_garage TO postgres;
```

### 2. Configurer l'Application

Modifiez `src/main/resources/application.yml` si nécessaire:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/renault_garage
    username: postgres
    password: votre_mot_de_passe
```

### 3. Compiler et Lancer

```powershell
# Compiler le projet
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

L'application sera accessible sur: http://localhost:8080

## Documentation API

Une fois l'application lancée:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs JSON**: http://localhost:8080/api-docs

## Tester l'API

### Créer un Garage

```powershell
$body = @{
    name = "Renault Paris Nord"
    address = @{
        rue = "123 Avenue des Champs"
        ville = "Paris"
        codePostal = "75008"
        pays = "France"
    }
    telephone = "+33123456789"
    email = "paris.nord@renault.fr"
    horairesOuverture = @{
        MONDAY = @(
            @{ startTime = "08:00"; endTime = "12:00" },
            @{ startTime = "14:00"; endTime = "18:00" }
        )
    }
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/garages" -Method Post -Body $body -ContentType "application/json"
```

### Lister les Garages

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/garages?page=0&size=10" -Method Get
```

## Structure du Projet

```
src/main/java/com/renault/garage/
├── domain/                    # Couche Domaine (Business Logic)
│   ├── model/                 # Entités et Value Objects
│   ├── exception/             # Exceptions métier
│   └── repository/            # Interfaces des repositories (Ports)
├── application/               # Couche Application
│   ├── dto/                   # Data Transfer Objects
│   ├── mapper/                # Mappers Domain <-> DTO
│   └── service/               # Services applicatifs
└── infrastructure/            # Couche Infrastructure
    ├── rest/                  # Controllers REST
    ├── config/                # Configuration Spring
    └── persistence/           # Implémentation JPA
        ├── jpa/               # Entités JPA et repositories Spring Data
        └── adapter/           # Adapters Repository (Implémentation des Ports)
```

## Endpoints Principaux

### Garages
- `POST /api/v1/garages` - Créer un garage
- `GET /api/v1/garages` - Lister les garages
- `GET /api/v1/garages/{id}` - Récupérer un garage
- `PUT /api/v1/garages/{id}` - Mettre à jour un garage
- `DELETE /api/v1/garages/{id}` - Supprimer un garage

### Véhicules
- `POST /api/v1/garages/{garageId}/vehicules` - Ajouter un véhicule
- `GET /api/v1/garages/{garageId}/vehicules` - Lister les véhicules
- `PUT /api/v1/garages/{garageId}/vehicules/{id}` - Mettre à jour
- `DELETE /api/v1/garages/{garageId}/vehicules/{id}` - Supprimer

### Accessoires
- `POST /api/v1/vehicules/{vehiculeId}/accessoires` - Ajouter un accessoire
- `GET /api/v1/vehicules/{vehiculeId}/accessoires` - Lister les accessoires

## Troubleshooting

### Erreur de connexion à PostgreSQL
Vérifiez que PostgreSQL est démarré et que les credentials dans `application.yml` sont corrects.

### Port 8080 déjà utilisé
Modifiez le port dans `application.yml`:
```yaml
server:
  port: 8081
```

## Support

Pour toute question: digital@renault.fr
