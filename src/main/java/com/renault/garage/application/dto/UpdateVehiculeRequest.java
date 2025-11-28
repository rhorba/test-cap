package com.renault.garage.application.dto;

import com.renault.garage.domain.model.TypeCarburant;
import jakarta.validation.constraints.*;
import java.util.UUID;

/**
 * DTO - Requête de mise à jour d'un véhicule
 */
public record UpdateVehiculeRequest(
    UUID modeleId,
    
    @Size(max = 100, message = "La marque ne peut pas dépasser 100 caractères")
    String brand,
    
    @Min(value = 1900, message = "L'année de fabrication doit être >= 1900")
    @Max(value = 2026, message = "L'année de fabrication ne peut pas dépasser 2026")
    Integer anneeFabrication,
    
    TypeCarburant typeCarburant
) {}
