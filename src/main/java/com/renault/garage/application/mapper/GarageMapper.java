package com.renault.garage.application.mapper;

import com.renault.garage.domain.model.*;
import com.renault.garage.application.dto.*;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper - Conversion entre le domaine Garage et les DTOs
 */
@Component
public class GarageMapper {
    
    /**
     * Convertit un CreateGarageRequest en entité Garage
     */
    public Garage toDomain(CreateGarageRequest request) {
        Address address = new Address(
            request.address().rue(),
            request.address().ville(),
            request.address().codePostal(),
            request.address().pays()
        );
        
        Map<DayOfWeek, List<OpeningTime>> horaires = request.horairesOuverture()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().stream()
                    .map(dto -> new OpeningTime(dto.startTime(), dto.endTime()))
                    .collect(Collectors.toList())
            ));
        
        return new Garage(
            request.name(),
            address,
            request.telephone(),
            request.email(),
            horaires
        );
    }
    
    /**
     * Convertit une entité Garage en GarageResponse
     */
    public GarageResponse toResponse(Garage garage) {
        AddressDTO addressDTO = new AddressDTO(
            garage.getAddress().rue(),
            garage.getAddress().ville(),
            garage.getAddress().codePostal(),
            garage.getAddress().pays()
        );
        
        Map<DayOfWeek, List<OpeningTimeDTO>> horairesDTO = garage.getHorairesOuverture()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().stream()
                    .map(ot -> new OpeningTimeDTO(ot.startTime(), ot.endTime()))
                    .collect(Collectors.toList())
            ));
        
        return new GarageResponse(
            garage.getId(),
            garage.getName(),
            addressDTO,
            garage.getTelephone(),
            garage.getEmail(),
            horairesDTO,
            garage.getVehicules().size(),
            garage.getCapaciteRestante(),
            garage.getCreatedAt(),
            garage.getUpdatedAt()
        );
    }
    
    /**
     * Convertit un AddressDTO en Address
     */
    public Address toAddressDomain(AddressDTO dto) {
        if (dto == null) return null;
        return new Address(
            dto.rue(),
            dto.ville(),
            dto.codePostal(),
            dto.pays()
        );
    }
    
    /**
     * Convertit un Map d'horaires DTO en Map d'horaires domaine
     */
    public Map<DayOfWeek, List<OpeningTime>> toHorairesDomain(
            Map<DayOfWeek, List<OpeningTimeDTO>> horairesDTO) {
        if (horairesDTO == null) return null;
        
        return horairesDTO.entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().stream()
                    .map(dto -> new OpeningTime(dto.startTime(), dto.endTime()))
                    .collect(Collectors.toList())
            ));
    }
}
