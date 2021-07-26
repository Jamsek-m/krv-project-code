package com.mjamsek.auth.lib;

public class ClientScope extends BaseType {
    
    private String name;
    
    private String clientId;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}

