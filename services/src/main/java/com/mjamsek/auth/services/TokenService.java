package com.mjamsek.auth.services;

import com.mjamsek.auth.lib.requests.TokenRequest;
import com.mjamsek.auth.lib.responses.TokenResponse;

public interface TokenService {
    
    TokenResponse clientCredentialsFlow(TokenRequest.ClientCredentialsTokenRequest req);
    
    TokenResponse authorizationFlow(TokenRequest.AuthorizationCodeTokenRequest req);
    
    TokenResponse passwordFlow(TokenRequest.PasswordTokenRequest req);
}
