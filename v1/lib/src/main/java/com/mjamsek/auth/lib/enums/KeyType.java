package com.mjamsek.auth.lib.enums;

import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Set;

public enum KeyType {
    HMAC("HMAC"),
    RSA("RSA"),
    ELLIPTIC_CURVE("EC");
    
    private final String type;
    
    KeyType(String type) {
        this.type = type;
    }
    
    public String type() {
        return this.type;
    }
    
    public static KeyType getKeyType(SignatureAlgorithm algorithm) {
        if (Set.of(SignatureAlgorithm.HS256, SignatureAlgorithm.HS384, SignatureAlgorithm.HS512).contains(algorithm)) {
            return KeyType.HMAC;
        } else if (Set.of(SignatureAlgorithm.RS256, SignatureAlgorithm.RS384, SignatureAlgorithm.RS512).contains(algorithm)) {
            return KeyType.RSA;
        } else if (Set.of(SignatureAlgorithm.ES256, SignatureAlgorithm.ES384, SignatureAlgorithm.ES512).contains(algorithm)) {
            return KeyType.ELLIPTIC_CURVE;
        }
        throw new IllegalArgumentException("Unrecognized algorithm group!");
    }
}
