package com.mjamsek.auth.lib.requests.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mjamsek.auth.lib.enums.TokenGrantType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorizationCodeRequest extends TokenRequest {
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("redirect_uri")
    private String redirectUri;
    
    @JsonProperty("client_id")
    private String clientId;
    
    @JsonProperty("code_verifier")
    private String codeVerifier;
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getRedirectUri() {
        return redirectUri;
    }
    
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public static class Builder {
        
        private final AuthorizationCodeRequest request;
        
        public static Builder newBuilder() {
            return new Builder();
        }
        
        private Builder() {
            this.request = new AuthorizationCodeRequest();
            this.request.setGrantType(TokenGrantType.AUTHORIZATION_CODE);
        }
        
        public Builder code(String code) {
            if (code == null) {
                throw new IllegalArgumentException("Required value 'code' is not present!");
            }
            this.request.setCode(code);
            return this;
        }
        
        public Builder clientId(String clientId) {
            if (clientId == null) {
                throw new IllegalArgumentException("Required value 'client_id' is not present!");
            }
            this.request.setClientId(clientId);
            return this;
        }
        
        public Builder redirectUri(String redirectUri) {
            if (redirectUri == null) {
                throw new IllegalArgumentException("Required value 'redirect_uri' is not present!");
            }
            this.request.setRedirectUri(redirectUri);
            return this;
        }
        
        public Builder codeVerifier(String codeVerifier) {
            this.request.setCodeVerifier(codeVerifier);
            return this;
        }
        
        public AuthorizationCodeRequest build() {
            return this.request;
        }
    }
    
    public String getCodeVerifier() {
        return codeVerifier;
    }
    
    public void setCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }
}
