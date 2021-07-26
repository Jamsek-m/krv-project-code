package com.mjamsek.auth.services;

import com.mjamsek.auth.lib.requests.token.AuthorizationCodeRequest;
import com.mjamsek.auth.lib.requests.token.ClientCredentialsRequest;
import com.mjamsek.auth.lib.requests.token.PasswordRequest;
import com.mjamsek.auth.lib.requests.token.RefreshTokenRequest;
import com.mjamsek.auth.lib.responses.TokenResponse;
import com.mjamsek.rest.exceptions.RestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.Optional;

public interface TokenService {
    
    TokenResponse clientCredentialsGrant(ClientCredentialsRequest req);
    
    TokenResponse authorizationGrant(AuthorizationCodeRequest req);
    
    TokenResponse passwordGrant(PasswordRequest req);
    
    TokenResponse refreshTokenGrant(RefreshTokenRequest req);
    
    Optional<Jws<Claims>> validateToken(String token);
    
    void validTokenOrThrow(String token) throws RestException;
}
