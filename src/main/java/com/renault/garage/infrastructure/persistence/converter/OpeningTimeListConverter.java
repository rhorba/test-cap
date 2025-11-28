package com.renault.garage.infrastructure.persistence.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.renault.garage.infrastructure.persistence.entity.OpeningTimeEmbeddable;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

/**
 * JPA Converter - Convertit List<OpeningTimeEmbeddable> en JSON pour PostgreSQL
 */
@Converter
public class OpeningTimeListConverter 
        implements AttributeConverter<List<OpeningTimeEmbeddable>, String> {
    
    private static final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());
    
    @Override
    public String convertToDatabaseColumn(List<OpeningTimeEmbeddable> attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur lors de la conversion en JSON", e);
        }
    }
    
    @Override
    public List<OpeningTimeEmbeddable> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(
                dbData, 
                new TypeReference<List<OpeningTimeEmbeddable>>() {}
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur lors de la conversion depuis JSON", e);
        }
    }
}
