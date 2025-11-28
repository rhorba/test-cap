package com.renault.garage.infrastructure.persistence.adapter;

import com.renault.garage.domain.model.Accessoire;
import com.renault.garage.domain.model.TypeAccessoire;
import com.renault.garage.domain.repository.AccessoireRepository;
import com.renault.garage.infrastructure.persistence.entity.TypeAccessoireEntity;
import com.renault.garage.infrastructure.persistence.jpa.AccessoireJpaEntity;
import com.renault.garage.infrastructure.persistence.jpa.SpringDataAccessoireRepository;
import com.renault.garage.infrastructure.persistence.jpa.SpringDataVehiculeRepository;
import com.renault.garage.infrastructure.persistence.jpa.VehiculeJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter - Implémentation du AccessoireRepository utilisant JPA
 */
@Component
public class AccessoireRepositoryAdapter implements AccessoireRepository {
    
    private final SpringDataAccessoireRepository jpaRepository;
    private final SpringDataVehiculeRepository vehiculeRepository;
    
    public AccessoireRepositoryAdapter(
            SpringDataAccessoireRepository jpaRepository,
            SpringDataVehiculeRepository vehiculeRepository) {
        this.jpaRepository = jpaRepository;
        this.vehiculeRepository = vehiculeRepository;
    }
    
    @Override
    public Accessoire save(Accessoire accessoire) {
        AccessoireJpaEntity entity = toEntity(accessoire);
        AccessoireJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public Optional<Accessoire> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }
    
    @Override
    public List<Accessoire> findByVehiculeId(UUID vehiculeId) {
        VehiculeJpaEntity vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicule not found: " + vehiculeId));
        return jpaRepository.findByVehicule(vehicule)
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Accessoire> findByType(TypeAccessoire type) {
        TypeAccessoireEntity entityType = toEntityType(type);
        return jpaRepository.findByType(entityType)
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public long countByVehiculeId(UUID vehiculeId) {
        VehiculeJpaEntity vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicule not found: " + vehiculeId));
        return jpaRepository.countByVehicule(vehicule);
    }
    
    // Conversion Entity -> Domain
    private Accessoire toDomain(AccessoireJpaEntity entity) {
        Accessoire accessoire = new Accessoire(
            entity.getNom(),
            entity.getDescription(),
            entity.getPrix(),
            toDomainType(entity.getType())
        );
        
        accessoire.setId(entity.getId());
        accessoire.setVehiculeId(entity.getVehicule().getId());
        accessoire.setCreatedAt(entity.getCreatedAt());
        
        return accessoire;
    }
    
    // Conversion Domain -> Entity
    private AccessoireJpaEntity toEntity(Accessoire accessoire) {
        AccessoireJpaEntity entity = new AccessoireJpaEntity();
        entity.setId(accessoire.getId());
        
        // Charger le VehiculeJpaEntity
        VehiculeJpaEntity vehicule = vehiculeRepository.findById(accessoire.getVehiculeId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicule not found: " + accessoire.getVehiculeId()));
        entity.setVehicule(vehicule);
        
        entity.setNom(accessoire.getNom());
        entity.setDescription(accessoire.getDescription());
        entity.setPrix(accessoire.getPrix());
        entity.setType(toEntityType(accessoire.getType()));
        entity.setCreatedAt(accessoire.getCreatedAt());
        return entity;
    }
    
    // Conversion TypeAccessoire Domain -> Entity
    private TypeAccessoireEntity toEntityType(TypeAccessoire type) {
        return TypeAccessoireEntity.valueOf(type.name());
    }
    
    // Conversion TypeAccessoireEntity Entity -> Domain
    private TypeAccessoire toDomainType(TypeAccessoireEntity type) {
        return TypeAccessoire.valueOf(type.name());
    }
}
