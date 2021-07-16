package com.mjamsek.auth.services.impl;

import com.mjamsek.auth.lib.requests.AuthorizationRequest;
import com.mjamsek.auth.lib.responses.TokenResponse;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.services.ClientService;
import com.mjamsek.auth.services.TokenService;
import com.mjamsek.rest.exceptions.UnauthorizedException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class TokenServiceImpl implements TokenService {
    
    @Inject
    private ClientService clientService;
    
    @Override
    public TokenResponse authorizationFlow(AuthorizationRequest req) {
    
        ClientEntity client = clientService.getClientByClientId(req.getClientId())
            .orElseThrow(() -> new UnauthorizedException("Unknown client!"));
        
        
        
        
        
        return null;
    }
}
