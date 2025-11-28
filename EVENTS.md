# 📡 Système de Publication/Consommation d'Événements

## 🎯 Vue d'ensemble

Le système implémente un pattern **Publisher-Subscriber** pour la gestion des événements lors de la création de véhicules. Cela permet un découplage entre la logique métier et les traitements annexes.

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│           VehiculeService (Application)                  │
│  • Crée un véhicule                                      │
│  • Publie VehiculeCreatedEvent                          │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ publish(event)
                     ▼
┌─────────────────────────────────────────────────────────┐
│      SpringDomainEventPublisher (Infrastructure)         │
│  • Adaptateur Spring pour publier des événements        │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ Spring ApplicationEventPublisher
                     ▼
┌─────────────────────────────────────────────────────────┐
│         VehiculeEventListener (Consumer)                 │
│  • Écoute les événements VehiculeCreatedEvent          │
│  • Traitement asynchrone                                │
│  • Notifications, statistiques, synchronisation         │
└─────────────────────────────────────────────────────────┘
```

## 📦 Composants Implémentés

### 1. Événement Domaine

**Fichier:** `domain/event/VehiculeCreatedEvent.java`

```java
public class VehiculeCreatedEvent {
    private final UUID vehiculeId;
    private final UUID garageId;
    private final String brand;
    private final int anneeFabrication;
    private final String typeCarburant;
    private final LocalDateTime occurredOn;
}
```

### 2. Publisher Interface

**Fichier:** `domain/event/DomainEventPublisher.java`

```java
public interface DomainEventPublisher {
    void publish(Object event);
}
```

### 3. Publisher Implementation

**Fichier:** `infrastructure/event/SpringDomainEventPublisher.java`

Implémentation utilisant `ApplicationEventPublisher` de Spring.

### 4. Consumer (Listener)

**Fichier:** `infrastructure/event/VehiculeEventListener.java`

```java
@Component
public class VehiculeEventListener {
    
    @Async
    @EventListener
    public void onVehiculeCreated(VehiculeCreatedEvent event) {
        // Traitement asynchrone de l'événement
    }
}
```

### 5. Configuration Asynchrone

**Fichier:** `infrastructure/config/AsyncConfig.java`

Configure le pool de threads pour le traitement asynchrone des événements.

## 🔄 Flux d'Exécution

### 1. Création d'un véhicule

```bash
POST /api/v1/vehicules
{
  "garageId": "uuid",
  "brand": "Renault Clio",
  "anneeFabrication": 2024,
  "typeCarburant": "ESSENCE"
}
```

### 2. Publication de l'événement

```java
// Dans VehiculeService.createVehicule()
VehiculeCreatedEvent event = new VehiculeCreatedEvent(...);
eventPublisher.publish(event);
```

### 3. Consommation asynchrone

```
[PUBLISHER] 📢 Publication d'un événement domaine: VehiculeCreatedEvent
[CONSUMER]  🚗 Réception d'un événement VehiculeCreatedEvent
[CONSUMER]     → Véhicule ID: 123e4567-e89b-12d3-a456-426614174000
[CONSUMER]     → Garage ID: 789e4567-e89b-12d3-a456-426614174000
[CONSUMER]     → Marque: Renault Clio
[CONSUMER]  ⚙️  Traitement de l'événement en cours...
[CONSUMER]  📧 Envoi de notification
[CONSUMER]  📊 Mise à jour des statistiques
[CONSUMER]  🔄 Synchronisation avec système externe
[CONSUMER]  ✅ Événement traité avec succès
```

## 🎯 Cas d'Usage du Consumer

Le `VehiculeEventListener` peut effectuer plusieurs traitements:

### 1. Notifications
```java
private void sendNotification(VehiculeCreatedEvent event) {
    // Envoi d'email au responsable du garage
    // Notification push vers une application mobile
    // Alerte SMS
}
```

### 2. Statistiques
```java
private void updateStatistics(VehiculeCreatedEvent event) {
    // Mise à jour du nombre de véhicules par type
    // Calcul des tendances d'acquisition
    // Mise à jour du tableau de bord
}
```

### 3. Synchronisation Externe
```java
private void syncWithExternalSystem(VehiculeCreatedEvent event) {
    // Synchronisation avec ERP
    // Mise à jour du système de facturation
    // Export vers data warehouse
}

