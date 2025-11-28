package com.renault.garage.infrastructure.persistence.jpa;

import com.renault.garage.infrastructure.persistence.entity.TypeAccessoireEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA Repository pour AccessoireJpaEntity
 */
@Repository
public interface SpringDataAccessoireRepository extends JpaRepository<AccessoireJpaEntity, UUID> {
    
    /**
     * Trouve les accessoires par véhicule
     */
    List<AccessoireJpaEntity> findByVehicule(VehiculeJpaEntity vehicule);
    
    /**
     * Trouve les accessoires par type
     */
    List<AccessoireJpaEntity> findByType(TypeAccessoireEntity type);
    
    /**
     * Compte le nombre d'accessoires d'un véhicule
     */
    long countByVehicule(VehiculeJpaEntity vehicule);
}
