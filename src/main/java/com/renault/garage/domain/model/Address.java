package com.renault.garage.domain.model;

import java.util.Objects;

/**
 * Value Object - Adresse
 * Représente une adresse physique immuable
 */
public record Address(
    String rue,
    String ville,
    String codePostal,
    String pays
) {
    public Address {
        Objects.requireNonNull(rue, "La rue ne peut pas être null");
        Objects.requireNonNull(ville, "La ville ne peut pas être null");
        Objects.requireNonNull(codePostal, "Le code postal ne peut pas être null");
        Objects.requireNonNull(pays, "Le pays ne peut pas être null");
        
        if (rue.isBlank()) {
            throw new IllegalArgumentException("La rue ne peut pas être vide");
        }
        if (ville.isBlank()) {
            throw new IllegalArgumentException("La ville ne peut pas être vide");
        }
        if (codePostal.isBlank()) {
            throw new IllegalArgumentException("Le code postal ne peut pas être vide");
        }
        if (pays.isBlank()) {
            throw new IllegalArgumentException("Le pays ne peut pas être vide");
        }
    }
}
