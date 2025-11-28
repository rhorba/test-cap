package com.renault.garage.infrastructure.persistence.jpa;

import com.renault.garage.domain.model.TypeCarburant;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity - Vehicule
 * Entité de persistance pour la table vehicules
 */
@Entity
@Table(name = "vehicules")
public class VehiculeJpaEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;
    
    @Column(name = "garage_id", columnDefinition = "UUID", nullable = false)
    private UUID garageId;
    
    @Column(name = "modele_id", columnDefinition = "UUID", nullable = false)
    private UUID modeleId;
    
    @Column(name = "brand", nullable = false, length = 100)
    private String brand;
    
    @Column(name = "annee_fabrication", nullable = false)
    private Integer anneeFabrication;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_carburant", nullable = false, length = 50)
    private TypeCarburant typeCarburant;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public VehiculeJpaEntity() {}
    
    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getGarageId() { return garageId; }
    public void setGarageId(UUID garageId) { this.garageId = garageId; }
    
    public UUID getModeleId() { return modeleId; }
    public void setModeleId(UUID modeleId) { this.modeleId = modeleId; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public Integer getAnneeFabrication() { return anneeFabrication; }
    public void setAnneeFabrication(Integer anneeFabrication) { this.anneeFabrication = anneeFabrication; }
    
    public TypeCarburant getTypeCarburant() { return typeCarburant; }
    public void setTypeCarburant(TypeCarburant typeCarburant) { this.typeCarburant = typeCarburant; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
