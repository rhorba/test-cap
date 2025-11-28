package com.renault.garage.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

/**
 * DTO - Requête de création d'un garage
 */
public record CreateGarageRequest(
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 3, max = 255, message = "Le nom doit contenir entre 3 et 255 caractères")
    String name,
    
    @NotNull(message = "L'adresse est obligatoire")
    @Valid
    AddressDTO address,
    
    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Format de téléphone invalide")
    String telephone,
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    String email,
    
    @NotNull(message = "Les horaires d'ouverture sont obligatoires")
    @NotEmpty(message = "Les horaires d'ouverture ne peuvent pas être vides")
    Map<DayOfWeek, @Valid List<OpeningTimeDTO>> horairesOuverture
) {}
