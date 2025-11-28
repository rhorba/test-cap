package com.renault.garage.infrastructure.event;

import com.renault.garage.application.dto.CreateVehiculeRequest;
import com.renault.garage.application.dto.VehiculeResponse;
import com.renault.garage.application.service.VehiculeService;
import com.renault.garage.domain.event.VehiculeCreatedEvent;
import com.renault.garage.domain.model.TypeCarburant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test d'intégration du système de publication/consommation d'événements
 * Vérifie que l'événement est bien publié et consommé lors de la création d'un véhicule
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EventPublishingIntegrationTest {
    
    @Autowired
    private VehiculeService vehiculeService;
    
    @SpyBean
    private VehiculeEventListener vehiculeEventListener;
    
    private UUID garageId;
    
    @BeforeEach
    void setUp() {
        // Utiliser l'ID d'un garage existant dans les données de test
        garageId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    }
    
    @Test
    @DisplayName("Devrait publier et consommer l'événement lors de la création d'un véhicule")
    void shouldPublishAndConsumeEventWhenVehiculeIsCreated() throws Exception {
        // Given
        CreateVehiculeRequest request = new CreateVehiculeRequest(
            UUID.fromString("650e8400-e29b-41d4-a716-446655440001"), // modeleId existant
            "Renault Megane E-Tech",
            2024,
            TypeCarburant.ELECTRIQUE
        );
        
        // When
        VehiculeResponse response = vehiculeService.createVehicule(garageId, request);
        
        // Then - Vérifier que le véhicule a été créé
        assert response != null;
        assert response.id() != null;
        
        // Attendre un peu pour le traitement asynchrone
        Thread.sleep(1000);
        
        // Vérifier que le listener a bien été appelé avec l'événement
        verify(vehiculeEventListener, timeout(2000).atLeastOnce())
            .onVehiculeCreated(any(VehiculeCreatedEvent.class));
    }
}
