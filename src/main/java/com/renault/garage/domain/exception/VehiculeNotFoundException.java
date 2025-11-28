package com.renault.garage.domain.exception;

/**
 * Exception levée lorsqu'un véhicule n'est pas trouvé
 */
public class VehiculeNotFoundException extends RuntimeException {
    public VehiculeNotFoundException(String message) {
        super(message);
    }
    
    public VehiculeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
