package com.renault.garage.domain.repository;

import com.renault.garage.domain.model.Garage;
import com.renault.garage.domain.model.TypeCarburant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port - Interface du repository pour les Garages
 * Définit le contrat pour la persistance des garages (Hexagonal Architecture)
 */
public interface GarageRepository {
    
    /**
     * Sauvegarde un garage (création ou mise à jour)
     */
    Garage save(Garage garage);
    
    /**
     * Trouve un garage par son ID
     */
    Optional<Garage> findById(UUID id);
    
    /**
     * Récupère tous les garages avec pagination
     */
    Page<Garage> findAll(Pageable pageable);
    
    /**
     * Trouve les garages par ville
     */
    List<Garage> findByVille(String ville);
    
    /**
     * Trouve les garages qui contiennent des véhicules d'un type de carburant spécifique
     */
    List<Garage> findByTypeCarburant(TypeCarburant typeCarburant);
    
    /**
     * Supprime un garage par son ID
     */
    void deleteById(UUID id);
    
    /**
     * Vérifie si un garage existe par son ID
     */
    boolean existsById(UUID id);
    
    /**
     * Compte le nombre total de garages
     */
    long count();
    
    /**
     * Trouve les garages par nom (recherche partielle)
     */
    List<Garage> findByNameContaining(String name);
}
