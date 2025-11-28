package com.renault.garage.application.dto;

import com.renault.garage.domain.model.TypeAccessoire;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO - Réponse pour un accessoire
 */
public record AccessoireResponse(
    UUID id,
    UUID vehiculeId,
    String nom,
    String description,
    BigDecimal prix,
    TypeAccessoire type,
    LocalDateTime createdAt
) {}
