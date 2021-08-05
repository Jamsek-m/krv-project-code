package com.mjamsek.auth.persistence.user;

import com.mjamsek.auth.persistence.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "user_roles", indexes = {
    @Index(name = "IDX_USER_ROLES_UNIQUE", columnList = "user_id,role_id", unique = true)
})
@NamedQueries({
    @NamedQuery(name = UserRoleEntity.GET_USER_ROLES, query = "SELECT r.role FROM UserRoleEntity r WHERE r.user.id = :userId"),
    @NamedQuery(name = UserRoleEntity.DELETE_USER_ROLE, query = "DELETE FROM UserRoleEntity r WHERE r.role.id = :roleId AND r.user.id = :userId")
})
public class UserRoleEntity extends BaseEntity {
    
    public static final String GET_USER_ROLES = "UserRoleEntity.getUserRoles";
    public static final String DELETE_USER_ROLE = "UserRoleEntity.deleteUserRole";
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity role;
    
    public UserEntity getUser() {
        return user;
    }
    
    public void setUser(UserEntity user) {
        this.user = user;
    }
    
    public RoleEntity getRole() {
        return role;
    }
    
    public void setRole(RoleEntity role) {
        this.role = role;
    }
}
