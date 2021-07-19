package com.mjamsek.auth.services;

import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.rest.exceptions.UnauthorizedException;

public interface CredentialsService {
    
    UserEntity checkPasswordCredentials(String username, String password) throws UnauthorizedException;
    
    void assignPasswordCredential(String userId, String newPassword);
    
}
