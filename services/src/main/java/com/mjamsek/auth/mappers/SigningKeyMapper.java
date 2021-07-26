package com.mjamsek.auth.mappers;

import com.mjamsek.auth.lib.JsonWebKey;
import com.mjamsek.auth.persistence.keys.ECSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.RsaSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.SigningKeyEntity;
import com.mjamsek.auth.services.utils.KeyUtil;

import java.security.spec.X509EncodedKeySpec;
import java.util.List;

public class SigningKeyMapper {
    
    public static JsonWebKey fromEntityToJwk(SigningKeyEntity entity) {
        JsonWebKey jwk = new JsonWebKey();
        
        jwk.setKid(entity.getId());
        jwk.setAlg(entity.getAlgorithm());
        jwk.setKty(entity.getKeyType().type());
        jwk.setUse("sig");
        
        if (entity instanceof RsaSigningKeyEntity) {
            RsaSigningKeyEntity rsaKey = (RsaSigningKeyEntity) entity;
            byte[] publicKeyBytes = KeyUtil.stringifiedKeyToBytes(rsaKey.getPublicKey());
            byte[] x5c = new X509EncodedKeySpec(publicKeyBytes).getEncoded();
            String x5cStringified = KeyUtil.keyToString(x5c);
            jwk.setX5c(List.of(x5cStringified));
        } else if (entity instanceof ECSigningKeyEntity) {
            // TODO:
            jwk.setX("");
            jwk.setY("");
            jwk.setCrv("");
        }
        
        return jwk;
    }
    
}
