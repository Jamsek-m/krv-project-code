package com.mjamsek.auth.lib.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TokenType {
    ID("ID"),
    ACCESS("Bearer"),
    REFRESH("Refresh");
    
    private final String type;
    
    TokenType(String type) {
        this.type = type;
    }
    
    @JsonValue
    public String type() {
        return this.type;
    }
    
}
