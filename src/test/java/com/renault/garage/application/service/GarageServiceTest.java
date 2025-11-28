package com.renault.garage.application.service;

import com.renault.garage.application.dto.*;
import com.renault.garage.application.mapper.GarageMapper;
import com.renault.garage.domain.exception.GarageNotFoundException;
import com.renault.garage.domain.model.Garage;
import com.renault.garage.domain.model.Address;
import com.renault.garage.domain.model.OpeningTime;
import com.renault.garage.domain.repository.GarageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarageServiceTest {
    
    @Mock
    private GarageRepository garageRepository;
    
    @Mock
    private GarageMapper garageMapper;
    
    @InjectMocks
    private GarageService garageService;
    
    private CreateGarageRequest createRequest;
    private Garage garage;
    private GarageResponse garageResponse;
    
    @BeforeEach
    void setUp() {
        createRequest = createTestRequest();
        garage = createTestGarage();
        garageResponse = createTestResponse();
    }
    
    @Test
    @DisplayName("Devrait créer un garage avec succès")
    void shouldCreateGarageSuccessfully() {
        // Arrange
        when(garageMapper.toDomain(createRequest)).thenReturn(garage);
        when(garageRepository.save(garage)).thenReturn(garage);
        when(garageMapper.toResponse(garage)).thenReturn(garageResponse);
        
        // Act
        GarageResponse result = garageService.createGarage(createRequest);
        
        // Assert
        assertNotNull(result);
        assertEquals("Renault Paris", result.name());
        verify(garageRepository, times(1)).save(garage);
    }
    
    @Test
    @DisplayName("Devrait récupérer un garage par ID")
    void shouldGetGarageById() {
        // Arrange
        UUID garageId = UUID.randomUUID();
        when(garageRepository.findById(garageId)).thenReturn(Optional.of(garage));
        when(garageMapper.toResponse(garage)).thenReturn(garageResponse);
        
        // Act
        GarageResponse result = garageService.getGarageById(garageId);
        
        // Assert
        assertNotNull(result);
        assertEquals("Renault Paris", result.name());
        verify(garageRepository, times(1)).findById(garageId);
    }
    
    @Test
    @DisplayName("Devrait lever GarageNotFoundException si garage introuvable")
    void shouldThrowGarageNotFoundExceptionWhenNotFound() {
        // Arrange
        UUID garageId = UUID.randomUUID();
        when(garageRepository.findById(garageId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(GarageNotFoundException.class, () -> {
            garageService.getGarageById(garageId);
        });
    }
    
    @Test
    @DisplayName("Devrait récupérer tous les garages avec pagination")
    void shouldGetAllGaragesWithPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Garage> garagePage = new PageImpl<>(List.of(garage));
        when(garageRepository.findAll(pageable)).thenReturn(garagePage);
        when(garageMapper.toResponse(any(Garage.class))).thenReturn(garageResponse);
        
        // Act
        GarageListResponse result = garageService.getAllGarages(pageable);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.garages().size());
        assertEquals(0, result.currentPage());
        assertEquals(1, result.totalPages());
    }
    
    @Test
    @DisplayName("Devrait supprimer un garage")
    void shouldDeleteGarage() {
        // Arrange
        UUID garageId = UUID.randomUUID();
        when(garageRepository.existsById(garageId)).thenReturn(true);
        
        // Act
        garageService.deleteGarage(garageId);
        
        // Assert
        verify(garageRepository, times(1)).deleteById(garageId);
    }
    
    // Méthodes utilitaires
    private CreateGarageRequest createTestRequest() {
        AddressDTO address = new AddressDTO(
            "123 Avenue des Champs",
            "Paris",
            "75008",
            "France"
        );
        
        Map<DayOfWeek, List<OpeningTimeDTO>> horaires = new HashMap<>();
        horaires.put(DayOfWeek.MONDAY, List.of(
            new OpeningTimeDTO(LocalTime.of(8, 0), LocalTime.of(18, 0))
        ));
        
        return new CreateGarageRequest(
            "Renault Paris",
            address,
            "+33123456789",
            "paris@renault.fr",
            horaires
        );
    }
    
    private Garage createTestGarage() {
        Address address = new Address("123 Avenue", "Paris", "75008", "France");
        Map<DayOfWeek, List<OpeningTime>> horaires = new HashMap<>();
        horaires.put(DayOfWeek.MONDAY, List.of(
            new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0))
        ));
        return new Garage(
            "Renault Paris",
            address,
            "+33123456789",
            "paris@renault.fr",
            horaires
        );
    }
    
    private GarageResponse createTestResponse() {
        AddressDTO address = new AddressDTO("123 Avenue", "Paris", "75008", "France");
        Map<DayOfWeek, List<OpeningTimeDTO>> horaires = new HashMap<>();
        horaires.put(DayOfWeek.MONDAY, List.of(
            new OpeningTimeDTO(LocalTime.of(8, 0), LocalTime.of(18, 0))
        ));
        return new GarageResponse(
            UUID.randomUUID(),
            "Renault Paris",
            address,
            "+33123456789",
            "paris@renault.fr",
            horaires,
            0,
            50,
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
        );
    }
}
