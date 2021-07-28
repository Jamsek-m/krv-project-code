package com.mjamsek.auth.api.context;

import com.mjamsek.auth.lib.enums.PKCEMethod;

import java.util.List;

public class AuthorizationFlowContext {
    
    public static final String CONTEXT_ID = "com.mjamsek.auth.ServletContext";
    
    private String clientId;
    
    private String clientName;
    
    private List<String> scopes;
    
    private PKCEMethod pkceMethod;
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public List<String> getScopes() {
        return scopes;
    }
    
    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
    
    public PKCEMethod getPkceMethod() {
        return pkceMethod;
    }
    
    public void setPkceMethod(PKCEMethod pkceMethod) {
        this.pkceMethod = pkceMethod;
    }
}
