package com.mjamsek.auth.lib;

public class BasicCredentialsWrapper {
    
    private String username;
    
    private String password;
    
    public BasicCredentialsWrapper(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public BasicCredentialsWrapper() {
    
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
