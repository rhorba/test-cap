package com.renault.garage.application.dto;

import java.util.List;

/**
 * DTO - Réponse paginée pour les garages
 */
public record GarageListResponse(
    List<GarageResponse> garages,
    int currentPage,
    int totalPages,
    long totalElements
) {}
