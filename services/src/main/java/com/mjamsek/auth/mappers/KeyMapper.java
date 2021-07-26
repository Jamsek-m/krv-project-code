package com.mjamsek.auth.mappers;

import com.mjamsek.auth.lib.responses.PublicSigningKey;
import com.mjamsek.auth.persistence.keys.SigningKeyEntity;

public class KeyMapper {
    
    public static PublicSigningKey fromEntity(SigningKeyEntity entity) {
        PublicSigningKey key = BaseMapper.mapBase(entity, new PublicSigningKey());
        // key.setPublicKey(entity.getPublicKey());
        key.setAlgorithm(entity.getAlgorithm());
        return key;
    }
    
}
