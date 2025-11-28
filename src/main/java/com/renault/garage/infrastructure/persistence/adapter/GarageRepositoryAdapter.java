package com.renault.garage.infrastructure.persistence.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.renault.garage.domain.model.*;
import com.renault.garage.domain.repository.GarageRepository;
import com.renault.garage.infrastructure.persistence.jpa.GarageJpaEntity;
import com.renault.garage.infrastructure.persistence.jpa.SpringDataGarageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Adapter - Implémentation du GarageRepository utilisant JPA
 * Fait le pont entre le domaine et la couche de persistance
 */
@Component
public class GarageRepositoryAdapter implements GarageRepository {
    
    private final SpringDataGarageRepository jpaRepository;
    private final ObjectMapper objectMapper;
    
    public GarageRepositoryAdapter(SpringDataGarageRepository jpaRepository, 
                                  ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Garage save(Garage garage) {
        GarageJpaEntity entity = toEntity(garage);
        GarageJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public Optional<Garage> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }
    
    @Override
    public Page<Garage> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(this::toDomain);
    }
    
    @Override
    public List<Garage> findByVille(String ville) {
        return jpaRepository.findByVille(ville)
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Garage> findByTypeCarburant(TypeCarburant typeCarburant) {
        // Implémentation simplifiée - à améliorer avec une vraie requête
        return Collections.emptyList();
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
    public long count() {
        return jpaRepository.count();
    }
    
    @Override
    public List<Garage> findByNameContaining(String name) {
        return jpaRepository.findByNameContainingIgnoreCase(name)
            .stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    // Conversion Entity -> Domain
    private Garage toDomain(GarageJpaEntity entity) {
        Address address = new Address(
            entity.getRue(),
            entity.getVille(),
            entity.getCodePostal(),
            entity.getPays()
        );
        
        Map<DayOfWeek, List<OpeningTime>> horaires = deserializeHoraires(entity.getHorairesOuverture());
        
        Garage garage = new Garage(
            entity.getName(),
            address,
            entity.getTelephone(),
            entity.getEmail(),
            horaires
        );
        
        garage.setId(entity.getId());
        garage.setCreatedAt(entity.getCreatedAt());
        garage.setUpdatedAt(entity.getUpdatedAt());
        
        return garage;
    }
    
    // Conversion Domain -> Entity
    private GarageJpaEntity toEntity(Garage garage) {
        GarageJpaEntity entity = new GarageJpaEntity();
        entity.setId(garage.getId());
        entity.setName(garage.getName());
        entity.setRue(garage.getAddress().rue());
        entity.setVille(garage.getAddress().ville());
        entity.setCodePostal(garage.getAddress().codePostal());
        entity.setPays(garage.getAddress().pays());
        entity.setTelephone(garage.getTelephone());
        entity.setEmail(garage.getEmail());
        entity.setHorairesOuverture(serializeHoraires(garage.getHorairesOuverture()));
        entity.setCreatedAt(garage.getCreatedAt());
        entity.setUpdatedAt(garage.getUpdatedAt());
        return entity;
    }
    
    // Sérialisation des horaires en JSON
    private String serializeHoraires(Map<DayOfWeek, List<OpeningTime>> horaires) {
        try {
            Map<String, List<Map<String, String>>> jsonMap = new HashMap<>();
            horaires.forEach((day, times) -> {
                List<Map<String, String>> timesList = times.stream()
                    .map(ot -> Map.of(
                        "startTime", ot.startTime().toString(),
                        "endTime", ot.endTime().toString()
                    ))
                    .collect(Collectors.toList());
                jsonMap.put(day.name(), timesList);
            });
            return objectMapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur lors de la sérialisation des horaires", e);
        }
    }
    
    // Désérialisation des horaires depuis JSON
    @SuppressWarnings("unchecked")
    private Map<DayOfWeek, List<OpeningTime>> deserializeHoraires(String json) {
        try {
            Map<String, List<Map<String, String>>> jsonMap = 
                objectMapper.readValue(json, Map.class);
            
            Map<DayOfWeek, List<OpeningTime>> horaires = new HashMap<>();
            jsonMap.forEach((dayStr, timesList) -> {
                DayOfWeek day = DayOfWeek.valueOf(dayStr);
                List<OpeningTime> times = timesList.stream()
                    .map(timeMap -> new OpeningTime(
                        LocalTime.parse(timeMap.get("startTime")),
                        LocalTime.parse(timeMap.get("endTime"))
                    ))
                    .collect(Collectors.toList());
                horaires.put(day, times);
            });
            return horaires;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur lors de la désérialisation des horaires", e);
        }
    }
}
