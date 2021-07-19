package com.mjamsek.auth.lib.enums;

public enum TokenGrantType {
    PASSWORD("password"),
    CLIENT_CREDENTIALS("client_credentials"),
    AUTHORIZATION_CODE("authorization_code");
    
    private final String type;
    
    TokenGrantType(String type) {
        this.type = type;
    }
    
    public String type() {
        return this.type;
    }
    
    public static TokenGrantType fromString(String type) {
        if (type != null) {
            for (TokenGrantType grantType : TokenGrantType.values()) {
                if (grantType.type.equals(type)) {
                    return grantType;
                }
            }
        }
        throw new IllegalArgumentException("No mapping for value '" + type + "'!");
    }
}
