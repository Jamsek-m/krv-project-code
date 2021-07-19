package com.mjamsek.auth.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtil {
    
    private HttpUtil() {
    
    }
    
    public static String formatQueryParams(Map<String, String[]> params) {
        StringBuilder sb = new StringBuilder();
        if (params == null || params.size() == 0) {
            return "";
        }
        
        sb.append("?");
        boolean first = true;
        for (var p : params.entrySet()) {
            if (!first) {
                sb.append("&");
            }
            first = false;
            
            String[] valueParts = p.getValue();
            if (valueParts.length == 0) {
                sb.append(p.getKey());
                sb.append("=");
            } else if (valueParts.length == 1) {
                sb.append(p.getKey());
                sb.append("=");
                sb.append(valueParts[0]);
            } else {
                boolean firstVal = true;
                for (int i = 0; i < valueParts.length; i++) {
                    String v = valueParts[i];
                    if (!firstVal) {
                        sb.append("&");
                    }
                    firstVal = false;
                    sb.append(p.getKey());
                    sb.append("[");
                    sb.append(i);
                    sb.append("]");
                    sb.append("=");
                    sb.append(v);
                }
            }
        }
        return sb.toString();
    }
    
    public static String encodeURI(String uri) {
        return URLEncoder.encode(uri, StandardCharsets.UTF_8)
            .replaceAll("\\+", "%20")
            .replaceAll("%21", "!")
            .replaceAll("%27", "'")
            .replaceAll("%28", "(")
            .replaceAll("%29", ")")
            .replaceAll("%7E", "~");
    }
    
}
