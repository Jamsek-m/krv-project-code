package com.mjamsek.auth.lib;

import com.mjamsek.auth.lib.enums.ClientStatus;
import com.mjamsek.auth.lib.enums.ClientType;
import com.mjamsek.auth.lib.enums.PKCEMethod;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.List;

public class Client extends BaseType {
    
    private String name;
    
    private String clientId;
    
    private ClientType type;
    
    private ClientStatus status;
    
    private PKCEMethod pkceMethod;
    
    private List<String> redirectUris;
    
    private List<String> webOrigins;
    
    private String secret;
    
    private Boolean requireConsent;
    
    private List<String> scopes;
    
    private SignatureAlgorithm signingKeyAlgorithm;
    
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
    
    public PKCEMethod getPkceMethod() {
        return pkceMethod;
    }
    
    public void setPkceMethod(PKCEMethod pkceMethod) {
        this.pkceMethod = pkceMethod;
    }
    
    public Boolean getRequireConsent() {
        return requireConsent;
    }
    
    public SignatureAlgorithm getSigningKeyAlgorithm() {
        return signingKeyAlgorithm;
    }
    
    public void setSigningKeyAlgorithm(SignatureAlgorithm signingKeyAlgorithm) {
        this.signingKeyAlgorithm = signingKeyAlgorithm;
    }
    
    public List<String> getWebOrigins() {
        return webOrigins;
    }
    
    public void setWebOrigins(List<String> webOrigins) {
        this.webOrigins = webOrigins;
    }
}
