package com.renault.garage.infrastructure.rest;

import com.renault.garage.domain.exception.CapaciteGarageDepasseeException;
import com.renault.garage.domain.exception.GarageNotFoundException;
import com.renault.garage.domain.exception.VehiculeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(CapaciteGarageDepasseeException.class)
    public ResponseEntity<Map<String, Object>> handleCapacityExceeded(CapaciteGarageDepasseeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", 400,
                        "error", "Capacité dépassée",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(GarageNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleGarageNotFound(GarageNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", 404,
                        "error", "Garage introuvable",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(VehiculeNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleVehiculeNotFound(VehiculeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", 404,
                        "error", "Véhicule introuvable",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", 400,
                        "error", "Requête invalide",
                        "message", ex.getMessage()
                ));
    }
}