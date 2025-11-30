package com.renault.garage.domain.model;

import com.renault.garage.domain.exception.CapaciteGarageDepasseeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

class GarageTest {
    
    @Test
    @DisplayName("Devrait créer un garage avec des informations valides")
    void shouldCreateGarageWithValidInformation() {
        // Préparation
        Address address = new Address("123 Rue de Paris", "Paris", "75001", "France");
        Map<DayOfWeek, List<OpeningTime>> horaires = createDefaultHoraires();
        
        // Action
        Garage garage = new Garage(
            "Renault Paris Centre",
            address,
            "+33123456789",
            "paris@renault.fr",
            horaires
        );
        
        // Vérification
        assertNotNull(garage.getId());
        assertEquals("Renault Paris Centre", garage.getName());
        assertEquals("Paris", garage.getAddress().ville());
        assertEquals(0, garage.getVehicules().size());
        assertEquals(50, garage.getCapaciteRestante());
        assertFalse(garage.estPlein());
    }
    
    @Test
    @DisplayName("Devrait lever une exception pour un email invalide")
    void shouldThrowExceptionForInvalidEmail() {
        // Préparation
        Address address = new Address("123 Rue", "Paris", "75001", "France");
        Map<DayOfWeek, List<OpeningTime>> horaires = createDefaultHoraires();
        
        // Action & Vérification
        assertThrows(IllegalArgumentException.class, () -> {
            new Garage(
                "Garage Test",
                address,
                "+33123456789",
                "email-invalide",
                horaires
            );
        });
    }
    
    @Test
    @DisplayName("Devrait ajouter un véhicule au garage")
    void shouldAddVehiculeToGarage() {
        // Préparation
        Garage garage = createTestGarage();
        Vehicule vehicule = createTestVehicule();
        
        // Action
        garage.ajouterVehicule(vehicule);
        
        // Vérification
        assertEquals(1, garage.getVehicules().size());
        assertEquals(49, garage.getCapaciteRestante());
        assertEquals(garage.getId(), vehicule.getGarageId());
    }
    
    @Test
    @DisplayName("Devrait lever une exception quand la capacité est dépassée")
    void shouldThrowExceptionWhenCapacityExceeded() {
        // Préparation
        Garage garage = createTestGarage();
        
        // Ajouter 50 véhicules
        for (int i = 0; i < 50; i++) {
            garage.ajouterVehicule(createTestVehicule());
        }
        
        // Action & Vérification
        assertTrue(garage.estPlein());
        assertThrows(CapaciteGarageDepasseeException.class, () -> {
            garage.ajouterVehicule(createTestVehicule());
        });
    }
    
    @Test
    @DisplayName("Devrait supprimer un véhicule du garage")
    void shouldRemoveVehiculeFromGarage() {
        // Préparation
        Garage garage = createTestGarage();
        Vehicule vehicule = createTestVehicule();
        garage.ajouterVehicule(vehicule);
        
        // Action
        garage.supprimerVehicule(vehicule.getId());
        
        // Assert
        assertEquals(0, garage.getVehicules().size());
        assertEquals(50, garage.getCapaciteRestante());
    }
    
    // Méthodes utilitaires
    private Garage createTestGarage() {
        Address address = new Address("123 Rue Test", "Paris", "75001", "France");
        return new Garage(
            "Garage Test",
            address,
            "+33123456789",
            "test@renault.fr",
            createDefaultHoraires()
        );
    }
    
    private Vehicule createTestVehicule() {
        return new Vehicule(
            UUID.randomUUID(),
            "Renault",
            2024,
            TypeCarburant.ELECTRIQUE
        );
    }
    
    private Map<DayOfWeek, List<OpeningTime>> createDefaultHoraires() {
        Map<DayOfWeek, List<OpeningTime>> horaires = new HashMap<>();
        horaires.put(DayOfWeek.MONDAY, List.of(
            new OpeningTime(LocalTime.of(8, 0), LocalTime.of(18, 0))
        ));
        return horaires;
    }
}
