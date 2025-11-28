package com.renault.garage.application.mapper;

import com.renault.garage.domain.model.Vehicule;
import com.renault.garage.application.dto.*;
import org.springframework.stereotype.Component;

/**
 * Mapper - Conversion entre le domaine Vehicule et les DTOs
 */
@Component
public class VehiculeMapper {
    
    /**
     * Convertit un CreateVehiculeRequest en entité Vehicule
     */
    public Vehicule toDomain(CreateVehiculeRequest request) {
        return new Vehicule(
            request.modeleId(),
            request.brand(),
            request.anneeFabrication(),
            request.typeCarburant()
        );
    }
    
    /**
     * Convertit une entité Vehicule en VehiculeResponse
     */
    public VehiculeResponse toResponse(Vehicule vehicule) {
        return new VehiculeResponse(
            vehicule.getId(),
            vehicule.getGarageId(),
            vehicule.getModeleId(),
            vehicule.getBrand(),
            vehicule.getAnneeFabrication(),
            vehicule.getTypeCarburant(),
            vehicule.getAccessoires().size(),
            vehicule.getCreatedAt(),
            vehicule.getUpdatedAt()
        );
    }
}
