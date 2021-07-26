package com.mjamsek.auth.persistence.keys;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("RSA")
public class RsaSigningKeyEntity extends SigningKeyEntity {
    
    @Column(name = "public_key", columnDefinition = "TEXT")
    private String publicKey;
    
    @Column(name = "private_key", columnDefinition = "TEXT")
    private String privateKey;
    
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
