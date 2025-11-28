package com.renault.garage.domain.model;

/**
 * Enum - Type d'accessoire
 */
public enum TypeAccessoire {
    INTERIEUR("Intérieur"),
    EXTERIEUR("Extérieur"),
    ELECTRONIQUE("Électronique"),
    SECURITE("Sécurité"),
    CONFORT("Confort");
    
    private final String displayName;
    
    TypeAccessoire(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
