package com.mjamsek.auth.utils;

import java.util.List;
import java.util.stream.Collectors;

public class UrlUtil {
    
    private UrlUtil() {
    
    }
    
    public static boolean checkMatchingUri(String uriPattern, String uri) {
        String regexPattern = List.of(uriPattern.split("/")).stream()
            .map(UrlUtil::replacePart)
            .collect(Collectors.joining("/"));
        return uri.matches(regexPattern);
    }
    
    private static String replacePart(String uriPart) {
        if (uriPart.equals("*")) {
            return "(.*)";
        }
        return uriPart;
    }
    
}
