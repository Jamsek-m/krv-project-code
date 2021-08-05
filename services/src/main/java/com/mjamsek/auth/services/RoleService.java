package com.mjamsek.auth.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.auth.lib.Role;
import com.mjamsek.auth.persistence.user.RoleEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleService {
    
    List<Role> getUserRoles(String userId);
    
    Set<String> getUserScopes(String userId);
    
    List<RoleEntity> getUserRoleEntities(String userId);
    
    List<Role> getRoles(QueryParameters queryParameters);
    
    Role getRole(String roleId);
    
    void assignRoleToUser(String userId, String roleId);
    
    void removeRoleFromUser(String userId, String roleId);
    
    Role createRole(Role role);
    
    Role patchRole(String roleId, Role role);
    
    void deleteRole(String roleId);
    
    Optional<RoleEntity> getEntityById(String roleId);
    
    Optional<RoleEntity> getEntityByName(String roleName);
    
}
