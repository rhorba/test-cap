package com.renault.garage.application.mapper;

import com.renault.garage.domain.model.Accessoire;
import com.renault.garage.application.dto.*;
import org.springframework.stereotype.Component;

/**
 * Mapper - Conversion entre le domaine Accessoire et les DTOs
 */
@Component
public class AccessoireMapper {
    
    /**
     * Convertit un CreateAccessoireRequest en entité Accessoire
     */
    public Accessoire toDomain(CreateAccessoireRequest request) {
        return new Accessoire(
            request.nom(),
            request.description(),
            request.prix(),
            request.type()
        );
    }
    
    /**
     * Convertit une entité Accessoire en AccessoireResponse
     */
    public AccessoireResponse toResponse(Accessoire accessoire) {
        return new AccessoireResponse(
            accessoire.getId(),
            accessoire.getVehiculeId(),
            accessoire.getNom(),
            accessoire.getDescription(),
            accessoire.getPrix(),
            accessoire.getType(),
            accessoire.getCreatedAt()
        );
    }
}
