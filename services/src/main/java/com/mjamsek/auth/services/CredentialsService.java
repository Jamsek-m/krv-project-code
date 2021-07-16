package com.mjamsek.auth.services;

import com.mjamsek.rest.exceptions.UnauthorizedException;

public interface CredentialsService {
    
    void checkPasswordCredentials(String username, String password) throws UnauthorizedException;
    
    void assignPasswordCredential(String userId, String newPassword);
    
}
