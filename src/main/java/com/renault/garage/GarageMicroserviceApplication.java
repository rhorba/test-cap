package com.renault.garage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Application principale Spring Boot
 * Point d'entrée du microservice de gestion des garages Renault
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.renault.garage.infrastructure.persistence.jpa")
public class GarageMicroserviceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GarageMicroserviceApplication.class, args);
    }
}
