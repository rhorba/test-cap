package com.renault.garage.infrastructure.rest;

import com.renault.garage.domain.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * Gère toutes les exceptions de l'application et retourne des réponses appropriées
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Gère les exceptions GarageNotFoundException
     */
    @ExceptionHandler(GarageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGarageNotFound(
            GarageNotFoundException ex, WebRequest request) {
        logger.warn("Garage non trouvé: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "GARAGE_NOT_FOUND",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Gère les exceptions VehiculeNotFoundException
     */
    @ExceptionHandler(VehiculeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVehiculeNotFound(
            VehiculeNotFoundException ex, WebRequest request) {
        logger.warn("Véhicule non trouvé: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "VEHICULE_NOT_FOUND",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Gère les exceptions AccessoireNotFoundException
     */
    @ExceptionHandler(AccessoireNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccessoireNotFound(
            AccessoireNotFoundException ex, WebRequest request) {
        logger.warn("Accessoire non trouvé: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "ACCESSOIRE_NOT_FOUND",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Gère les exceptions CapaciteGarageDepasseeException
     */
    @ExceptionHandler(CapaciteGarageDepasseeException.class)
    public ResponseEntity<ErrorResponse> handleCapaciteDepassee(
            CapaciteGarageDepasseeException ex, WebRequest request) {
        logger.warn("Capacité garage dépassée: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "CAPACITY_EXCEEDED",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Gère les exceptions de validation (annotations @Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {
        logger.warn("Erreur de validation: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ValidationErrorResponse response = new ValidationErrorResponse(
            "VALIDATION_ERROR",
            "Erreur de validation des données",
            errors,
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    /**
     * Gère les exceptions IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        logger.warn("Argument invalide: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "INVALID_ARGUMENT",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Gère toutes les autres exceptions non gérées
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        logger.error("Erreur interne du serveur", ex);
        
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "Une erreur interne est survenue. Veuillez réessayer plus tard.",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
