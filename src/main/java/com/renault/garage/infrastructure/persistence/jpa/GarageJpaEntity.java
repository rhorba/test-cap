package com.renault.garage.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity - Garage
 * Entité de persistance pour la table garages
 */
@Entity
@Table(name = "garages")
public class GarageJpaEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "rue", nullable = false, length = 255)
    private String rue;
    
    @Column(name = "ville", nullable = false, length = 100)
    private String ville;
    
    @Column(name = "code_postal", nullable = false, length = 10)
    private String codePostal;
    
    @Column(name = "pays", nullable = false, length = 100)
    private String pays;
    
    @Column(name = "telephone", nullable = false, length = 20)
    private String telephone;
    
    @Column(name = "email", nullable = false, length = 255)
    private String email;
    
    // Changed from jsonb to TEXT for H2 compatibility
    @Column(name = "horaires_ouverture", columnDefinition = "TEXT", nullable = false)
    private String horairesOuverture;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public GarageJpaEntity() {}
    
    // Getters et Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRue() { return rue; }
    public void setRue(String rue) { this.rue = rue; }
    
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    
    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }
    
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
    
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getHorairesOuverture() { return horairesOuverture; }
    public void setHorairesOuverture(String horairesOuverture) { this.horairesOuverture = horairesOuverture; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
