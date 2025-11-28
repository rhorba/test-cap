package com.renault.garage.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renault.garage.application.dto.*;
import com.renault.garage.application.service.GarageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GarageController.class)
class GarageControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private GarageService garageService;
    
    @Test
    @DisplayName("POST /api/v1/garages devrait créer un garage")
    void shouldCreateGarage() throws Exception {
        // Arrange
        CreateGarageRequest request = createValidRequest();
        GarageResponse response = createValidResponse();
        
        when(garageService.createGarage(any(CreateGarageRequest.class)))
            .thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/garages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Renault Paris"))
            .andExpect(jsonPath("$.email").value("paris@renault.fr"))
            .andExpect(jsonPath("$.nombreVehicules").value(0))
            .andExpect(jsonPath("$.capaciteRestante").value(50));
    }
    
    @Test
    @DisplayName("GET /api/v1/garages/{id} devrait retourner un garage")
    void shouldGetGarageById() throws Exception {
        // Arrange
        UUID garageId = UUID.randomUUID();
        GarageResponse response = createValidResponse();
        
        when(garageService.getGarageById(garageId)).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(get("/api/v1/garages/{id}", garageId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Renault Paris"));
    }
    
    @Test
    @DisplayName("POST /api/v1/garages devrait retourner 400 si validation échoue")
    void shouldReturn400WhenValidationFails() throws Exception {
        // Arrange - Requête avec email invalide
        CreateGarageRequest invalidRequest = new CreateGarageRequest(
            "Garage Test",
            new AddressDTO("Rue", "Ville", "Code", "Pays"),
            "+33123456789",
            "email-invalide",  // Email invalide
            Map.of(DayOfWeek.MONDAY, List.of(
                new OpeningTimeDTO(LocalTime.of(8, 0), LocalTime.of(18, 0))
            ))
        );
        
        // Act & Assert
        mockMvc.perform(post("/api/v1/garages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
    
    @Test
    @DisplayName("DELETE /api/v1/garages/{id} devrait supprimer un garage")
    void shouldDeleteGarage() throws Exception {
        // Arrange
        UUID garageId = UUID.randomUUID();
        
        // Act & Assert
        mockMvc.perform(delete("/api/v1/garages/{id}", garageId))
            .andExpect(status().isNoContent());
    }
    
    // Méthodes utilitaires
    private CreateGarageRequest createValidRequest() {
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
    
    private GarageResponse createValidResponse() {
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
