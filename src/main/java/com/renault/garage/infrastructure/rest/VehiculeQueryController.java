package com.renault.garage.infrastructure.rest;

import com.renault.garage.application.dto.VehiculeResponse;
import com.renault.garage.application.mapper.VehiculeMapper;
import com.renault.garage.domain.repository.VehiculeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller - Requêtes transversales véhicules
 */
@RestController
@RequestMapping("/api/v1/vehicules")
@Tag(name = "Véhicules - Requêtes", description = "API de requêtes véhicules entre garages")
public class VehiculeQueryController {

    private final VehiculeRepository vehiculeRepository;
    private final VehiculeMapper vehiculeMapper;

    public VehiculeQueryController(VehiculeRepository vehiculeRepository, VehiculeMapper vehiculeMapper) {
        this.vehiculeRepository = vehiculeRepository;
        this.vehiculeMapper = vehiculeMapper;
    }

    @GetMapping
    @Operation(summary = "Lister les véhicules par modèle",
               description = "Retourne tous les véhicules d'un modèle donné, tous garages confondus")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des véhicules récupérée")
    })
    public ResponseEntity<List<VehiculeResponse>> getVehiculesByModeleId(
            @RequestParam(name = "modeleId") String modeleId) {
        try {
            UUID parsed = UUID.fromString(modeleId);
            List<VehiculeResponse> responses = vehiculeRepository.findByModeleId(parsed)
                    .stream()
                    .map(vehiculeMapper::toResponse)
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException ex) {
            // UUID invalide -> 400 Bad Request au lieu de 500
            return ResponseEntity.badRequest().build();
        }
    }
}
