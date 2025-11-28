package com.renault.garage.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity - Accessoire
 * Représente un accessoire attaché à un véhicule
 */
public class Accessoire {
    private UUID id;
    private UUID vehiculeId;
    private String nom;
    private String description;
    private BigDecimal prix;
    private TypeAccessoire type;
    private LocalDateTime createdAt;

    /**
     * Constructeur pour créer un nouvel accessoire
     */
    public Accessoire(String nom, String description, 
                     BigDecimal prix, TypeAccessoire type) {
        this.id = UUID.randomUUID();
        this.nom = Objects.requireNonNull(nom, "Le nom ne peut pas être null");
        this.description = description;
        this.prix = Objects.requireNonNull(prix, "Le prix ne peut pas être null");
        this.type = Objects.requireNonNull(type, "Le type ne peut pas être null");
        this.createdAt = LocalDateTime.now();
        
        validateNom(nom);
        validatePrix(prix);
    }

    /**
     * Met à jour les informations de l'accessoire
     */
    public void update(String nom, String description, BigDecimal prix, TypeAccessoire type) {
        if (nom != null) {
            validateNom(nom);
            this.nom = nom;
        }
        if (description != null) {
            this.description = description;
        }
        if (prix != null) {
            validatePrix(prix);
            this.prix = prix;
        }
        if (type != null) {
            this.type = type;
        }
    }

    // Validations
    private void validatePrix(BigDecimal prix) {
        if (prix.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        if (prix.compareTo(new BigDecimal("999999.99")) > 0) {
            throw new IllegalArgumentException("Le prix ne peut pas dépasser 999999.99");
        }
    }

    private void validateNom(String nom) {
        if (nom.isBlank()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        if (nom.length() > 255) {
            throw new IllegalArgumentException("Le nom ne peut pas dépasser 255 caractères");
        }
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getVehiculeId() { return vehiculeId; }
    public String getNom() { return nom; }
    public String getDescription() { return description; }
    public BigDecimal getPrix() { return prix; }
    public TypeAccessoire getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setVehiculeId(UUID vehiculeId) { 
        this.vehiculeId = vehiculeId; 
    }
    
    // Setters pour la reconstruction depuis la base de données
    public void setId(UUID id) { this.id = id; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Accessoire that = (Accessoire) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Accessoire{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", type=" + type +
                ", vehiculeId=" + vehiculeId +
                '}';
    }
}
