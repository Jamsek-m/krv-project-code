package com.mjamsek.auth.lib.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PKCEMethod {
    NONE(""),
    S256("S256"),
    PLAIN("plain");
    
    private final String type;
    
    PKCEMethod(String type) {
        this.type = type;
    }
    
    @JsonValue
    public String getType() {
        return type;
    }
}
