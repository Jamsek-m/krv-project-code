package com.mjamsek.auth.lib.requests.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mjamsek.auth.lib.enums.TokenGrantType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientCredentialsRequest extends TokenRequest {
    
    @JsonProperty("client_id")
    private String clientId;
    
    @JsonProperty("client_secret")
    private String clientSecret;
    
    @JsonProperty("scope")
    private String scope;
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    
    public String getScope() {
        return scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    public static class Builder {
        
        private final ClientCredentialsRequest request;
        
        public static Builder newBuilder() {
            return new Builder();
        }
        
        private Builder() {
            this.request = new ClientCredentialsRequest();
            this.request.setGrantType(TokenGrantType.CLIENT_CREDENTIALS);
        }
        
        public Builder clientId(String clientId) {
            this.request.setClientId(clientId);
            return this;
        }
        
        public Builder clientSecret(String clientSecret) {
            this.request.setClientSecret(clientSecret);
            return this;
        }
        
        public Builder scope(String scope) {
            this.request.setScope(scope);
            return this;
        }
        
        public ClientCredentialsRequest build() {
            return this.request;
        }
        
    }
}
