package com.mjamsek.auth.services;

import com.mjamsek.auth.lib.requests.AuthorizationRequest;
import com.mjamsek.auth.lib.responses.TokenResponse;

public interface TokenService {
    
    TokenResponse authorizationFlow(AuthorizationRequest req);
    
}
