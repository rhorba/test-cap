package com.renault.garage.infrastructure.rest;

import com.renault.garage.application.dto.*;
import com.renault.garage.application.service.GarageService;
import com.renault.garage.domain.model.TypeCarburant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller - Gestion des garages
 */
@RestController
@RequestMapping("/api/v1/garages")
@Tag(name = "Garages", description = "API de gestion des garages Renault")
public class GarageController {
    
    private final GarageService garageService;
    
    public GarageController(GarageService garageService) {
        this.garageService = garageService;
    }
    
    @PostMapping
    @Operation(summary = "Créer un nouveau garage", description = "Crée un nouveau garage Renault avec toutes ses informations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Garage créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<GarageResponse> createGarage(
            @Valid @RequestBody CreateGarageRequest request) {
        GarageResponse response = garageService.createGarage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un garage par son ID", description = "Retourne les détails complets d'un garage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Garage trouvé"),
        @ApiResponse(responseCode = "404", description = "Garage non trouvé")
    })
    public ResponseEntity<GarageResponse> getGarageById(@PathVariable UUID id) {
        GarageResponse response = garageService.getGarageById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Lister tous les garages avec pagination et tri", 
               description = "Retourne une liste paginée de tous les garages")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des garages récupérée avec succès")
    })
    public ResponseEntity<GarageListResponse> getAllGarages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        GarageListResponse response = garageService.getAllGarages(pageable);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un garage", description = "Met à jour les informations d'un garage existant")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Garage mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Garage non trouvé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<GarageResponse> updateGarage(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGarageRequest request) {
        GarageResponse response = garageService.updateGarage(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un garage", description = "Supprime définitivement un garage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Garage supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Garage non trouvé")
    })
    public ResponseEntity<Void> deleteGarage(@PathVariable UUID id) {
        garageService.deleteGarage(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search/by-ville")
    @Operation(summary = "Rechercher des garages par ville", 
               description = "Retourne tous les garages d'une ville donnée")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des garages trouvés")
    })
    public ResponseEntity<List<GarageResponse>> searchByVille(
            @RequestParam String ville) {
        List<GarageResponse> garages = garageService.findGaragesByVille(ville);
        return ResponseEntity.ok(garages);
    }
    
    @GetMapping("/search/by-name")
    @Operation(summary = "Rechercher des garages par nom", 
               description = "Recherche des garages contenant le nom spécifié")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des garages trouvés")
    })
    public ResponseEntity<List<GarageResponse>> searchByName(
            @RequestParam String name) {
        List<GarageResponse> garages = garageService.findGaragesByName(name);
        return ResponseEntity.ok(garages);
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des garages par carburant et accessoire", 
               description = "Filtre les garages ayant des véhicules du type de carburant donné et disposant d'un accessoire correspondant au nom fourni")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des garages trouvés")
    })
    public ResponseEntity<List<GarageResponse>> searchByFuelAndAccessory(
            @RequestParam TypeCarburant typeCarburant,
            @RequestParam String accessoireNom) {
        List<GarageResponse> garages = garageService.searchByFuelAndAccessoryName(typeCarburant, accessoireNom);
        return ResponseEntity.ok(garages);
    }
}
