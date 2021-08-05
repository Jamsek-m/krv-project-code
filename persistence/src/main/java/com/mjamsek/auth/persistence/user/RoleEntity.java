package com.mjamsek.auth.persistence.user;

import com.mjamsek.auth.persistence.BaseEntity;
import com.mjamsek.auth.persistence.converters.ListConverter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "roles", indexes = {
    @Index(name = "IDX_ROLES_NAME", columnList = "name", unique = true)
})
@NamedQueries({
    @NamedQuery(name = RoleEntity.GET_ROLE_BY_NAME, query = "SELECT r FROM RoleEntity r WHERE r.name = :roleName")
})
public class RoleEntity extends BaseEntity {
    
    public static final String GET_ROLE_BY_NAME = "RoleEntity.getRoleByName";
    
    @Column(name = "name", unique = true)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Convert(converter = ListConverter.class)
    @Column(name = "granted_scopes")
    private List<String> grantedScopes;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getGrantedScopes() {
        return grantedScopes;
    }
    
    public void setGrantedScopes(List<String> grantedScopes) {
        this.grantedScopes = grantedScopes;
    }
}
