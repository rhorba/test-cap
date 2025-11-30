package com.renault.garage.infrastructure.event;

import com.renault.garage.domain.event.DomainEventPublisher;
import com.renault.garage.infrastructure.config.KafkaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Implémentation Kafka du publisher d'événements domaine
 * Publie les événements vers les topics Kafka
 */
@Component
public class KafkaDomainEventPublisher implements DomainEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaDomainEventPublisher.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;

    public KafkaDomainEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                     ApplicationEventPublisher applicationEventPublisher) {
        this.kafkaTemplate = kafkaTemplate;
        this.applicationEventPublisher = applicationEventPublisher;
    }
    
    @Override
    public void publish(Object event) {
        String topic = determineTopicFromEvent(event);
        String key = extractKeyFromEvent(event);
        
        logger.info("📢 [KAFKA PUBLISHER] Publication de l'événement: {} vers le topic: {}", 
                    event.getClass().getSimpleName(), topic);
        logger.debug("Détails de l'événement: {}", event);

        // Publier également un événement Spring interne pour les listeners @EventListener
        try {
            applicationEventPublisher.publishEvent(event);
            logger.debug("📣 Événement Spring publié: {}", event.getClass().getSimpleName());
        } catch (Exception e) {
            logger.warn("⚠️  Impossible de publier l'événement Spring: {}", e.getMessage());
        }
        
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("✅ [KAFKA] Événement publié avec succès sur le topic '{}' - partition: {}, offset: {}", 
                           topic, 
                           result.getRecordMetadata().partition(), 
                           result.getRecordMetadata().offset());
            } else {
                logger.error("❌ [KAFKA] Échec de publication de l'événement: {}", ex.getMessage(), ex);
            }
        });
    }
    
    /**
     * Détermine le topic Kafka en fonction du type d'événement
     */
    private String determineTopicFromEvent(Object event) {
        String eventClassName = event.getClass().getSimpleName();
        
        // Mapping des événements vers les topics
        switch (eventClassName) {
            case "VehiculeCreatedEvent":
                return KafkaConfig.VEHICULE_CREATED_TOPIC;
            default:
                logger.warn("⚠️  Type d'événement inconnu: {}, utilisation du topic par défaut", eventClassName);
                return "garage.events.unknown";
        }
    }
    
    /**
     * Extrait une clé de partitionnement de l'événement
     * Permet de garantir l'ordre des événements pour une même clé
     */
    private String extractKeyFromEvent(Object event) {
        // Pour VehiculeCreatedEvent, on utilise le garageId comme clé
        // Tous les événements d'un même garage iront dans la même partition
        if (event.getClass().getSimpleName().equals("VehiculeCreatedEvent")) {
            try {
                var method = event.getClass().getMethod("getGarageId");
                Object garageId = method.invoke(event);
                return garageId != null ? garageId.toString() : "unknown";
            } catch (Exception e) {
                logger.warn("Impossible d'extraire la clé de l'événement: {}", e.getMessage());
            }
        }
        return "default";
    }
}
