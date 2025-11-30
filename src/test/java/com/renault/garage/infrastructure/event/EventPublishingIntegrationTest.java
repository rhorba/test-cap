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
    
    @Autowired
    private com.renault.garage.domain.repository.GarageRepository garageRepository;
    
    
    private UUID garageId;
    
    @BeforeEach
    void setUp() {
        // Créer un garage réel en base de test (H2) pour le scénario
        var horaires = new java.util.EnumMap<java.time.DayOfWeek, java.util.List<com.renault.garage.application.dto.OpeningTimeDTO>>(java.time.DayOfWeek.class);
        horaires.put(java.time.DayOfWeek.MONDAY, java.util.List.of(new com.renault.garage.application.dto.OpeningTimeDTO(java.time.LocalTime.of(8,0), java.time.LocalTime.of(18,0))));

        var address = new com.renault.garage.application.dto.AddressDTO("1 Rue Test","Rabat","10000","MA");

        // Mapper minimal pour construire le domaine
        var addrDomain = new com.renault.garage.domain.model.Address(address.rue(), address.ville(), address.codePostal(), address.pays());
        var horairesDomain = new java.util.EnumMap<java.time.DayOfWeek, java.util.List<com.renault.garage.domain.model.OpeningTime>>(java.time.DayOfWeek.class);
        horairesDomain.put(java.time.DayOfWeek.MONDAY, java.util.List.of(new com.renault.garage.domain.model.OpeningTime(java.time.LocalTime.of(8,0), java.time.LocalTime.of(18,0))));

        var garage = new com.renault.garage.domain.model.Garage("Garage Test IT", addrDomain, "+212600000001", "garage.it@renault.ma", horairesDomain);
        var saved = garageRepository.save(garage);
        garageId = saved.getId();
    }
    
    @Test
    @DisplayName("Devrait publier et consommer l'événement lors de la création d'un véhicule")
    void shouldPublishAndConsumeEventWhenVehiculeIsCreated() {
        // Étant donné
        CreateVehiculeRequest request = new CreateVehiculeRequest(
            UUID.fromString("650e8400-e29b-41d4-a716-446655440001"), // modeleId existant
            "Renault Megane E-Tech",
            2024,
            TypeCarburant.ELECTRIQUE
        );
        
        // Quand
        VehiculeResponse response = vehiculeService.createVehicule(garageId, request);
        
        // Alors - Vérifier que le véhicule a été créé
        assert response != null;
        assert response.id() != null;
        
        // Le verify(..., timeout(...)) ci-dessous attend déjà l'appel asynchrone
        
        // Vérifier que le listener a bien été appelé avec l'événement
        verify(vehiculeEventListener, timeout(2000).atLeastOnce())
            .onVehiculeCreated(any(VehiculeCreatedEvent.class));
    }
}
