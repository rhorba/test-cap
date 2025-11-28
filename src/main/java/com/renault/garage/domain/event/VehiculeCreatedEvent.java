package com.renault.garage.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Événement domaine publié lors de la création d'un véhicule
 * Sérialisable pour Kafka
 */
public class VehiculeCreatedEvent implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final UUID vehiculeId;
    private final UUID garageId;
    private final String brand;
    private final int anneeFabrication;
    private final String typeCarburant;
    private final LocalDateTime occurredOn;
    
    @JsonCreator
    public VehiculeCreatedEvent(
            @JsonProperty("vehiculeId") UUID vehiculeId,
            @JsonProperty("garageId") UUID garageId,
            @JsonProperty("brand") String brand,
            @JsonProperty("anneeFabrication") int anneeFabrication,
            @JsonProperty("typeCarburant") String typeCarburant) {
        this.vehiculeId = vehiculeId;
        this.garageId = garageId;
        this.brand = brand;
        this.anneeFabrication = anneeFabrication;
        this.typeCarburant = typeCarburant;
        this.occurredOn = LocalDateTime.now();
    }
    
    public UUID getVehiculeId() {
        return vehiculeId;
    }
    
    public UUID getGarageId() {
        return garageId;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public int getAnneeFabrication() {
        return anneeFabrication;
    }
    
    public String getTypeCarburant() {
        return typeCarburant;
    }
    
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }
    
    @Override
    public String toString() {
        return "VehiculeCreatedEvent{" +
                "vehiculeId=" + vehiculeId +
                ", garageId=" + garageId +
                ", brand='" + brand + '\'' +
                ", anneeFabrication=" + anneeFabrication +
                ", typeCarburant='" + typeCarburant + '\'' +
                ", occurredOn=" + occurredOn +
                '}';
    }
}
