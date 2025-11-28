package com.renault.garage.application.dto;

import com.renault.garage.domain.model.TypeCarburant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO - Réponse pour un véhicule
 */
public record VehiculeResponse(
    UUID id,
    UUID garageId,
    UUID modeleId,
    String brand,
    int anneeFabrication,
    TypeCarburant typeCarburant,
    int nombreAccessoires,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
