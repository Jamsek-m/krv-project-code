package com.mjamsek.auth.lib;

import java.util.List;

public class Role extends BaseType {
    
    private String name;
    
    private String description;
    
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
