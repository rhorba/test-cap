package com.renault.garage.domain.repository;

import com.renault.garage.domain.model.TypeCarburant;
import com.renault.garage.domain.model.Vehicule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port - Interface du repository pour les Véhicules
 */
public interface VehiculeRepository {
    
    /**
     * Sauvegarde un véhicule (création ou mise à jour)
     */
    Vehicule save(Vehicule vehicule);
    
    /**
     * Trouve un véhicule par son ID
     */
    Optional<Vehicule> findById(UUID id);
    
    /**
     * Récupère tous les véhicules d'un garage
     */
    List<Vehicule> findByGarageId(UUID garageId);
    
    /**
     * Récupère tous les véhicules d'un garage avec pagination
     */
    Page<Vehicule> findByGarageId(UUID garageId, Pageable pageable);
    
    /**
     * Trouve les véhicules par modèle
     */
    List<Vehicule> findByModeleId(UUID modeleId);
    
    /**
     * Trouve les véhicules par type de carburant
     */
    List<Vehicule> findByTypeCarburant(TypeCarburant typeCarburant);
    
    /**
     * Trouve les véhicules par marque
     */
    List<Vehicule> findByBrand(String brand);
    
    /**
     * Supprime un véhicule par son ID
     */
    void deleteById(UUID id);
    
    /**
     * Vérifie si un véhicule existe par son ID
     */
    boolean existsById(UUID id);
    
    /**
     * Compte le nombre de véhicules dans un garage
     */
    long countByGarageId(UUID garageId);
}
