package com.renault.garage.domain.model;

/**
 * Enum - Type de carburant
 */
public enum TypeCarburant {
    ESSENCE("Essence"),
    DIESEL("Diesel"),
    ELECTRIQUE("Électrique"),
    HYBRIDE("Hybride"),
    GPL("GPL");
    
    private final String displayName;
    
    TypeCarburant(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
