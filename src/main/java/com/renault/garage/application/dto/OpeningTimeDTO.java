package com.renault.garage.application.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * DTO - Horaire d'ouverture
 */
public record OpeningTimeDTO(
    @NotNull(message = "L'heure de début est obligatoire")
    LocalTime startTime,
    
    @NotNull(message = "L'heure de fin est obligatoire")
    LocalTime endTime
) {}
