package com.renault.garage.domain.model;

import com.renault.garage.domain.exception.CapaciteGarageDepasseeException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Entity - Garage (Aggregate Root)
 * Représente un garage Renault avec sa capacité de véhicules
 */
public class Garage {
    private UUID id;
    private String name;
    private Address address;
    private String telephone;
    private String email;
    private Map<DayOfWeek, List<OpeningTime>> horairesOuverture;
    private List<Vehicule> vehicules;
    private static final int MAX_CAPACITY = 50;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Constructeur pour créer un nouveau garage
     */
    public Garage(String name, Address address, String telephone, 
                  String email, Map<DayOfWeek, List<OpeningTime>> horairesOuverture) {
        this.id = UUID.randomUUID();
        this.name = Objects.requireNonNull(name, "Le nom ne peut pas être null");
        this.address = Objects.requireNonNull(address, "L'adresse ne peut pas être null");
        this.telephone = Objects.requireNonNull(telephone, "Le téléphone ne peut pas être null");
        this.email = Objects.requireNonNull(email, "L'email ne peut pas être null");
        this.horairesOuverture = Objects.requireNonNull(horairesOuverture);
        this.vehicules = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        validateName(name);
        validateEmail(email);
        validateTelephone(telephone);
        validateHoraires(horairesOuverture);
    }

    /**
     * Ajoute un véhicule au garage
     * @throws CapaciteGarageDepasseeException si le garage est plein
     */
    public void ajouterVehicule(Vehicule vehicule) {
        if (vehicules.size() >= MAX_CAPACITY) {
            throw new CapaciteGarageDepasseeException(
                "Le garage a atteint sa capacité maximale de " + MAX_CAPACITY + " véhicules"
            );
        }
        vehicules.add(vehicule);
        vehicule.setGarageId(this.id);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Supprime un véhicule du garage
     */
    public void supprimerVehicule(UUID vehiculeId) {
        vehicules.removeIf(v -> v.getId().equals(vehiculeId));
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Vérifie si le garage est plein
     */
    public boolean estPlein() {
        return vehicules.size() >= MAX_CAPACITY;
    }

    /**
     * Retourne la capacité restante du garage
     */
    public int getCapaciteRestante() {
        return MAX_CAPACITY - vehicules.size();
    }

    /**
     * Met à jour les informations du garage
     */
    public void update(String name, Address address, String telephone, 
                      String email, Map<DayOfWeek, List<OpeningTime>> horairesOuverture) {
        if (name != null) {
            validateName(name);
            this.name = name;
        }
        if (address != null) {
            this.address = address;
        }
        if (telephone != null) {
            validateTelephone(telephone);
            this.telephone = telephone;
        }
        if (email != null) {
            validateEmail(email);
            this.email = email;
        }
        if (horairesOuverture != null) {
            validateHoraires(horairesOuverture);
            this.horairesOuverture = horairesOuverture;
        }
        this.updatedAt = LocalDateTime.now();
    }

    // Validations
    private void validateName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        if (name.length() < 3 || name.length() > 255) {
            throw new IllegalArgumentException("Le nom doit contenir entre 3 et 255 caractères");
        }
    }

    private void validateEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Format d'email invalide");
        }
    }

    private void validateTelephone(String telephone) {
        if (!telephone.matches("^\\+?[0-9]{10,15}$")) {
            throw new IllegalArgumentException("Format de téléphone invalide");
        }
    }

    private void validateHoraires(Map<DayOfWeek, List<OpeningTime>> horaires) {
        if (horaires.isEmpty()) {
            throw new IllegalArgumentException("Les horaires d'ouverture ne peuvent pas être vides");
        }
        
        // Vérifier qu'il n'y a pas de chevauchement dans les horaires d'une même journée
        horaires.forEach((jour, creneaux) -> {
            for (int i = 0; i < creneaux.size(); i++) {
                for (int j = i + 1; j < creneaux.size(); j++) {
                    if (creneaux.get(i).overlaps(creneaux.get(j))) {
                        throw new IllegalArgumentException(
                            "Les créneaux horaires du " + jour + " se chevauchent"
                        );
                    }
                }
            }
        });
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public Address getAddress() { return address; }
    public String getTelephone() { return telephone; }
    public String getEmail() { return email; }
    public Map<DayOfWeek, List<OpeningTime>> getHorairesOuverture() { 
        return Collections.unmodifiableMap(horairesOuverture); 
    }
    public List<Vehicule> getVehicules() { 
        return Collections.unmodifiableList(vehicules); 
    }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public static int getMaxCapacity() { return MAX_CAPACITY; }

    // Setters pour la reconstruction depuis la base de données
    public void setId(UUID id) { this.id = id; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    protected void setVehicules(List<Vehicule> vehicules) { this.vehicules = vehicules; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Garage garage = (Garage) o;
        return Objects.equals(id, garage.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Garage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ville='" + address.ville() + '\'' +
                ", nombreVehicules=" + vehicules.size() +
                '}';
    }
}
