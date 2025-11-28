package com.renault.garage.infrastructure.event;

import com.renault.garage.domain.event.VehiculeCreatedEvent;
import com.renault.garage.infrastructure.config.KafkaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consumer Kafka des événements de véhicule
 * Écoute le topic Kafka et traite les événements de manière asynchrone
 */
@Component
public class VehiculeKafkaConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(VehiculeKafkaConsumer.class);
    
    /**
     * Consomme les événements de création de véhicule depuis Kafka
     * 
     * @param event L'événement reçu
     * @param partition La partition Kafka
     * @param offset L'offset du message
     * @param acknowledgment Pour l'acquittement manuel
     */
    @KafkaListener(
        topics = KafkaConfig.VEHICULE_CREATED_TOPIC,
        groupId = "${spring.kafka.consumer.group-id:garage-service-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onVehiculeCreated(
            @Payload VehiculeCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("🚗 [KAFKA CONSUMER] Réception d'un événement VehiculeCreatedEvent");
            logger.info("   📍 Partition: {}, Offset: {}", partition, offset);
            logger.info("   → Véhicule ID: {}", event.getVehiculeId());
            logger.info("   → Garage ID: {}", event.getGarageId());
            logger.info("   → Marque: {}", event.getBrand());
            logger.info("   → Année: {}", event.getAnneeFabrication());
            logger.info("   → Carburant: {}", event.getTypeCarburant());
            logger.info("   → Créé le: {}", event.getOccurredOn());
            
            // Traitement de l'événement
            processVehiculeCreation(event);
            
            // Acquittement manuel après traitement réussi
            if (acknowledgment != null) {
                acknowledgment.acknowledge();
                logger.debug("✅ Message acquitté - partition: {}, offset: {}", partition, offset);
            }
            
        } catch (Exception e) {
            logger.error("❌ [KAFKA CONSUMER] Erreur lors du traitement de l'événement: {}", 
                        e.getMessage(), e);
            // En cas d'erreur, on ne fait pas acknowledge()
            // Le message sera retraité selon la configuration Kafka
            throw new RuntimeException("Erreur de traitement de l'événement", e);
        }
    }
    
    /**
     * Traite la création du véhicule
     * Exemples de traitements possibles :
     * - Envoi d'une notification email
     * - Mise à jour d'un système externe
     * - Déclenchement d'un workflow
     * - Mise à jour de statistiques
     * - Indexation dans Elasticsearch
     */
    private void processVehiculeCreation(VehiculeCreatedEvent event) {
        logger.info("⚙️  [KAFKA] Traitement de l'événement en cours...");
        
        try {
            // Exemple 1: Envoi de notification
            sendNotification(event);
            
            // Exemple 2: Mise à jour des statistiques
            updateStatistics(event);
            
            // Exemple 3: Synchronisation avec système externe
            syncWithExternalSystem(event);
            
            // Exemple 4: Indexation
            indexVehicule(event);
            
            logger.info("✅ [KAFKA] Événement traité avec succès pour le véhicule {}", 
                       event.getVehiculeId());
            
        } catch (Exception e) {
            logger.error("❌ Erreur lors du traitement métier: {}", e.getMessage(), e);
            throw e; // Relance l'exception pour déclencher le retry Kafka
        }
    }
    
    private void sendNotification(VehiculeCreatedEvent event) {
        logger.info("📧 [Notification] Envoi d'email pour le nouveau véhicule {} dans le garage {}", 
                    event.getBrand(), event.getGarageId());
        // Implémentation de l'envoi de notification
        // Ex: emailService.send(...)
    }
    
    private void updateStatistics(VehiculeCreatedEvent event) {
        logger.info("📊 [Statistiques] Mise à jour: +1 véhicule {} ({})", 
                    event.getBrand(), event.getTypeCarburant());
        // Implémentation de la mise à jour des stats
        // Ex: statisticsService.incrementVehicleCount(...)
    }
    
    private void syncWithExternalSystem(VehiculeCreatedEvent event) {
        logger.info("🔄 [Synchronisation] Mise à jour du système externe pour le véhicule {}", 
                    event.getVehiculeId());
        // Implémentation de la synchronisation
        // Ex: externalSystemClient.syncVehicule(...)
    }
    
    private void indexVehicule(VehiculeCreatedEvent event) {
        logger.info("🔍 [Indexation] Indexation du véhicule {} dans Elasticsearch", 
                    event.getVehiculeId());
        // Implémentation de l'indexation
        // Ex: elasticsearchService.index(...)
    }
}
