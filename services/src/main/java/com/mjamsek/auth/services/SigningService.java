package com.mjamsek.auth.services;

import com.mjamsek.auth.lib.JsonWebKey;
import com.mjamsek.auth.lib.requests.CreateSignatureRequest;
import com.mjamsek.auth.lib.responses.PublicSigningKey;
import com.mjamsek.auth.persistence.keys.SigningKeyEntity;
import com.mjamsek.auth.services.keys.SigningKey;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Optional;

public interface SigningService {
    
    JsonWebKey createNewSigningKey(CreateSignatureRequest request);
    
    PublicSigningKey getSigningKey(SignatureAlgorithm algorithm);
    
    Optional<SigningKeyEntity> getEntityByAlgorithm(SignatureAlgorithm algorithm);
    
    Optional<SigningKeyEntity> getDefaultKey();
    
    List<SigningKeyEntity> getKeys();
    
    void assignKeyToClient(SignatureAlgorithm algorithm, String clientId);
    
    PrivateKey getPrivateKeyFromEntity(SigningKeyEntity entity);
    
    PublicKey getPublicKeyFromEntity(SigningKeyEntity entity);
}
