package com.mjamsek.auth.lib;

import com.mjamsek.auth.lib.enums.ClientStatus;
import com.mjamsek.auth.lib.enums.ClientType;

import java.util.List;

public class Client extends BaseType {
    
    private String name;
    
    private String clientId;
    
    private ClientType type;
    
    private ClientStatus status;
    
    private List<String> redirectUris;
    
    private String secret;
    
    private Boolean requireConsent;
    
    private List<String> scopes;
    
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
    
    public ClientType getType() {
        return type;
    }
    
    public void setType(ClientType type) {
        this.type = type;
    }
    
    public ClientStatus getStatus() {
        return status;
    }
    
    public void setStatus(ClientStatus status) {
        this.status = status;
    }
    
    public List<String> getRedirectUris() {
        return redirectUris;
    }
    
    public void setRedirectUris(List<String> redirectUris) {
        this.redirectUris = redirectUris;
    }
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public Boolean isRequireConsent() {
        return requireConsent;
    }
    
    public void setRequireConsent(Boolean requireConsent) {
        this.requireConsent = requireConsent;
    }
    
    public List<String> getScopes() {
        return scopes;
    }
    
    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
}
