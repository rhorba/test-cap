# 🚀 Guide Kafka - Système d'Événements

## 📡 Vue d'ensemble

Le système de publication/consommation d'événements utilise **Apache Kafka** comme message broker pour garantir :
- ✅ **Persistance** : Les messages sont stockés sur disque
- ✅ **Scalabilité** : Partitions et réplication
- ✅ **Fiabilité** : Acknowledgment et retry automatique
- ✅ **Performance** : Traitement asynchrone haute vitesse
- ✅ **Traçabilité** : Offset et historique complet

---

## 🏗️ Architecture Kafka

```
┌──────────────────────────────────────────────────────────────┐
│                    VehiculeService                            │
│              (Crée un véhicule)                              │
└─────────────────────┬────────────────────────────────────────┘
                      │
                      │ publish(VehiculeCreatedEvent)
                      ▼
┌──────────────────────────────────────────────────────────────┐
│           KafkaDomainEventPublisher                           │
│     • Sérialise l'événement en JSON                          │
│     • Détermine le topic (vehicule.created)                  │
│     • Extrait la clé (garageId) pour partitionnement         │
└─────────────────────┬────────────────────────────────────────┘
                      │
                      │ KafkaTemplate.send()
                      ▼
┌──────────────────────────────────────────────────────────────┐
│                    Apache Kafka                               │
│  Topic: vehicule.created                                     │
│  • 3 partitions                                              │
│  • Rétention: 7 jours                                        │
│  • Compression: Snappy                                       │
└─────────────────────┬────────────────────────────────────────┘
                      │
                      │ @KafkaListener
                      ▼
┌──────────────────────────────────────────────────────────────┐
│            VehiculeKafkaConsumer                              │
│  • 3 consumers en parallèle (concurrency=3)                  │
│  • Acquittement manuel                                       │
│  • Traite les événements :                                   │
│    - Notifications                                           │
│    - Statistiques                                            │
│    - Synchronisation externe                                 │
│    - Indexation                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## 🚀 Démarrage Rapide

### 1. Démarrer l'infrastructure Kafka

```powershell
# Démarre Zookeeper, Kafka, Kafka UI et PostgreSQL
docker-compose up -d
```

**Services démarrés :**
- 🟢 **Zookeeper** : Port 2181
- 🟢 **Kafka Broker** : Port 9092
- 🟢 **Kafka UI** : http://localhost:8090
- 🟢 **PostgreSQL** : Port 5432
- 🟢 **pgAdmin** : http://localhost:5050

### 2. Vérifier Kafka

```powershell
# Vérifier que Kafka est en cours d'exécution
docker ps | findstr kafka

# Logs Kafka
docker logs renault_kafka

