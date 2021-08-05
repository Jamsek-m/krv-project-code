package com.mjamsek.auth.persistence.keys;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AsymmetricSigningKeyEntity extends SigningKeyEntity {
    
    @Column(name = "public_key", columnDefinition = "TEXT", updatable = false)
    protected String publicKey;
    
    @Column(name = "private_key", columnDefinition = "TEXT", updatable = false)
    protected String privateKey;
    
    public String getPublicKey() {
        return publicKey;
    }
    
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    
    public String getPrivateKey() {
        return privateKey;
    }
    
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
