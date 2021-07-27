package com.mjamsek.auth.services.resolvers;

import com.mjamsek.auth.lib.enums.KeyType;
import com.mjamsek.auth.services.keys.SigningKey;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolver;

import java.security.Key;
import java.util.Map;
import java.util.UUID;


public class KeyResolver implements SigningKeyResolver {
    
    private final Map<UUID, SigningKey> keys;
    
    public KeyResolver(Map<UUID, SigningKey> keys) {
        this.keys = keys;
    }
    
    @Override
    public Key resolveSigningKey(JwsHeader header, Claims claims) {
        return resolveKeyOnHeader(header);
    }
    
    @Override
    public Key resolveSigningKey(JwsHeader header, String plaintext) {
        return resolveKeyOnHeader(header);
    }
    
    private Key resolveKeyOnHeader(JwsHeader<?> header) {
        String headerKid = header.getKeyId();
        if (headerKid == null) {
            throw new RuntimeException("No kid!");
        }
        UUID kid = UUID.fromString(headerKid);
        
        if (!keys.containsKey(kid)) {
            throw new UnauthorizedException("JWT signed with an unrecognized key!");
        }
        
        SigningKey signingKey = keys.get(kid);
        if (signingKey.getKeyType().equals(KeyType.RSA)) {
            return signingKey.getPublicKey();
        } else if (signingKey.getKeyType().equals(KeyType.HMAC)) {
            return signingKey.getSecretKey();
        } else if (signingKey.getKeyType().equals(KeyType.ELLIPTIC_CURVE)) {
            return signingKey.getPublicKey();
        }
        throw new UnauthorizedException("JWT signed with a malformed key!");
    }
    
}