# Accéder à Kafka UI
start http://localhost:8090
```

### 3. Lancer l'application

```powershell
mvn spring-boot:run
```

### 4. Créer un véhicule (déclenche l'événement)

```powershell
$body = @{
    modeleId = "650e8400-e29b-41d4-a716-446655440001"
    brand = "Renault Zoe E-Tech"
    anneeFabrication = 2024
    typeCarburant = "ELECTRIQUE"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/v1/garages/550e8400-e29b-41d4-a716-446655440001/vehicules" `
    -Method POST -ContentType "application/json" -Body $body
```

---

## 📊 Kafka UI - Visualisation

### Accéder à Kafka UI
http://localhost:8090

### Fonctionnalités disponibles :
- 📋 **Topics** : Liste des topics et configuration
- 📊 **Messages** : Visualisation des messages
- 👥 **Consumers** : Groupes de consommateurs et lag
- ⚙️ **Brokers** : État des brokers Kafka

### Vérifier les messages

1. Accéder à Kafka UI : http://localhost:8090
2. Cliquer sur **Topics**
3. Sélectionner `vehicule.created`
4. Onglet **Messages**
5. Voir les événements publiés avec leur contenu JSON

---

## 📝 Configuration Kafka

### application.yml

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    
    consumer:
      group-id: garage-service-group
      auto-offset-reset: earliest      # Lit depuis le début
      enable-auto-commit: false        # Acquittement manuel
      
    producer:
      acks: all                        # Confirmation de tous les replicas
      retries: 3                       # Retry en cas d'échec
      properties:
        enable.idempotence: true       # Évite les doublons
    
    listener:
      ack-mode: manual                 # Contrôle manuel de l'acquittement
```

### KafkaConfig.java

```java
@Configuration
@EnableKafka
public class KafkaConfig {
    
    public static final String VEHICULE_CREATED_TOPIC = "vehicule.created";
    
    @Bean
    public NewTopic vehiculeCreatedTopic() {
        return TopicBuilder.name(VEHICULE_CREATED_TOPIC)
                .partitions(3)           // 3 partitions pour parallélisme
                .replicas(1)             // 1 réplica (dev)
                .config("retention.ms", "604800000")  // 7 jours
                .config("compression.type", "snappy") // Compression
                .build();
    }
}
```

---

## 🔑 Concepts Clés

### 1. Topics
**Topic** : `vehicule.created`
- Canal de communication pour les événements de véhicule
- Créé automatiquement au démarrage
- Configuré avec 3 partitions

### 2. Partitionnement
**Clé de partition** : `garageId`
- Tous les événements d'un même garage vont dans la même partition
- **Garantit l'ordre** des événements pour un garage donné
- Permet la **scalabilité horizontale**

### 3. Consumer Group
**Group ID** : `garage-service-group`
- Tous les consumers du même groupe partagent les partitions
- Chaque message est consommé par **un seul** consumer du groupe
- Permet le **load balancing** automatique

### 4. Acquittement Manuel
- Le consumer acquitte manuellement après traitement réussi
- En cas d'erreur, le message n'est pas acquitté
- Kafka le **réessaiera** automatiquement

### 5. Concurrency
**3 consumers en parallèle**
- Chaque partition est traitée par un consumer dédié
- Maximise le **throughput**
- Configuration : `factory.setConcurrency(3)`

---

## 🔄 Flux Complet

### 1. Publication (Producer)

```java
@Component
public class KafkaDomainEventPublisher implements DomainEventPublisher {
    
    @Override
    public void publish(Object event) {
        String topic = "vehicule.created";
        String key = event.getGarageId().toString();
        
        kafkaTemplate.send(topic, key, event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("✅ Événement publié - partition: {}, offset: {}",
                               result.getRecordMetadata().partition(),
                               result.getRecordMetadata().offset());
                }
            });
    }
}
```

### 2. Consommation (Consumer)

```java
@Component
public class VehiculeKafkaConsumer {
    
    @KafkaListener(topics = "vehicule.created", groupId = "garage-service-group")
    public void onVehiculeCreated(
            @Payload VehiculeCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            // Traiter l'événement
            processEvent(event);
            
            // Acquitter le message
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            // En cas d'erreur, ne pas acquitter
            // Kafka réessaiera automatiquement
            throw new RuntimeException("Erreur de traitement", e);
        }
    }
}
```

---

## 📜 Logs Détaillés

### Exemple de logs lors de la création d'un véhicule :

```
[INFO] VehiculeService - 🚗 Création d'un nouveau véhicule pour le garage 550e8400...
[INFO] KafkaDomainEventPublisher - 📢 [KAFKA PUBLISHER] Publication de l'événement: VehiculeCreatedEvent vers le topic: vehicule.created
[INFO] KafkaDomainEventPublisher - ✅ [KAFKA] Événement publié avec succès sur le topic 'vehicule.created' - partition: 2, offset: 15
[INFO] VehiculeService - ✅ Véhicule créé avec succès: 789e4567...

--- Consumer (Thread kafka-listener-1) ---
[INFO] VehiculeKafkaConsumer - 🚗 [KAFKA CONSUMER] Réception d'un événement VehiculeCreatedEvent
[INFO] VehiculeKafkaConsumer -    📍 Partition: 2, Offset: 15
[INFO] VehiculeKafkaConsumer -    → Véhicule ID: 789e4567...
[INFO] VehiculeKafkaConsumer -    → Garage ID: 550e8400...
[INFO] VehiculeKafkaConsumer -    → Marque: Renault Zoe E-Tech
[INFO] VehiculeKafkaConsumer - ⚙️  [KAFKA] Traitement de l'événement en cours...
[INFO] VehiculeKafkaConsumer - 📧 [Notification] Envoi d'email
[INFO] VehiculeKafkaConsumer - 📊 [Statistiques] Mise à jour: +1 véhicule
[INFO] VehiculeKafkaConsumer - 🔄 [Synchronisation] Mise à jour du système externe
[INFO] VehiculeKafkaConsumer - 🔍 [Indexation] Indexation dans Elasticsearch
[INFO] VehiculeKafkaConsumer - ✅ [KAFKA] Événement traité avec succès
[DEBUG] VehiculeKafkaConsumer - ✅ Message acquitté - partition: 2, offset: 15
```

---

## 🧪 Tests

### Test d'Intégration avec Embedded Kafka

```java
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"vehicule.created"})
class KafkaEventIntegrationTest {
    
    @Test
    void shouldPublishKafkaEventWhenVehiculeIsCreated() throws InterruptedException {
        // Given
        CreateVehiculeRequest request = new CreateVehiculeRequest(...);
        
        // When
        vehiculeService.createVehicule(garageId, request);
        
        // Then
        ConsumerRecord<String, VehiculeCreatedEvent> received = 
            records.poll(10, TimeUnit.SECONDS);
        
        assertThat(received).isNotNull();
        assertThat(received.value().getVehiculeId()).isEqualTo(...);
    }
}
```

### Lancer les tests

```powershell
# Tous les tests
mvn test

# Tests Kafka uniquement
mvn test -Dtest=KafkaEventIntegrationTest
```

---

## 🔍 Monitoring & Debug

### 1. Kafka UI (Recommandé)
http://localhost:8090
- Vue d'ensemble du cluster
- Messages en temps réel
- Consumer lag
- Métriques

### 2. CLI Kafka (via Docker)

```powershell
# Lister les topics
docker exec -it renault_kafka kafka-topics --list --bootstrap-server localhost:9092

# Détails d'un topic
docker exec -it renault_kafka kafka-topics --describe --topic vehicule.created --bootstrap-server localhost:9092

# Consommer des messages (debug)
docker exec -it renault_kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic vehicule.created --from-beginning

# Produire un message (test)
docker exec -it renault_kafka kafka-console-producer --broker-list localhost:9092 --topic vehicule.created
```

### 3. Consumer Groups

```powershell
# Lister les consumer groups
docker exec -it renault_kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list

# Détails d'un group (lag, offset)
docker exec -it renault_kafka kafka-consumer-groups --bootstrap-server localhost:9092 --describe --group garage-service-group
```

---

## ⚙️ Configuration Avancée

### Production-Ready Settings

```yaml
spring:
  kafka:
    bootstrap-servers: kafka1:9092,kafka2:9092,kafka3:9092  # Cluster
    
    producer:
      acks: all
      retries: 10
      max-in-flight-requests-per-connection: 5
      properties:
        enable.idempotence: true
        compression.type: snappy
        linger.ms: 10                    # Batching
        batch.size: 32768                # 32 KB
    
    consumer:
      max-poll-records: 500
      max-poll-interval-ms: 300000       # 5 minutes
      session-timeout-ms: 10000
      heartbeat-interval-ms: 3000
      
    listener:
      concurrency: 10                    # 10 consumers
      ack-mode: manual
```

### Topic avec Réplication (Production)

```java
@Bean
public NewTopic vehiculeCreatedTopic() {
    return TopicBuilder.name("vehicule.created")
            .partitions(10)              // Plus de partitions
            .replicas(3)                 // 3 réplicas pour HA
            .config("min.insync.replicas", "2")  // Minimum 2 replicas sync
            .config("retention.ms", "2592000000") // 30 jours
            .build();
}
```

---

## 🚨 Gestion des Erreurs

### 1. Retry Automatique
Si le consumer échoue, Kafka réessaie automatiquement :
- Le message n'est pas acquitté
- Kafka le renvoie après un délai
- Configurable via `max.poll.interval.ms`

### 2. Dead Letter Topic (DLT)
Pour les messages qui échouent après plusieurs retries :

```java
@Bean
public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory() {
    factory.setCommonErrorHandler(
        new DefaultErrorHandler(
            new DeadLetterPublishingRecoverer(kafkaTemplate),
            new FixedBackOff(1000L, 3L)  // 3 retries avec 1s entre chaque
        )
    );
    return factory;
}
```

---

## 📊 Avantages de Kafka vs Spring Events

| Critère | Spring Events | Apache Kafka |
|---------|---------------|--------------|
| **Persistance** | ❌ Non | ✅ Oui (disque) |
| **Scalabilité** | ⚠️  Limitée | ✅ Excellente |
| **Fiabilité** | ⚠️  En mémoire | ✅ Réplication |
| **Historique** | ❌ Non | ✅ Rétention configurable |
| **Multi-instances** | ❌ Local | ✅ Distribué |
| **Replay** | ❌ Impossible | ✅ Rejouer les messages |
| **Monitoring** | ⚠️  Basique | ✅ Complet |
| **Complexité** | ✅ Simple | ⚠️  Moyenne |
| **Infrastructure** | ✅ Aucune | ⚠️  Kafka requis |

---

## 🎯 Use Cases

### ✅ Quand utiliser Kafka :
- Architecture microservices distribuée
- Besoin de persistance des événements
- Historique et replay nécessaires
- Haute volumétrie d'événements
- Intégration avec d'autres systèmes
- Event Sourcing / CQRS

### ⚠️  Quand utiliser Spring Events :
- Application monolithique
- Événements en mémoire suffisants
- Faible volumétrie
- Prototype rapide

---

## 🚀 Évolutions Futures

### Phase 2 : Schema Registry
```yaml
spring:
  kafka:
    properties:
      schema.registry.url: http://localhost:8081
```
- Validation des schémas avec Avro
- Versioning des événements
- Compatibilité assurée

### Phase 3 : Kafka Streams
- Agrégation en temps réel
- Transformations de flux
- Fenêtrage temporel

### Phase 4 : Kafka Connect
- Intégration avec bases de données
- Synchronisation automatique
- ETL en temps réel

---

## 📞 Ressources

### Documentation
- **Kafka UI** : http://localhost:8090
- **Kafka Docs** : https://kafka.apache.org/documentation/
- **Spring Kafka** : https://spring.io/projects/spring-kafka

### Commandes Utiles
```powershell
# Démarrer l'infrastructure
docker-compose up -d

# Arrêter l'infrastructure
docker-compose down

# Voir les logs Kafka
docker logs -f renault_kafka

# Nettoyer les données
docker-compose down -v
```

---

**✅ Système Kafka opérationnel et production-ready !** 🎉
