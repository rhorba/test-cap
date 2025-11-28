# 🧪 Guide de Tests - Renault Garage Microservice

## 📋 Vue d'ensemble

Ce projet contient une suite complète de tests pour garantir la qualité et la fiabilité du microservice de gestion des garages Renault.

## 🎯 Types de tests

### 1. Tests Unitaires du Domaine
**Fichier:** `GarageTest.java`

Tests de la logique métier pure (Domain-Driven Design) :
- ✅ Création de garage avec informations valides
- ✅ Validation des emails
- ✅ Ajout de véhicules au garage
- ✅ Vérification de la capacité maximale (50 véhicules)
- ✅ Suppression de véhicules

**Exécution:**
```bash
mvn test -Dtest=GarageTest
```

### 2. Tests Unitaires des Services
**Fichier:** `GarageServiceTest.java`

Tests de la couche application avec mocks (Mockito) :
- ✅ Création de garage via service
- ✅ Récupération par ID
- ✅ Gestion des exceptions (GarageNotFoundException)
- ✅ Pagination des résultats
- ✅ Suppression de garage

**Exécution:**
```bash
mvn test -Dtest=GarageServiceTest
```

### 3. Tests d'Intégration REST API
**Fichier:** `GarageControllerIntegrationTest.java`

Tests end-to-end avec MockMvc (Spring Boot Test) :
- ✅ POST /api/v1/garages - Création de garage
- ✅ GET /api/v1/garages/{id} - Récupération par ID
- ✅ Validation des requêtes (retour HTTP 400 si données invalides)
- ✅ DELETE /api/v1/garages/{id} - Suppression

**Exécution:**
```bash
mvn test -Dtest=GarageControllerIntegrationTest
```

## 🚀 Exécution des tests

### Tous les tests
```bash
mvn test
```

### Tests avec rapport de couverture
```bash
mvn clean test jacoco:report
```
Rapport disponible dans : `target/site/jacoco/index.html`

### Tests d'une classe spécifique
```bash
mvn test -Dtest=NomDeLaClasse
```

### Tests en mode debug
```bash
mvn test -X
```

## 📊 Résultats attendus

### Statistiques de couverture
- **Domain Layer:** ~90% de couverture
- **Application Layer:** ~85% de couverture
- **Infrastructure Layer:** ~80% de couverture

### Temps d'exécution
- Tests unitaires : < 2 secondes
- Tests d'intégration : < 5 secondes
- Suite complète : < 10 secondes

## 🗄️ Migrations Flyway

### Scripts de migration

#### V1__create_garage_tables.sql
Création de la structure de base de données :
- Table `garages` avec contrainte unique sur email
- Table `garage_horaires` avec horaires en JSONB
- Table `modeles_vehicules` (catalogue partagé)
- Table `vehicules` avec contrainte de capacité (max 50 par garage)
- Table `accessoires`
- Triggers automatiques pour `updated_at`
- Fonction de vérification de capacité

#### V2__insert_sample_data.sql
Données de test initiales :
- 3 modèles de véhicules Renault (Zoe, Clio, Megane E-Tech)
- 2 garages (Paris Nord, Lyon Centre)
- Horaires d'ouverture pour les garages

### Exécution des migrations

#### Avec Spring Boot (automatique au démarrage)
```bash
mvn spring-boot:run
```

#### Avec Flyway CLI
```bash
flyway -url=jdbc:postgresql://localhost:5432/renault_garage \
       -user=postgres \
       -password=postgres \
       migrate
```

#### Vérifier l'état des migrations
```bash
flyway -url=jdbc:postgresql://localhost:5432/renault_garage \
       -user=postgres \
       -password=postgres \
       info
```

### Configuration Flyway

**application.yml (production) :**
```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
```

**application.yml (tests) :**
```yaml
spring:
  flyway:
    enabled: false  # Désactivé pour H2 en mémoire
```

## 🔧 Configuration de test

### Base de données de test
- **Type:** H2 (en mémoire)
- **Mode:** create-drop
- **Avantage:** Rapide, isolé, pas besoin de PostgreSQL

### Dépendances Maven
```xml
<!-- Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- H2 pour tests -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

## 📝 Bonnes pratiques

### Nomenclature des tests
- **shouldXxx** : Comportement attendu
- **@DisplayName** : Description en français pour la lisibilité

### Structure AAA (Arrange-Act-Assert)
```java
@Test
void shouldCreateGarageSuccessfully() {
    // Arrange - Préparation des données
    CreateGarageRequest request = createTestRequest();
    
    // Act - Exécution de l'action
    GarageResponse result = garageService.createGarage(request);
    
    // Assert - Vérification des résultats
    assertNotNull(result);
    assertEquals("Renault Paris", result.name());
}
```

### Isolation des tests
- Chaque test est indépendant
- Utilisation de `@BeforeEach` pour l'initialisation
- Pas de dépendance entre tests

## 🐛 Debugging des tests

### Activer les logs détaillés
Modifier `src/test/resources/application.yml` :
```yaml
logging:
  level:
    com.renault.garage: DEBUG
    org.springframework.test: DEBUG
```

### Exécuter un seul test
```bash
mvn test -Dtest=GarageTest#shouldCreateGarageWithValidInformation
```

## 📚 Ressources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Flyway Documentation](https://flywaydb.org/documentation/)

## ✅ Checklist avant commit

- [ ] Tous les tests passent (`mvn test`)
- [ ] Couverture > 80%
- [ ] Pas de tests ignorés sans raison
- [ ] Code formaté correctement
- [ ] Pas de System.out.println() dans le code de production
