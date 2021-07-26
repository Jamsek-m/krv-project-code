package com.mjamsek.auth.services.keys;

import com.mjamsek.auth.lib.enums.KeyType;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class HmacSigningKey extends SigningKey {
    
    private final SecretKey secretKey;
    
    public HmacSigningKey(SignatureAlgorithm algorithm, String kid, String secretKey) {
        super(algorithm, KeyType.HMAC, kid);
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
    
    @Override
    public SecretKey getSecretKey() {
        return this.secretKey;
    }
}
