package com.mjamsek.auth.mappers;

import com.mjamsek.auth.lib.Role;
import com.mjamsek.auth.persistence.user.RoleEntity;

public class RoleMapper {
    
    public static Role fromEntity(RoleEntity entity) {
        Role role = BaseMapper.mapBase(entity, new Role());
        role.setName(entity.getName());
        role.setDescription(entity.getDescription());
        role.setGrantedScopes(entity.getGrantedScopes());
        return role;
    }
    
}
