package com.renault.garage.infrastructure.persistence.jpa;

import com.renault.garage.domain.model.TypeCarburant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository pour VehiculeJpaEntity
 */
@Repository
public interface SpringDataVehiculeRepository extends JpaRepository<VehiculeJpaEntity, UUID> {
    
    /**
     * Trouve les véhicules par garage ID
     */
    List<VehiculeJpaEntity> findByGarageId(UUID garageId);
    
    /**
     * Trouve les véhicules par garage ID avec pagination
     */
    Page<VehiculeJpaEntity> findByGarageId(UUID garageId, Pageable pageable);
    
    /**
     * Trouve les véhicules par modèle ID
     */
    List<VehiculeJpaEntity> findByModeleId(UUID modeleId);
    
    /**
     * Trouve les véhicules par type de carburant
     */
    List<VehiculeJpaEntity> findByTypeCarburant(TypeCarburant typeCarburant);
    
    /**
     * Trouve les véhicules par marque
     */
    List<VehiculeJpaEntity> findByBrandContainingIgnoreCase(String brand);
    
    /**
     * Compte le nombre de véhicules dans un garage
     */
    long countByGarageId(UUID garageId);
}
