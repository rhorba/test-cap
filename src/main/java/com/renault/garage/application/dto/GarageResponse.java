package com.renault.garage.application.dto;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO - Réponse pour un garage
 */
public record GarageResponse(
    UUID id,
    String name,
    AddressDTO address,
    String telephone,
    String email,
    Map<DayOfWeek, List<OpeningTimeDTO>> horairesOuverture,
    int nombreVehicules,
    int capaciteRestante,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
