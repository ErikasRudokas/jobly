package com.jobly.model.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

@Converter
public class SkillEmbeddingConverter implements AttributeConverter<Float[], String> {

    @Override
    public String convertToDatabaseColumn(Float[] attribute) {
        if (attribute == null || attribute.length == 0) return "[]";
        return Arrays.toString(attribute);
    }

    @Override
    public Float[] convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.equals("[]")) return new Float[0];
        String cleaned = dbData.replaceAll("[\\[\\]]", "");
        String[] parts = cleaned.split(",");
        Float[] result = new Float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }
}
