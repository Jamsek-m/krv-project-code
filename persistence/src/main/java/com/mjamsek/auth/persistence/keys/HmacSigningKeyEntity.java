package com.mjamsek.auth.persistence.keys;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("HMAC")
public class HmacSigningKeyEntity extends SigningKeyEntity {
    
    @Column(name = "secret_key", columnDefinition = "TEXT")
    private String secretKey;
    
    public String getSecretKey() {
        return secretKey;
    }
    
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
