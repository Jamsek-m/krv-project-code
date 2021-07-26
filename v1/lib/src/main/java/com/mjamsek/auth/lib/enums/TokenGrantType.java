package com.mjamsek.auth.lib.enums;

import java.util.Optional;

public enum TokenGrantType {
    PASSWORD("password"),
    REFRESH_TOKEN("refresh_token"),
    CLIENT_CREDENTIALS("client_credentials"),
    AUTHORIZATION_CODE("authorization_code");
    
    private final String type;
    
    TokenGrantType(String type) {
        this.type = type;
    }
    
    public String type() {
        return this.type;
    }
    
    public static Optional<TokenGrantType> fromString(String type) {
        if (type != null) {
            for (TokenGrantType grantType : TokenGrantType.values()) {
                if (grantType.type.equals(type)) {
                    return Optional.of(grantType);
                }
            }
        }
        return Optional.empty();
    }
}
