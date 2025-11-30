package com.renault.garage.application.service;

import com.renault.garage.application.dto.*;
import com.renault.garage.application.mapper.AccessoireMapper;
import com.renault.garage.domain.exception.AccessoireNotFoundException;
import com.renault.garage.domain.exception.VehiculeNotFoundException;
import com.renault.garage.domain.model.Accessoire;
import com.renault.garage.domain.model.Vehicule;
import com.renault.garage.domain.repository.AccessoireRepository;
import com.renault.garage.domain.repository.VehiculeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Application Service - Gestion des accessoires d'un véhicule
 */
@Service
public class AccessoireService {

    private final AccessoireRepository accessoireRepository;
    private final VehiculeRepository vehiculeRepository;
    private final AccessoireMapper accessoireMapper;

    public AccessoireService(AccessoireRepository accessoireRepository,
                             VehiculeRepository vehiculeRepository,
                             AccessoireMapper accessoireMapper) {
        this.accessoireRepository = accessoireRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.accessoireMapper = accessoireMapper;
    }

    public AccessoireResponse create(UUID garageId, UUID vehiculeId, CreateAccessoireRequest request) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new VehiculeNotFoundException("Vehicule non trouvé: " + vehiculeId));
        if (!vehicule.getGarageId().equals(garageId)) {
            throw new VehiculeNotFoundException("Vehicule " + vehiculeId + " n'appartient pas au garage " + garageId);
        }
        Accessoire accessoire = accessoireMapper.toDomain(request);
        accessoire.setId(UUID.randomUUID());
        vehicule.ajouterAccessoire(accessoire);
        Accessoire saved = accessoireRepository.save(accessoire);
        return accessoireMapper.toResponse(saved);
    }

    public List<AccessoireResponse> list(UUID vehiculeId) {
        // Vérifie que le véhicule existe
        if (!vehiculeRepository.existsById(vehiculeId)) {
            throw new VehiculeNotFoundException("Vehicule non trouvé: " + vehiculeId);
        }
        return accessoireRepository.findByVehiculeId(vehiculeId)
                .stream().map(accessoireMapper::toResponse).toList();
    }

    public AccessoireResponse update(UUID garageId, UUID vehiculeId, UUID accessoireId, UpdateAccessoireRequest request) {
        Vehicule vehicule = vehiculeRepository.findById(vehiculeId)
                .orElseThrow(() -> new VehiculeNotFoundException("Vehicule non trouvé: " + vehiculeId));
        if (!vehicule.getGarageId().equals(garageId)) {
            throw new VehiculeNotFoundException("Vehicule " + vehiculeId + " n'appartient pas au garage " + garageId);
        }
        Accessoire accessoire = accessoireRepository.findById(accessoireId)
                .orElseThrow(() -> new AccessoireNotFoundException("Accessoire non trouvé: " + accessoireId));
        accessoire.update(request.nom(), request.description(), request.prix(), request.type());
        Accessoire saved = accessoireRepository.save(accessoire);
        return accessoireMapper.toResponse(saved);
    }

    public void delete(UUID vehiculeId, UUID accessoireId) {
        // Vérifie que le véhicule existe
        if (!vehiculeRepository.existsById(vehiculeId)) {
            throw new VehiculeNotFoundException("Vehicule non trouvé: " + vehiculeId);
        }
        // Vérifie que l'accessoire existe
        accessoireRepository.findById(accessoireId)
                .orElseThrow(() -> new AccessoireNotFoundException("Accessoire non trouvé: " + accessoireId));
        accessoireRepository.deleteById(accessoireId);
    }
}
