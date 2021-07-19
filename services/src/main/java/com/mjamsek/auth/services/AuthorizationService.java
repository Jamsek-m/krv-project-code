package com.mjamsek.auth.services;

import com.mjamsek.auth.persistence.auth.AuthorizationRequestEntity;
import com.mjamsek.auth.persistence.client.ClientConsentEntity;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.user.UserEntity;

import java.util.Optional;

public interface AuthorizationService {
    
    AuthorizationRequestEntity initializeRequest(String clientId, String userIp);
    
    AuthorizationRequestEntity createAuthorizationCode(String requestId, String userId);
    
    void removeAuthorizationRequest(String requestId);
    
    Optional<AuthorizationRequestEntity> getRequestEntityById(String requestId);
    
    Optional<ClientConsentEntity> getClientConsent(String userId, String clientId);
    
    void addClientConsent(String userId, String clientId);
    
    boolean validateRedirectUri(String redirectUri, ClientEntity client);
    
    Optional<UserEntity> getUserByCode(String authorizationCode);

}
