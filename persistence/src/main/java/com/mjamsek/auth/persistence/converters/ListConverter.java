package com.mjamsek.auth.persistence.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class ListConverter implements AttributeConverter<List<String>, String> {
    
    private static final String DELIMITOR = ";";
    
    @Override
    public String convertToDatabaseColumn(List<String> attributes) {
        if (attributes == null) {
            return "";
        }
        return String.join(DELIMITOR, attributes);
    }
    
    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData != null && !dbData.isEmpty()) {
            return Arrays.asList(dbData.split(DELIMITOR));
        }
        return new ArrayList<>();
    }
}
