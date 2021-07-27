package com.mjamsek.auth.lib;

import com.mjamsek.auth.persistence.keys.AsymmetricSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.HmacSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.SigningKeyEntity;

public class VerificationKeyWrapper {
    
    private String plainKey;
    
    private String keyId;
    
    public VerificationKeyWrapper() {
    
    }
    
    public VerificationKeyWrapper(SigningKeyEntity keyEntity) throws IllegalStateException {
        this.keyId = keyEntity.getId();
        if (keyEntity instanceof AsymmetricSigningKeyEntity) {
            this.plainKey = ((AsymmetricSigningKeyEntity) keyEntity).getPublicKey();
        } else if (keyEntity instanceof HmacSigningKeyEntity) {
            this.plainKey = ((HmacSigningKeyEntity) keyEntity).getSecretKey();
        } else {
            throw new IllegalStateException("Cannot return certificate from key! (kid: " + keyEntity.getId() + ", alg: " + keyEntity.getAlgorithm().getValue());
        }
    }
    
    public String getPlainKey() {
        return plainKey;
    }
    
    public void setPlainKey(String plainKey) {
        this.plainKey = plainKey;
    }
    
    public String getKeyId() {
        return keyId;
    }
    
    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}
