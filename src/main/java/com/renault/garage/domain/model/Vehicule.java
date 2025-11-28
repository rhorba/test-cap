package com.renault.garage.domain.model;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

/**
 * Entity - Véhicule
 * Représente un véhicule dans un garage
 */
public class Vehicule {
    private UUID id;
    private UUID garageId;
    private UUID modeleId;
    private String brand;
    private int anneeFabrication;
    private TypeCarburant typeCarburant;
    private List<Accessoire> accessoires;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructeur pour créer un nouveau véhicule
     */
    public Vehicule(UUID modeleId, String brand, int anneeFabrication, 
                    TypeCarburant typeCarburant) {
        this.id = UUID.randomUUID();
        this.modeleId = Objects.requireNonNull(modeleId, "Le modèle ID ne peut pas être null");
        this.brand = Objects.requireNonNull(brand, "La marque ne peut pas être null");
        this.anneeFabrication = anneeFabrication;
        this.typeCarburant = Objects.requireNonNull(typeCarburant, "Le type de carburant ne peut pas être null");
        this.accessoires = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        validateBrand(brand);
        validateAnneeFabrication(anneeFabrication);
    }

    /**
     * Ajoute un accessoire au véhicule
     */
    public void ajouterAccessoire(Accessoire accessoire) {
        Objects.requireNonNull(accessoire, "L'accessoire ne peut pas être null");
        accessoires.add(accessoire);
        accessoire.setVehiculeId(this.id);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Supprime un accessoire du véhicule
     */
    public void supprimerAccessoire(UUID accessoireId) {
        Objects.requireNonNull(accessoireId, "L'ID de l'accessoire ne peut pas être null");
        accessoires.removeIf(a -> a.getId().equals(accessoireId));
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Met à jour les informations du véhicule
     */
    public void update(UUID modeleId, String brand, Integer anneeFabrication, 
                      TypeCarburant typeCarburant) {
        if (modeleId != null) {
            this.modeleId = modeleId;
        }
        if (brand != null) {
            validateBrand(brand);
            this.brand = brand;
        }
        if (anneeFabrication != null) {
            validateAnneeFabrication(anneeFabrication);
            this.anneeFabrication = anneeFabrication;
        }
        if (typeCarburant != null) {
            this.typeCarburant = typeCarburant;
        }
        this.updatedAt = LocalDateTime.now();
    }

    // Validations
    private void validateAnneeFabrication(int annee) {
        int currentYear = Year.now().getValue();
        if (annee < 1900 || annee > currentYear + 1) {
            throw new IllegalArgumentException(
                "Année de fabrication invalide. Doit être entre 1900 et " + (currentYear + 1)
            );
        }
    }

    private void validateBrand(String brand) {
        if (brand.isBlank()) {
            throw new IllegalArgumentException("La marque ne peut pas être vide");
        }
        if (brand.length() > 100) {
            throw new IllegalArgumentException("La marque ne peut pas dépasser 100 caractères");
        }
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getGarageId() { return garageId; }
    public UUID getModeleId() { return modeleId; }
    public String getBrand() { return brand; }
    public int getAnneeFabrication() { return anneeFabrication; }
    public TypeCarburant getTypeCarburant() { return typeCarburant; }
    public List<Accessoire> getAccessoires() { 
        return Collections.unmodifiableList(accessoires); 
    }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setGarageId(UUID garageId) { 
        this.garageId = garageId; 
        this.updatedAt = LocalDateTime.now();
    }
    
    // Setters pour la reconstruction depuis la base de données
    public void setId(UUID id) { this.id = id; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    protected void setAccessoires(List<Accessoire> accessoires) { this.accessoires = accessoires; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicule vehicule = (Vehicule) o;
        return Objects.equals(id, vehicule.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vehicule{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", anneeFabrication=" + anneeFabrication +
                ", typeCarburant=" + typeCarburant +
                ", garageId=" + garageId +
                '}';
    }
}
