package com.renault.garage.infrastructure.rest;

import com.renault.garage.application.dto.*;
import com.renault.garage.application.service.VehiculeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller - Gestion des véhicules
 */
@RestController
@RequestMapping("/api/v1/garages/{garageId}/vehicules")
@Tag(name = "Véhicules", description = "API de gestion des véhicules dans les garages")
public class VehiculeController {
    
    private final VehiculeService vehiculeService;
    
    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }
    
    @PostMapping
    @Operation(summary = "Ajouter un véhicule à un garage", 
               description = "Crée un nouveau véhicule dans un garage. Vérifie la capacité du garage.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Véhicule ajouté avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou garage plein"),
        @ApiResponse(responseCode = "404", description = "Garage non trouvé")
    })
    public ResponseEntity<VehiculeResponse> createVehicule(
            @PathVariable UUID garageId,
            @Valid @RequestBody CreateVehiculeRequest request) {
        VehiculeResponse response = vehiculeService.createVehicule(garageId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    @Operation(summary = "Lister les véhicules d'un garage", 
               description = "Retourne tous les véhicules d'un garage donné")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des véhicules récupérée"),
        @ApiResponse(responseCode = "404", description = "Garage non trouvé")
    })
    public ResponseEntity<List<VehiculeResponse>> getVehiculesByGarage(
            @PathVariable UUID garageId) {
        List<VehiculeResponse> vehicules = vehiculeService.getVehiculesByGarageId(garageId);
        return ResponseEntity.ok(vehicules);
    }
    
    @GetMapping("/{vehiculeId}")
    @Operation(summary = "Récupérer un véhicule par son ID", 
               description = "Retourne les détails d'un véhicule spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
        @ApiResponse(responseCode = "404", description = "Véhicule non trouvé")
    })
    public ResponseEntity<VehiculeResponse> getVehiculeById(
            @PathVariable UUID garageId,
            @PathVariable UUID vehiculeId) {
        VehiculeResponse response = vehiculeService.getVehiculeById(vehiculeId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{vehiculeId}")
    @Operation(summary = "Mettre à jour un véhicule", 
               description = "Met à jour les informations d'un véhicule existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Véhicule mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Véhicule non trouvé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<VehiculeResponse> updateVehicule(
            @PathVariable UUID garageId,
            @PathVariable UUID vehiculeId,
            @Valid @RequestBody UpdateVehiculeRequest request) {
        VehiculeResponse response = vehiculeService.updateVehicule(vehiculeId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{vehiculeId}")
    @Operation(summary = "Supprimer un véhicule", 
               description = "Supprime un véhicule d'un garage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Véhicule supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Véhicule ou garage non trouvé")
    })
    public ResponseEntity<Void> deleteVehicule(
            @PathVariable UUID garageId,
            @PathVariable UUID vehiculeId) {
        vehiculeService.deleteVehicule(garageId, vehiculeId);
        return ResponseEntity.noContent().build();
    }
}
