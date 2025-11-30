package com.renault.garage.application.service;

import com.renault.garage.application.dto.CreateVehiculeRequest;
import com.renault.garage.application.mapper.VehiculeMapper;
import com.renault.garage.domain.event.DomainEventPublisher;
import com.renault.garage.domain.exception.CapaciteGarageDepasseeException;
import com.renault.garage.domain.exception.GarageNotFoundException;
import com.renault.garage.domain.model.Address;
import com.renault.garage.domain.model.Garage;
import com.renault.garage.domain.model.TypeCarburant;
import com.renault.garage.domain.model.Vehicule;
import com.renault.garage.domain.repository.GarageRepository;
import com.renault.garage.domain.repository.VehiculeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehiculeServiceTest {

    private VehiculeRepository vehiculeRepository;
    private GarageRepository garageRepository;
    private VehiculeMapper vehiculeMapper;
    private DomainEventPublisher eventPublisher;
    private VehiculeService vehiculeService;

    private UUID garageId;
    private Garage garage;

    @BeforeEach
    void setup() {
        vehiculeRepository = mock(VehiculeRepository.class);
        garageRepository = mock(GarageRepository.class);
        vehiculeMapper = new VehiculeMapper();
        eventPublisher = mock(DomainEventPublisher.class);
        vehiculeService = new VehiculeService(vehiculeRepository, garageRepository, vehiculeMapper, eventPublisher);

        garageId = UUID.randomUUID();
        garage = new Garage(
            "Garage Test",
            new Address("Rue 1", "Ville", "12345", "Pays"),
            "+212600000000",
            "garage@test.com",
            Map.of(DayOfWeek.MONDAY, List.of())
        );
        garage.setId(garageId);
    }

    @Test
    void createVehicule_publishesEvent_andPersists() {
        when(garageRepository.findById(garageId)).thenReturn(Optional.of(garage));
        when(vehiculeRepository.save(Mockito.any(Vehicule.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(garageRepository.save(Mockito.any(Garage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateVehiculeRequest req = new CreateVehiculeRequest(
            UUID.randomUUID(),
            "RENAULT",
            2024,
            TypeCarburant.ESSENCE
        );

        var response = vehiculeService.createVehicule(garageId, req);

        assertNotNull(response);
        assertEquals("RENAULT", response.brand());
        verify(vehiculeRepository, times(1)).save(Mockito.any(Vehicule.class));
        verify(garageRepository, times(1)).save(Mockito.any(Garage.class));

        // Vérifier que l'événement a été publié
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(1)).publish(eventCaptor.capture());
        Object event = eventCaptor.getValue();
        assertEquals("VehiculeCreatedEvent", event.getClass().getSimpleName());
    }

    @Test
    void createVehicule_throwsWhenGarageNotFound() {
        when(garageRepository.findById(garageId)).thenReturn(Optional.empty());

        CreateVehiculeRequest req = new CreateVehiculeRequest(
            UUID.randomUUID(), "RENAULT", 2024, TypeCarburant.ESSENCE
        );

        assertThrows(GarageNotFoundException.class, () -> vehiculeService.createVehicule(garageId, req));
        verify(eventPublisher, times(0)).publish(any());
    }

    @Test
    void createVehicule_respectsGarageCapacity() {
        // Remplir le garage à capacité maximale
        when(garageRepository.findById(garageId)).thenReturn(Optional.of(garage));
        for (int i = 0; i < Garage.getMaxCapacity(); i++) {
            garage.ajouterVehicule(new Vehicule(UUID.randomUUID(), "B", 2020, TypeCarburant.DIESEL));
        }

        CreateVehiculeRequest req = new CreateVehiculeRequest(
            UUID.randomUUID(), "RENAULT", 2024, TypeCarburant.ESSENCE
        );

        assertThrows(CapaciteGarageDepasseeException.class, () -> vehiculeService.createVehicule(garageId, req));
        verify(eventPublisher, times(0)).publish(any());
    }
}
