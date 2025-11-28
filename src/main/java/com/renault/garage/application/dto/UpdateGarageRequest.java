package com.renault.garage.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

/**
 * DTO - Requête de mise à jour d'un garage
 */
public record UpdateGarageRequest(
    @Size(min = 3, max = 255, message = "Le nom doit contenir entre 3 et 255 caractères")
    String name,
    
    @Valid
    AddressDTO address,
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Format de téléphone invalide")
    String telephone,
    
    @Email(message = "Format d'email invalide")
    String email,
    
    Map<DayOfWeek, @Valid List<OpeningTimeDTO>> horairesOuverture
) {}
