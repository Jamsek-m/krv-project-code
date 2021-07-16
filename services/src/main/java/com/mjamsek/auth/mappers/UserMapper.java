package com.mjamsek.auth.mappers;

import com.mjamsek.auth.lib.User;
import com.mjamsek.auth.lib.UserAttribute;
import com.mjamsek.auth.persistence.user.UserAttributeEntity;
import com.mjamsek.auth.persistence.user.UserEntity;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class UserMapper {
    
    public static User fromEntity(UserEntity entity) {
        User user = BaseMapper.mapBase(entity, new User());
        user.setUsername(entity.getUsername());
        user.setFirstName(entity.getFirstName());
        user.setLastName(entity.getLastName());
        user.setEmail(entity.getEmail());
        
        if (entity.getAttributes() != null) {
            user.setAttributes(entity.getAttributes().stream().map(UserMapper::fromEntity).collect(Collectors.toList()));
        } else {
            user.setAttributes(new ArrayList<>());
        }
        
        return user;
    }
    
    public static UserAttribute fromEntity(UserAttributeEntity entity) {
        UserAttribute attr = BaseMapper.mapBase(entity, new UserAttribute());
        attr.setKey(entity.getKey());
        attr.setValue(entity.getValue());
        attr.setType(entity.getType());
        
        if (entity.getUser() != null) {
            attr.setUserId(entity.getUser().getId());
        }
        return attr;
    }
    
    public static UserAttributeEntity toEntity(UserAttribute attr) {
        UserAttributeEntity entity = new UserAttributeEntity();
        entity.setKey(attr.getKey());
        entity.setValue(attr.getValue());
        entity.setType(attr.getType());
        return entity;
    }
    
}
