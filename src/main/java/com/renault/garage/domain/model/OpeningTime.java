package com.renault.garage.domain.model;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Value Object - Horaire d'ouverture
 * Représente un créneau horaire immuable
 */
public record OpeningTime(
    LocalTime startTime,
    LocalTime endTime
) {
    public OpeningTime {
        Objects.requireNonNull(startTime, "L'heure de début ne peut pas être null");
        Objects.requireNonNull(endTime, "L'heure de fin ne peut pas être null");
        
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException(
                "L'heure de fin doit être après l'heure de début"
            );
        }
        
        if (endTime.equals(startTime)) {
            throw new IllegalArgumentException(
                "L'heure de début et de fin ne peuvent pas être identiques"
            );
        }
    }
    
    /**
     * Vérifie si un horaire chevauche un autre
     */
    public boolean overlaps(OpeningTime other) {
        return !this.endTime.isBefore(other.startTime) && 
               !other.endTime.isBefore(this.startTime);
    }
}
