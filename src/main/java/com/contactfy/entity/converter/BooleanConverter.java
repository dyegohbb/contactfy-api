package com.contactfy.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) {
            return null;
        }
        
        String string = attribute ? "S" : "N";
	return string;
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
	return "S".equalsIgnoreCase(dbData);
    }

}
