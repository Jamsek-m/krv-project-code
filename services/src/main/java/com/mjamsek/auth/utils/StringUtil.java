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
    
}
