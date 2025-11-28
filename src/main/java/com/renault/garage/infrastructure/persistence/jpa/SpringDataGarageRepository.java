package com.renault.garage.infrastructure.persistence.jpa;

import com.renault.garage.domain.model.TypeCarburant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository pour GarageJpaEntity
 */
@Repository
public interface SpringDataGarageRepository extends JpaRepository<GarageJpaEntity, UUID> {
    
    /**
     * Trouve les garages par ville
     */
    List<GarageJpaEntity> findByVille(String ville);
    
    /**
     * Trouve les garages par nom contenant une chaîne
     */
    List<GarageJpaEntity> findByNameContainingIgnoreCase(String name);
    
    /**
     * Trouve les garages qui ont des véhicules d'un certain type de carburant
     * Note: Cette requête nécessite une jointure avec la table vehicules
     */
    @Query("SELECT DISTINCT g FROM GarageJpaEntity g WHERE g.ville = :ville")
    List<GarageJpaEntity> findGaragesByVille(@Param("ville") String ville);
}
