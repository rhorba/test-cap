package com.renault.garage.application.service;

import com.renault.garage.domain.model.Garage;
import com.renault.garage.domain.repository.GarageRepository;
import com.renault.garage.domain.exception.*;
import com.renault.garage.application.dto.*;
import com.renault.garage.application.mapper.GarageMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service - Gestion des garages
 * Contient la logique métier de l'application
 */
@Service
@Transactional
public class GarageService {
    
    private final GarageRepository garageRepository;
    private final GarageMapper garageMapper;
    
    public GarageService(GarageRepository garageRepository, GarageMapper garageMapper) {
        this.garageRepository = garageRepository;
        this.garageMapper = garageMapper;
    }
    
    /**
     * Crée un nouveau garage
     */
    public GarageResponse createGarage(CreateGarageRequest request) {
        Garage garage = garageMapper.toDomain(request);
        Garage savedGarage = garageRepository.save(garage);
        return garageMapper.toResponse(savedGarage);
    }
    
    /**
     * Récupère un garage par son ID
     */
    @Transactional(readOnly = true)
    public GarageResponse getGarageById(UUID id) {
        Garage garage = garageRepository.findById(id)
            .orElseThrow(() -> new GarageNotFoundException(
                "Garage non trouvé avec l'ID: " + id
            ));
        return garageMapper.toResponse(garage);
    }
    
    /**
     * Récupère tous les garages avec pagination
     */
    @Transactional(readOnly = true)
    public GarageListResponse getAllGarages(Pageable pageable) {
        Page<Garage> garagePage = garageRepository.findAll(pageable);
        
        List<GarageResponse> garages = garagePage.getContent()
            .stream()
            .map(garageMapper::toResponse)
            .collect(Collectors.toList());
        
        return new GarageListResponse(
            garages,
            garagePage.getNumber(),
            garagePage.getTotalPages(),
            garagePage.getTotalElements()
        );
    }
    
    /**
     * Met à jour un garage
     */
    public GarageResponse updateGarage(UUID id, UpdateGarageRequest request) {
        Garage garage = garageRepository.findById(id)
            .orElseThrow(() -> new GarageNotFoundException(
                "Garage non trouvé avec l'ID: " + id
            ));
        
        garage.update(
            request.name(),
            garageMapper.toAddressDomain(request.address()),
            request.telephone(),
            request.email(),
            garageMapper.toHorairesDomain(request.horairesOuverture())
        );
        
        Garage updatedGarage = garageRepository.save(garage);
        return garageMapper.toResponse(updatedGarage);
    }
    
    /**
     * Supprime un garage
     */
    public void deleteGarage(UUID id) {
        if (!garageRepository.existsById(id)) {
            throw new GarageNotFoundException(
                "Garage non trouvé avec l'ID: " + id
            );
        }
        garageRepository.deleteById(id);
    }
    
    /**
     * Recherche des garages par ville
     */
    @Transactional(readOnly = true)
    public List<GarageResponse> findGaragesByVille(String ville) {
        return garageRepository.findByVille(ville)
            .stream()
            .map(garageMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Recherche des garages par nom
     */
    @Transactional(readOnly = true)
    public List<GarageResponse> findGaragesByName(String name) {
        return garageRepository.findByNameContaining(name)
            .stream()
            .map(garageMapper::toResponse)
            .collect(Collectors.toList());
    }
}
