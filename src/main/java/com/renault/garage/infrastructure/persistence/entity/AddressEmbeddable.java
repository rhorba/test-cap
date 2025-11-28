package com.renault.garage.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Embeddable - Adresse
 */
@Embeddable
public class AddressEmbeddable {
    @Column(nullable = false)
    private String rue;
    
    @Column(nullable = false, length = 100)
    private String ville;
    
    @Column(nullable = false, length = 10, name = "code_postal")
    private String codePostal;
    
    @Column(nullable = false, length = 100)
    private String pays;
    
    public AddressEmbeddable() {}
    
    public AddressEmbeddable(String rue, String ville, String codePostal, String pays) {
        this.rue = rue;
        this.ville = ville;
        this.codePostal = codePostal;
        this.pays = pays;
    }
    
    // Getters et Setters
    public String getRue() { return rue; }
    public void setRue(String rue) { this.rue = rue; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getCodePostal() { return codePostal; }
    public void setCodePostal(String codePostal) { this.codePostal = codePostal; }
    public String getPays() { return pays; }
    public void setPays(String pays) { this.pays = pays; }
}