```

### 4. Indexation
```java
private void indexInSearchEngine(VehiculeCreatedEvent event) {
    // Indexation dans Elasticsearch
    // Mise à jour du catalogue de recherche
}
```

## ⚙️ Configuration

### Pool de Threads Asynchrone

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    - Core Pool Size: 5 threads
    - Max Pool Size: 10 threads
    - Queue Capacity: 100 événements
    - Thread Name: event-consumer-*
}
```

## 🧪 Tests

### Test du Consumer

**Fichier:** `test/.../VehiculeEventListenerTest.java`

```bash
mvn test -Dtest=VehiculeEventListenerTest
```

### Test d'Intégration Complet

```java
@Test
void shouldPublishEventWhenVehiculeIsCreated() {
    // Créer un véhicule via API
    // Vérifier que l'événement est publié
    // Vérifier que le consumer le traite
}
```

## 📊 Avantages de cette Architecture

### ✅ Découplage
- La logique métier ne dépend pas des traitements annexes
- Les consumers peuvent être ajoutés/modifiés sans toucher au service

### ✅ Asynchronisme
- Les traitements lourds n'impactent pas le temps de réponse API
- Meilleure scalabilité

### ✅ Extensibilité
- Facile d'ajouter de nouveaux consumers
- Chaque consumer est indépendant

### ✅ Résilience
- Si un consumer échoue, cela n'impacte pas les autres
- Possibilité de retry et dead letter queue

## 🚀 Évolutions Possibles

### 1. Message Broker Externe
```java
// Remplacer Spring Events par RabbitMQ/Kafka
@KafkaListener(topics = "vehicule.created")
public void onVehiculeCreated(VehiculeCreatedEvent event) {
    // ...
}
```

### 2. Event Sourcing
```java
// Stocker tous les événements dans un event store
public interface EventStore {
    void append(DomainEvent event);
    List<DomainEvent> getEvents(UUID aggregateId);
}
```

### 3. SAGA Pattern
```java
// Orchestration de transactions distribuées
public class VehiculeCreationSaga {
    // Coordonner plusieurs services
}
```

### 4. Dead Letter Queue
```java
// Gestion des événements en erreur
@EventListener(condition = "#root.event.retryCount > 3")
public void handleFailedEvent(VehiculeCreatedEvent event) {
    // Envoyer vers une DLQ
}
```

## 📝 Logs

Les logs permettent de suivre le cycle de vie complet:

```
[INFO] VehiculeService - 🚗 Création d'un nouveau véhicule pour le garage abc-123
[INFO] SpringDomainEventPublisher - 📢 Publication d'un événement domaine: VehiculeCreatedEvent
[INFO] VehiculeEventListener - 🚗 [CONSUMER] Réception d'un événement VehiculeCreatedEvent
[INFO] VehiculeEventListener - ⚙️  Traitement de l'événement en cours...
[INFO] VehiculeEventListener - 📧 Envoi de notification
[INFO] VehiculeEventListener - 📊 Mise à jour des statistiques
[INFO] VehiculeEventListener - 🔄 Synchronisation avec système externe
[INFO] VehiculeEventListener - ✅ Événement traité avec succès pour le véhicule xyz-789
[INFO] VehiculeService - ✅ Véhicule créé avec succès: xyz-789
```

## 🔍 Monitoring

### Métriques à Surveiller

1. **Nombre d'événements publiés** (par type)
2. **Temps de traitement** des consumers
3. **Taux d'erreur** dans les consumers
4. **Taille de la queue** d'événements en attente

### Health Check

```java
@Component
public class EventSystemHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Vérifier l'état du système d'événements
    }
}
```

## 📚 Documentation Complémentaire

- **README.md** - Documentation principale
- **API_USAGE_GUIDE.md** - Guide d'utilisation de l'API
- **TESTING.md** - Guide des tests

---

**✅ Système de publication/consommation d'événements opérationnel !** 🎉
