package com.renault.garage.infrastructure.event;

import com.renault.garage.application.dto.CreateVehiculeRequest;
import com.renault.garage.application.service.VehiculeService;
import com.renault.garage.domain.event.VehiculeCreatedEvent;
import com.renault.garage.domain.model.TypeCarburant;
import com.renault.garage.infrastructure.config.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test d'intégration Kafka pour la publication/consommation d'événements
 * Utilise un broker Kafka embarqué pour les tests
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext
@EmbeddedKafka(
    partitions = 1,
    topics = {KafkaConfig.VEHICULE_CREATED_TOPIC},
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
class KafkaEventIntegrationTest {
    
    @Autowired
    private VehiculeService vehiculeService;
    
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;
    
    private BlockingQueue<ConsumerRecord<String, VehiculeCreatedEvent>> records;
    private KafkaMessageListenerContainer<String, VehiculeCreatedEvent> container;
    
    private UUID garageId;
    
    @BeforeEach
    void setUp() {
        garageId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
        
        // Configuration du consumer de test
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configs.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        configs.put(JsonDeserializer.VALUE_DEFAULT_TYPE, VehiculeCreatedEvent.class.getName());
        
        DefaultKafkaConsumerFactory<String, VehiculeCreatedEvent> consumerFactory =
                new DefaultKafkaConsumerFactory<>(configs);
        
        ContainerProperties containerProperties = new ContainerProperties(KafkaConfig.VEHICULE_CREATED_TOPIC);
        
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        
        container.setupMessageListener((MessageListener<String, VehiculeCreatedEvent>) records::add);
        container.start();
        
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
    }
    
    @Test
    @DisplayName("Devrait publier un événement Kafka lors de la création d'un véhicule")
    @Transactional
    void shouldPublishKafkaEventWhenVehiculeIsCreated() throws InterruptedException {
        // Given
        CreateVehiculeRequest request = new CreateVehiculeRequest(
            UUID.fromString("650e8400-e29b-41d4-a716-446655440001"),
            "Renault Megane E-Tech Electric",
            2024,
            TypeCarburant.ELECTRIQUE
        );
        
        // When
        var response = vehiculeService.createVehicule(garageId, request);
        
        // Then
        ConsumerRecord<String, VehiculeCreatedEvent> received = records.poll(10, TimeUnit.SECONDS);
        
        assertThat(received).isNotNull();
        assertThat(received.topic()).isEqualTo(KafkaConfig.VEHICULE_CREATED_TOPIC);
        
        VehiculeCreatedEvent event = received.value();
        assertThat(event).isNotNull();
        assertThat(event.getVehiculeId()).isEqualTo(response.id());
        assertThat(event.getGarageId()).isEqualTo(garageId);
        assertThat(event.getBrand()).isEqualTo("Renault Megane E-Tech Electric");
        assertThat(event.getAnneeFabrication()).isEqualTo(2024);
        assertThat(event.getTypeCarburant()).isEqualTo("ELECTRIQUE");
        assertThat(event.getOccurredOn()).isNotNull();
    }
}
