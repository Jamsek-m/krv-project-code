package com.mjamsek.auth.utils;

import java.util.Map;

public class HttpUtil {
    
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
    
}
