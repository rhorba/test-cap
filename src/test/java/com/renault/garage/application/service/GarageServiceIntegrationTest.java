package com.renault.garage.application.service;

import com.renault.garage.application.dto.*;
import com.renault.garage.domain.exception.GarageNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour GarageService
 */
@SpringBootTest
@Transactional
class GarageServiceIntegrationTest {
    
    @Autowired
    private GarageService garageService;
    
    @Test
    void shouldCreateGarage() {
        // Given
        CreateGarageRequest request = new CreateGarageRequest(
            "Renault Paris Test",
            new AddressDTO("123 Rue Test", "Paris", "75001", "France"),
            "+33123456789",
            "test@renault.fr",
            Map.of(
                DayOfWeek.MONDAY, List.of(
                    new OpeningTimeDTO(LocalTime.of(8, 0), LocalTime.of(18, 0))
                )
            )
        );
        
        // When
        GarageResponse response = garageService.createGarage(request);
        
        // Then
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("Renault Paris Test", response.name());
        assertEquals("Paris", response.address().ville());
        assertEquals(0, response.nombreVehicules());
        assertEquals(50, response.capaciteRestante());
    }
    
    @Test
    void shouldGetGarageById() {
        // Given
        CreateGarageRequest request = new CreateGarageRequest(
            "Renault Lyon",
            new AddressDTO("456 Rue Lyon", "Lyon", "69000", "France"),
            "+33987654321",
            "lyon@renault.fr",
            Map.of(
                DayOfWeek.TUESDAY, List.of(
                    new OpeningTimeDTO(LocalTime.of(9, 0), LocalTime.of(17, 0))
                )
            )
        );
        GarageResponse created = garageService.createGarage(request);
        
        // When
        GarageResponse found = garageService.getGarageById(created.id());
        
        // Then
        assertNotNull(found);
        assertEquals(created.id(), found.id());
        assertEquals("Renault Lyon", found.name());
    }
    
    @Test
    void shouldThrowExceptionWhenGarageNotFound() {
        // When & Then
        assertThrows(GarageNotFoundException.class, () -> {
            garageService.getGarageById(java.util.UUID.randomUUID());
        });
    }
}
