package com.mjamsek.auth.lib.requests.token;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mjamsek.auth.lib.enums.TokenGrantType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RefreshTokenRequest extends TokenRequest {
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("scope")
    private String scope;
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getScope() {
        return scope;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }
    
    public static class Builder {
        
        private final RefreshTokenRequest request;
        
        public static Builder newBuilder() {
            return new RefreshTokenRequest.Builder();
        }
        
        private Builder() {
            this.request = new RefreshTokenRequest();
            this.request.setGrantType(TokenGrantType.REFRESH_TOKEN);
        }
        
        public Builder scope(String scope) {
            if (scope != null) {
                this.request.setScope(scope);
            }
            return this;
        }
        
        public Builder refreshToken(String refreshToken) {
            if (refreshToken == null) {
                throw new IllegalArgumentException("Required value 'refresh_token' is not present!");
            }
            this.request.setRefreshToken(refreshToken);
            return this;
        }
        
        public RefreshTokenRequest build() {
            return this.request;
        }
    }
}
