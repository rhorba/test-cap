package com.renault.garage.application.dto;

import com.renault.garage.domain.model.TypeCarburant;
import jakarta.validation.constraints.*;
import java.util.UUID;

/**
 * DTO - Requête de création d'un véhicule
 */
public record CreateVehiculeRequest(
    @NotNull(message = "Le modèle ID est obligatoire")
    UUID modeleId,
    
    @NotBlank(message = "La marque est obligatoire")
    @Size(max = 100, message = "La marque ne peut pas dépasser 100 caractères")
    String brand,
    
    @NotNull(message = "L'année de fabrication est obligatoire")
    @Min(value = 1900, message = "L'année de fabrication doit être >= 1900")
    @Max(value = 2026, message = "L'année de fabrication ne peut pas dépasser 2026")
    Integer anneeFabrication,
    
    @NotNull(message = "Le type de carburant est obligatoire")
    TypeCarburant typeCarburant
) {}
