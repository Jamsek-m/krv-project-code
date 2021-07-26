package com.mjamsek.auth.lib.requests.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mjamsek.auth.lib.enums.TokenGrantType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PasswordRequest extends TokenRequest {
    
    @JsonProperty("client_id")
    private String clientId;
    
    @JsonProperty("password")
    private String password;
    
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("scope")
    private String scope;
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getScope() {
        return scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public static class Builder {
        
        private final PasswordRequest request;
        
        public static Builder newBuilder() {
            return new PasswordRequest.Builder();
        }
        
        private Builder() {
            this.request = new PasswordRequest();
            this.request.setGrantType(TokenGrantType.PASSWORD);
        }
        
        public Builder scope(String scope) {
            if (scope != null) {
                this.request.setScope(scope);
            }
            return this;
        }
        
        public Builder password(String password) {
            if (password == null) {
                throw new IllegalArgumentException("Required value 'password' is not present!");
            }
            this.request.setPassword(password);
            return this;
        }
        
        public Builder username(String username) {
            if (username == null) {
                throw new IllegalArgumentException("Required value 'username' is not present!");
            }
            this.request.setUsername(username);
            return this;
        }
        
        public Builder clientId(String clientId) {
            if (clientId == null) {
                throw new IllegalArgumentException("Required value 'client_id' is not present!");
            }
            this.request.setClientId(clientId);
            return this;
        }
        
        public PasswordRequest build() {
            return this.request;
        }
    }
}
