package com.renault.garage.infrastructure.rest;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response - Erreur de validation
 */
public record ValidationErrorResponse(
    String code,
    String message,
    Map<String, String> errors,
    LocalDateTime timestamp
) {}
