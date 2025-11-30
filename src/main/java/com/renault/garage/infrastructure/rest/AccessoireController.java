package com.renault.garage.infrastructure.rest;

import com.renault.garage.application.dto.*;
import com.renault.garage.application.service.AccessoireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller - Accessoires d'un véhicule
 */
@RestController
@RequestMapping("/api/v1/garages/{garageId}/vehicules/{vehiculeId}/accessoires")
@Tag(name = "Accessoires", description = "API de gestion des accessoires d'un véhicule")
public class AccessoireController {

    private final AccessoireService accessoireService;

    public AccessoireController(AccessoireService accessoireService) {
        this.accessoireService = accessoireService;
    }

    @PostMapping
    @Operation(summary = "Ajouter un accessoire", description = "Ajoute un accessoire à un véhicule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Accessoire créé avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule non trouvé")
    })
    public ResponseEntity<AccessoireResponse> create(
            @PathVariable UUID garageId,
            @PathVariable UUID vehiculeId,
            @Valid @RequestBody CreateAccessoireRequest request) {
        AccessoireResponse response = accessoireService.create(garageId, vehiculeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lister les accessoires", description = "Retourne la liste des accessoires d'un véhicule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule non trouvé")
    })
    public ResponseEntity<List<AccessoireResponse>> list(
            @PathVariable UUID vehiculeId) {
        List<AccessoireResponse> list = accessoireService.list(vehiculeId);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{accessoireId}")
    @Operation(summary = "Mettre à jour un accessoire", description = "Met à jour les informations d'un accessoire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Accessoire mis à jour"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou accessoire non trouvé")
    })
    public ResponseEntity<AccessoireResponse> update(
            @PathVariable UUID garageId,
            @PathVariable UUID vehiculeId,
            @PathVariable UUID accessoireId,
            @Valid @RequestBody UpdateAccessoireRequest request) {
        AccessoireResponse response = accessoireService.update(garageId, vehiculeId, accessoireId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{accessoireId}")
    @Operation(summary = "Supprimer un accessoire", description = "Supprime un accessoire d'un véhicule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Accessoire supprimé"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou accessoire non trouvé")
    })
    public ResponseEntity<Void> delete(
            @PathVariable UUID vehiculeId,
            @PathVariable UUID accessoireId) {
        accessoireService.delete(vehiculeId, accessoireId);
        return ResponseEntity.noContent().build();
    }
}
