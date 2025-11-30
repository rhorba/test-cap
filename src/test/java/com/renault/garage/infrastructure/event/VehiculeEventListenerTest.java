package com.renault.garage.infrastructure.event;

import com.renault.garage.domain.event.VehiculeCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour le VehiculeEventListener
 */
@ExtendWith(MockitoExtension.class)
class VehiculeEventListenerTest {
    
    @InjectMocks
    private VehiculeEventListener vehiculeEventListener;
    
    private VehiculeCreatedEvent event;
    
    @BeforeEach
    void setUp() {
        UUID vehiculeId = UUID.randomUUID();
        UUID garageId = UUID.randomUUID();
        
        event = new VehiculeCreatedEvent(
            vehiculeId,
            garageId,
            "Renault Clio",
            2024,
            "ESSENCE"
        );
    }
    
    @Test
    @DisplayName("Devrait consommer l'événement VehiculeCreatedEvent sans erreur")
    void shouldConsumeVehiculeCreatedEventSuccessfully() {
        // Action & Vérification - Ne devrait pas lever d'exception
        assertDoesNotThrow(() -> vehiculeEventListener.onVehiculeCreated(event));
    }
    
    @Test
    @DisplayName("L'événement devrait contenir les bonnes informations")
    void shouldContainCorrectEventInformation() {
        // Vérification
        assertNotNull(event.getVehiculeId());
        assertNotNull(event.getGarageId());
        assertEquals("Renault Clio", event.getBrand());
        assertEquals(2024, event.getAnneeFabrication());
        assertEquals("ESSENCE", event.getTypeCarburant());
        assertNotNull(event.getOccurredOn());
    }
}
