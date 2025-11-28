package com.renault.garage.domain.repository;

import com.renault.garage.domain.model.Accessoire;
import com.renault.garage.domain.model.TypeAccessoire;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port - Interface du repository pour les Accessoires
 */
public interface AccessoireRepository {
    
    /**
     * Sauvegarde un accessoire (création ou mise à jour)
     */
    Accessoire save(Accessoire accessoire);
    
    /**
     * Trouve un accessoire par son ID
     */
    Optional<Accessoire> findById(UUID id);
    
    /**
     * Récupère tous les accessoires d'un véhicule
     */
    List<Accessoire> findByVehiculeId(UUID vehiculeId);
    
    /**
     * Trouve les accessoires par type
     */
    List<Accessoire> findByType(TypeAccessoire type);
    
    /**
     * Supprime un accessoire par son ID
     */
    void deleteById(UUID id);
    
    /**
     * Vérifie si un accessoire existe par son ID
     */
    boolean existsById(UUID id);
    
    /**
     * Compte le nombre d'accessoires d'un véhicule
     */
    long countByVehiculeId(UUID vehiculeId);
}
