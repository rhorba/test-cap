package com.renault.garage.application.dto;

import com.renault.garage.domain.model.TypeAccessoire;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO - Requête de mise à jour d'un accessoire
 */
public record UpdateAccessoireRequest(
    @Size(max = 255, message = "Le nom ne peut pas dépasser 255 caractères")
    String nom,
    
    String description,
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Le prix ne peut pas être négatif")
    @DecimalMax(value = "999999.99", message = "Le prix ne peut pas dépasser 999999.99")
    BigDecimal prix,
    
    TypeAccessoire type
) {}
