package com.mjamsek.auth.mappers;

import com.mjamsek.auth.lib.responses.PublicSigningKey;
import com.mjamsek.auth.persistence.keys.SigningKeyEntity;

public class KeyMapper {
    
    public static PublicSigningKey fromEntity(SigningKeyEntity entity) {
        PublicSigningKey key = BaseMapper.mapBase(entity, new PublicSigningKey());
        key.setAlgorithm(entity.getAlgorithm());
        key.setKeyType(entity.getKeyType());
        key.setPriority(entity.getPriority());
        return key;
    }
    
}
