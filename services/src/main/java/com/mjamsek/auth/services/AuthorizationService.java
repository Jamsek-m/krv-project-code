package com.mjamsek.auth.services;

import com.mjamsek.auth.persistence.auth.AuthorizationRequestEntity;
import com.mjamsek.auth.persistence.client.ClientConsentEntity;
import com.mjamsek.auth.persistence.client.ClientEntity;

import java.util.Optional;

public interface AuthorizationService {
    
    AuthorizationRequestEntity initializeRequest(String clientId, String userIp);
    
    AuthorizationRequestEntity createAuthorizationCode(String requestId, String userId);
    
    void removeAuthorizationRequest(String requestId);
    
    Optional<AuthorizationRequestEntity> getRequestEntityById(String requestId);
    
    Optional<AuthorizationRequestEntity> getRequestEntityByIpAndUser(String userIp, String clientId);
    
    Optional<ClientConsentEntity> getClientConsent(String userId, String clientId);
    
    void addClientConsent(String userId, String clientId);
    
    boolean checkIfConsentRequired(String clientId);
    
    boolean validateRedirectUri(String redirectUri, ClientEntity client);
    
    Optional<AuthorizationRequestEntity> getRequestByCode(String authorizationCode, String clientId);

}
