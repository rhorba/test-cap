package com.renault.garage.domain.exception;

/**
 * Exception levée lorsqu'un accessoire n'est pas trouvé
 */
public class AccessoireNotFoundException extends RuntimeException {
    public AccessoireNotFoundException(String message) {
        super(message);
    }
    
    public AccessoireNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
