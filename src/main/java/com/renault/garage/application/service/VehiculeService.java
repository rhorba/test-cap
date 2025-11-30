package com.renault.garage.application.service;

import com.renault.garage.domain.model.Garage;
import com.renault.garage.domain.model.Vehicule;
import com.renault.garage.domain.repository.GarageRepository;
import com.renault.garage.domain.repository.VehiculeRepository;
import com.renault.garage.domain.exception.*;
import com.renault.garage.domain.event.DomainEventPublisher;
import com.renault.garage.domain.event.VehiculeCreatedEvent;
import com.renault.garage.application.dto.*;
import com.renault.garage.application.mapper.VehiculeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service - Gestion des véhicules
 */
@Service
@Transactional
public class VehiculeService {
    
    private static final Logger logger = LoggerFactory.getLogger(VehiculeService.class);
    
    private final VehiculeRepository vehiculeRepository;
    private final GarageRepository garageRepository;
    private final VehiculeMapper vehiculeMapper;
    private final DomainEventPublisher eventPublisher;
    
    public VehiculeService(VehiculeRepository vehiculeRepository,
                          GarageRepository garageRepository,
                          VehiculeMapper vehiculeMapper,
                          DomainEventPublisher eventPublisher) {
        this.vehiculeRepository = vehiculeRepository;
        this.garageRepository = garageRepository;
        this.vehiculeMapper = vehiculeMapper;
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Crée un nouveau véhicule dans un garage
     */
    public VehiculeResponse createVehicule(UUID garageId, CreateVehiculeRequest request) {
        logger.info("🚗 Création d'un nouveau véhicule pour le garage {}", garageId);
        
        Garage garage = garageRepository.findById(garageId)
            .orElseThrow(() -> new GarageNotFoundException(
                "Garage non trouvé avec l'ID: " + garageId
            ));

        // Vérifier la capacité actuelle de manière robuste côté persistance
        long currentCount = vehiculeRepository.countByGarageId(garageId);
        if (currentCount >= Garage.getMaxCapacity()) {
            throw new CapaciteGarageDepasseeException(
                "Le garage a atteint sa capacité maximale de " + Garage.getMaxCapacity() + " véhicules"
            );
        }
        
        Vehicule vehicule = vehiculeMapper.toDomain(request);
        
        // Utiliser la méthode du garage pour ajouter le véhicule (règle métier de capacité)
        garage.ajouterVehicule(vehicule);
        
        garageRepository.save(garage);
        Vehicule savedVehicule = vehiculeRepository.save(vehicule);
        
        // 📢 Publier l'événement de création
        VehiculeCreatedEvent event = new VehiculeCreatedEvent(
            savedVehicule.getId(),
            savedVehicule.getGarageId(),
            savedVehicule.getBrand(),
            savedVehicule.getAnneeFabrication(),
            savedVehicule.getTypeCarburant().name()
        );
        eventPublisher.publish(event);
        
        logger.info("✅ Véhicule créé avec succès: {}", savedVehicule.getId());
        
        return vehiculeMapper.toResponse(savedVehicule);
    }
    
    /**
     * Récupère un véhicule par son ID
     */
    @Transactional(readOnly = true)
    public VehiculeResponse getVehiculeById(UUID id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
            .orElseThrow(() -> new VehiculeNotFoundException(
                "Véhicule non trouvé avec l'ID: " + id
            ));
        return vehiculeMapper.toResponse(vehicule);
    }
    
    /**
     * Récupère tous les véhicules d'un garage
     */
    @Transactional(readOnly = true)
    public List<VehiculeResponse> getVehiculesByGarageId(UUID garageId) {
        if (!garageRepository.existsById(garageId)) {
            throw new GarageNotFoundException(
                "Garage non trouvé avec l'ID: " + garageId
            );
        }
        
        return vehiculeRepository.findByGarageId(garageId)
            .stream()
            .map(vehiculeMapper::toResponse)
            .toList();
    }
    
    /**
     * Met à jour un véhicule
     */
    public VehiculeResponse updateVehicule(UUID id, UpdateVehiculeRequest request) {
        Vehicule vehicule = vehiculeRepository.findById(id)
            .orElseThrow(() -> new VehiculeNotFoundException(
                "Véhicule non trouvé avec l'ID: " + id
            ));
        
        vehicule.update(
            request.modeleId(),
            request.brand(),
            request.anneeFabrication(),
            request.typeCarburant()
        );
        
        Vehicule updatedVehicule = vehiculeRepository.save(vehicule);
        return vehiculeMapper.toResponse(updatedVehicule);
    }
    
    /**
     * Supprime un véhicule
     */
    public void deleteVehicule(UUID garageId, UUID vehiculeId) {
        Garage garage = garageRepository.findById(garageId)
            .orElseThrow(() -> new GarageNotFoundException(
                "Garage non trouvé avec l'ID: " + garageId
            ));
        
        if (!vehiculeRepository.existsById(vehiculeId)) {
            throw new VehiculeNotFoundException(
                "Véhicule non trouvé avec l'ID: " + vehiculeId
            );
        }
        
        garage.supprimerVehicule(vehiculeId);
        garageRepository.save(garage);
        vehiculeRepository.deleteById(vehiculeId);
    }
}
