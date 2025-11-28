package com.renault.garage.domain.exception;

/**
 * Exception levée lorsqu'un garage n'est pas trouvé
 */
public class GarageNotFoundException extends RuntimeException {
    public GarageNotFoundException(String message) {
        super(message);
    }
    
    public GarageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
