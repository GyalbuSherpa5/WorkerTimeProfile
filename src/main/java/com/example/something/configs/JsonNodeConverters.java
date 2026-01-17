package com.example.something.configs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.postgresql.util.PGobject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.io.IOException;
import java.sql.SQLException;

public class JsonNodeConverters {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ReadingConverter
    public static class PGobjectToJsonNodeConverter implements Converter<PGobject, JsonNode> {
        @Override
        public JsonNode convert(PGobject source) {
            try {
                if (source == null || source.getValue() == null) {
                    return null;
                }
                return objectMapper.readTree(source.getValue());
            } catch (IOException e) {
                throw new RuntimeException("Failed to convert PGobject to JsonNode", e);
            }
        }
    }

    @ReadingConverter
    public static class StringToJsonNodeConverter implements Converter<String, JsonNode> {
        @Override
        public JsonNode convert(String source) {
            try {
                if (source == null || source.isEmpty()) {
                    return null;
                }
                return objectMapper.readTree(source);
            } catch (IOException e) {
                throw new RuntimeException("Failed to convert String to JsonNode", e);
            }
        }
    }

    @WritingConverter
    public static class JsonNodeToPGobjectConverter implements Converter<JsonNode, PGobject> {
        @Override
        public PGobject convert(JsonNode source) {
            try {
                if (source == null) {
                    return null;
                }
                PGobject pgObject = new PGobject();
                pgObject.setType("jsonb");
                pgObject.setValue(objectMapper.writeValueAsString(source));
                return pgObject;
            } catch (SQLException | JsonProcessingException e) {
                throw new RuntimeException("Failed to convert JsonNode to PGobject", e);
            }
        }
    }
}