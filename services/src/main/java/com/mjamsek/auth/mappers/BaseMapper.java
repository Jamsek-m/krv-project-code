package com.mjamsek.auth.mappers;

import com.mjamsek.auth.lib.BaseType;
import com.mjamsek.auth.persistence.BaseEntity;

import java.util.UUID;

public class BaseMapper {
    
    public static <E extends BaseEntity, T extends BaseType> T mapBase(E entity, T type) {
        type.setId(UUID.fromString(entity.getId()));
        type.setTimestamp(entity.getTimestamp().toInstant());
        return type;
    }
    
}
