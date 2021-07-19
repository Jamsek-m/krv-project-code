package com.mjamsek.auth.lib.enums;

public enum TokenType {
    ID("ID"),
    ACCESS("Bearer"),
    REFRESH("Refresh");
    
    private final String type;
    
    TokenType(String type) {
        this.type = type;
    }
    
    public String type() {
        return this.type;
    }
    
}
