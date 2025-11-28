package com.renault.garage.domain.exception;

/**
 * Exception levée lorsqu'un garage atteint sa capacité maximale de véhicules
 */
public class CapaciteGarageDepasseeException extends RuntimeException {
    public CapaciteGarageDepasseeException(String message) {
        super(message);
    }
    
    public CapaciteGarageDepasseeException(String message, Throwable cause) {
        super(message, cause);
    }
}
