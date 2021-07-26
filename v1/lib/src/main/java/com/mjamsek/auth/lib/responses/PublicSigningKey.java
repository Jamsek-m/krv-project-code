package com.mjamsek.auth.lib.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mjamsek.auth.lib.BaseType;
import io.jsonwebtoken.SignatureAlgorithm;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicSigningKey extends BaseType {
    
    private String publicKey;
    
    private SignatureAlgorithm algorithm;
    
    public String getPublicKey() {
        return publicKey;
    }
    
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    
    public SignatureAlgorithm getAlgorithm() {
        return algorithm;
    }
    
    public void setAlgorithm(SignatureAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
}
