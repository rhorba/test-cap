package com.renault.garage.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO - Adresse
 */
public record AddressDTO(
    @NotBlank(message = "La rue est obligatoire")
    String rue,
    
    @NotBlank(message = "La ville est obligatoire")
    String ville,
    
    @NotBlank(message = "Le code postal est obligatoire")
    String codePostal,
    
    @NotBlank(message = "Le pays est obligatoire")
    String pays
) {}
