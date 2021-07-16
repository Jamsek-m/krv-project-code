package com.mjamsek.auth.lib;

import java.util.List;

public class User extends BaseType {
    
    private String username;
    
    private String email;
    
    private String firstName;
    
    private String lastName;
    
    private List<UserAttribute> attributes;
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public List<UserAttribute> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(List<UserAttribute> attributes) {
        this.attributes = attributes;
    }
}
