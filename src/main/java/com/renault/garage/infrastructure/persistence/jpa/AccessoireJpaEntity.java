package com.renault.garage.infrastructure.persistence.jpa;

import com.renault.garage.infrastructure.persistence.entity.TypeAccessoireEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity - Accessoire avec relation ManyToOne vers Vehicule
 */
@Entity
@Table(name = "accessoires", indexes = {
    @Index(name = "idx_accessoire_vehicule", columnList = "vehicule_id"),
    @Index(name = "idx_accessoire_type", columnList = "type")
})
public class AccessoireJpaEntity {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", nullable = false)
    private VehiculeJpaEntity vehicule;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TypeAccessoireEntity type;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructeurs
    public AccessoireJpaEntity() {}
    
    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public VehiculeJpaEntity getVehicule() { return vehicule; }
    public void setVehicule(VehiculeJpaEntity vehicule) { this.vehicule = vehicule; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrix() { return prix; }
    public void setPrix(BigDecimal prix) { this.prix = prix; }
    public TypeAccessoireEntity getType() { return type; }
    public void setType(TypeAccessoireEntity type) { this.type = type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
