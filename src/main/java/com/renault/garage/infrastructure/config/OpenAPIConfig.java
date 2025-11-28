package com.renault.garage.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration OpenAPI/Swagger
 */
@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("API Microservice Gestion des Garages Renault")
                .version("1.0.0")
                .description("""
                    API REST pour la gestion des garages Renault.
                    
                    Fonctionnalités:
                    - Gestion des garages (CRUD)
                    - Gestion des véhicules dans les garages
                    - Gestion des accessoires des véhicules
                    - Recherche avancée
                    
                    Architecture: Hexagonale (Ports & Adapters) avec DDD
                    """)
                .contact(new Contact()
                    .name("Renault Digital Team")
                    .email("digital@renault.fr")
                    .url("https://www.renault.fr"))
                .license(new License()
                    .name("Propriétaire")
                    .url("https://www.renault.fr")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Serveur de développement local"),
                new Server()
                    .url("https://api.renault.fr")
                    .description("Serveur de production")
            ));
    }
}
