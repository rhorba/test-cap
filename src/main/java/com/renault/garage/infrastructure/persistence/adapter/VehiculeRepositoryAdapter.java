package com.renault.garage.infrastructure.persistence.adapter;

import com.renault.garage.domain.model.TypeCarburant;
import com.renault.garage.domain.model.Vehicule;
import com.renault.garage.domain.repository.VehiculeRepository;
import com.renault.garage.infrastructure.persistence.jpa.SpringDataVehiculeRepository;
import com.renault.garage.infrastructure.persistence.jpa.VehiculeJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter - Implémentation du VehiculeRepository utilisant JPA
 */
@Component
public class VehiculeRepositoryAdapter implements VehiculeRepository {
    
    private final SpringDataVehiculeRepository jpaRepository;
    
    public VehiculeRepositoryAdapter(SpringDataVehiculeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Vehicule save(Vehicule vehicule) {
        VehiculeJpaEntity entity = toEntity(vehicule);
        VehiculeJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public Optional<Vehicule> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }
    
    @Override
    public List<Vehicule> findByGarageId(UUID garageId) {
        return jpaRepository.findByGarageId(garageId)
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Page<Vehicule> findByGarageId(UUID garageId, Pageable pageable) {
        return jpaRepository.findByGarageId(garageId, pageable).map(this::toDomain);
    }
    
    @Override
    public List<Vehicule> findByModeleId(UUID modeleId) {
        return jpaRepository.findByModeleId(modeleId)
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Vehicule> findByTypeCarburant(TypeCarburant typeCarburant) {
        return jpaRepository.findByTypeCarburant(typeCarburant)
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Vehicule> findByBrand(String brand) {
        return jpaRepository.findByBrandContainingIgnoreCase(brand)
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
    public long countByGarageId(UUID garageId) {
        return jpaRepository.countByGarageId(garageId);
    }
    
    // Conversion Entity -> Domain
    private Vehicule toDomain(VehiculeJpaEntity entity) {
        Vehicule vehicule = new Vehicule(
            entity.getModeleId(),
            entity.getBrand(),
            entity.getAnneeFabrication(),
            entity.getTypeCarburant()
        );
        
        vehicule.setId(entity.getId());
        vehicule.setGarageId(entity.getGarageId());
        vehicule.setCreatedAt(entity.getCreatedAt());
        vehicule.setUpdatedAt(entity.getUpdatedAt());
        
        return vehicule;
    }
    
    // Conversion Domain -> Entity
    private VehiculeJpaEntity toEntity(Vehicule vehicule) {
        VehiculeJpaEntity entity = new VehiculeJpaEntity();
        entity.setId(vehicule.getId());
        entity.setGarageId(vehicule.getGarageId());
        entity.setModeleId(vehicule.getModeleId());
        entity.setBrand(vehicule.getBrand());
        entity.setAnneeFabrication(vehicule.getAnneeFabrication());
        entity.setTypeCarburant(vehicule.getTypeCarburant());
        entity.setCreatedAt(vehicule.getCreatedAt());
        entity.setUpdatedAt(vehicule.getUpdatedAt());
        return entity;
    }
}
