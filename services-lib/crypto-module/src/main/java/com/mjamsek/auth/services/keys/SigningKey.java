package com.mjamsek.auth.services.keys;

import com.mjamsek.auth.lib.enums.KeyType;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;
import java.util.UUID;

public abstract class SigningKey implements Serializable {
    
    protected KeyType keyType;
    
    protected SignatureAlgorithm algorithm;
    
    protected String kid;
    
    protected SigningKey(SignatureAlgorithm algorithm, KeyType keyType, String kid) {
        this.algorithm = algorithm;
        this.kid = kid;
        this.keyType = keyType;
    }
    
    public PublicKey getPublicKey() {
        return null;
    }
    
    public PrivateKey getPrivateKey() {
        return null;
    }
    
    public SecretKey getSecretKey() {
        return null;
    }
    
    public UUID getKid() {
        return UUID.fromString(kid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(kid);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SigningKey that = (SigningKey) o;
        return kid.equals(that.kid);
    }
    
    public KeyType getKeyType() {
        return keyType;
    }
    
    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }
    
    public SignatureAlgorithm getAlgorithm() {
        return algorithm;
    }
    
    public void setAlgorithm(SignatureAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
    
    public void setKid(String kid) {
        this.kid = kid;
    }
}
