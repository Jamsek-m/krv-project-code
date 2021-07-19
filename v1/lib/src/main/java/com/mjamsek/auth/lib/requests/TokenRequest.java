package com.mjamsek.auth.lib.requests;

import com.mjamsek.auth.lib.enums.TokenGrantType;

public class TokenRequest {

    private TokenGrantType grantType;
    
    public TokenGrantType getGrantType() {
        return grantType;
    }
    
    public void setGrantType(TokenGrantType grantType) {
        this.grantType = grantType;
    }
    
    public static class PasswordTokenRequest extends TokenRequest {
        
        private String username;
        
        private String password;
    
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
    
    public static class ClientCredentialsTokenRequest extends TokenRequest {
        private String clientId;
        private String clientSecret;
    
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
    }
    
    public static class AuthorizationCodeTokenRequest extends TokenRequest {
        private String code;
        
        public String getCode() {
            return code;
        }
    
        public void setCode(String code) {
            this.code = code;
        }
    }
}
