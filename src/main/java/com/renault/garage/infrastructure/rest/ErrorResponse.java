package com.renault.garage.infrastructure.rest;

import java.time.LocalDateTime;

/**
 * Response - Erreur simple
 */
public record ErrorResponse(
    String code,
    String message,
    LocalDateTime timestamp
) {}
