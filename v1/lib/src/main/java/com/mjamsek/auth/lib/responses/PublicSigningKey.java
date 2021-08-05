package com.mjamsek.auth.lib.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mjamsek.auth.lib.BaseType;
import com.mjamsek.auth.lib.enums.KeyType;
import io.jsonwebtoken.SignatureAlgorithm;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicSigningKey extends BaseType {
    
    private KeyType keyType;
    
    private Integer priority;
    
    private SignatureAlgorithm algorithm;
    
    public SignatureAlgorithm getAlgorithm() {
        return algorithm;
    }
    
    public void setAlgorithm(SignatureAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
    
    public KeyType getKeyType() {
        return keyType;
    }
    
    public void setKeyType(KeyType keyType) {
        this.keyType = keyType;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
