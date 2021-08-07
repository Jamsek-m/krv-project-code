package com.mjamsek.auth.lib.requests;

import com.mjamsek.auth.lib.User;

public class UserProfileUpdateRequest extends User {
    
    private String password;
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
}
