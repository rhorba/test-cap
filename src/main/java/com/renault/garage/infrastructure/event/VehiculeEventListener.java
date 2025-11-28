package com.renault.garage.infrastructure.event;

import com.renault.garage.domain.event.VehiculeCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Consumer (Listener) des événements de véhicule
 * Traite les événements de manière asynchrone
 */
@Component
public class VehiculeEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(VehiculeEventListener.class);
    
    /**
     * Consomme l'événement de création de véhicule
     * Traitement asynchrone pour ne pas bloquer le thread principal
     */
    @Async
    @EventListener
    public void onVehiculeCreated(VehiculeCreatedEvent event) {
        logger.info("🚗 [CONSUMER] Réception d'un événement VehiculeCreatedEvent");
        logger.info("   → Véhicule ID: {}", event.getVehiculeId());
        logger.info("   → Garage ID: {}", event.getGarageId());
        logger.info("   → Marque: {}", event.getBrand());
        logger.info("   → Année: {}", event.getAnneeFabrication());
        logger.info("   → Carburant: {}", event.getTypeCarburant());
        logger.info("   → Créé le: {}", event.getOccurredOn());
        
        // Logique de traitement de l'événement
        processVehiculeCreation(event);
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
        try {
            // Simulation de traitement asynchrone
            logger.info("⚙️  Traitement de l'événement en cours...");
            
            // Exemple 1: Envoi de notification
            sendNotification(event);
            
            // Exemple 2: Mise à jour des statistiques
            updateStatistics(event);
            
            // Exemple 3: Synchronisation avec système externe
            syncWithExternalSystem(event);
            
            logger.info("✅ Événement traité avec succès pour le véhicule {}", event.getVehiculeId());
            
        } catch (Exception e) {
            logger.error("❌ Erreur lors du traitement de l'événement: {}", e.getMessage(), e);
            // Logique de gestion d'erreur (retry, dead letter queue, etc.)
        }
    }
    
    private void sendNotification(VehiculeCreatedEvent event) {
        logger.info("📧 Envoi de notification pour le nouveau véhicule {} dans le garage {}", 
                    event.getBrand(), event.getGarageId());
        // Implémentation de l'envoi de notification
    }
    
    private void updateStatistics(VehiculeCreatedEvent event) {
        logger.info("📊 Mise à jour des statistiques: +1 véhicule {} ({})", 
                    event.getBrand(), event.getTypeCarburant());
        // Implémentation de la mise à jour des stats
    }
    
    private void syncWithExternalSystem(VehiculeCreatedEvent event) {
        logger.info("🔄 Synchronisation avec le système externe pour le véhicule {}", 
                    event.getVehiculeId());
        // Implémentation de la synchronisation
    }
}
