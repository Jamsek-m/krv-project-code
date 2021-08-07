package com.mjamsek.auth.utils;

import java.util.Random;

public class StringUtil {
    
    public static String getRandomAlphanumericString(int length) {
        final int LEFT_LIMIT = 48; // char '0'
        final int RIGHT_LIMIT = 122; // char 'z'
        
        Random random = new Random();
        
        return random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
            .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)) // remove non-alphanumeric chars
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }
    
    /**
     * Filters value based on its content. Null or empty strings are returned as null value, otherwise returns same value.
     * @param value nullable value, or empty string or non-empty string
     * @return value or null
     */
    public static String fieldValue(String value) {
        if (value == null || value.trim().isBlank()) {
            return null;
        }
        return value;
    }
    
    public static String fieldValueOrElse(String value, String orElse) {
        if (fieldSet(value)) {
            return value;
        }
        return orElse;
    }
    
    /**
     *
     * @param value value to be checked
     * @return <code>true</code> if field is not null or not an empty string
     */
    public static boolean fieldSet(String value) {
        return fieldValue(value) != null;
    }
    
}
