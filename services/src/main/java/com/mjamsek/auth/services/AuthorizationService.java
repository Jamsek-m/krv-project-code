package com.mjamsek.auth.services;

import com.mjamsek.auth.lib.enums.PKCEMethod;
import com.mjamsek.auth.persistence.auth.AuthorizationRequestEntity;
import com.mjamsek.auth.persistence.client.ClientConsentEntity;
import com.mjamsek.auth.persistence.client.ClientEntity;

import javax.ws.rs.BadRequestException;
import java.util.Optional;

public interface AuthorizationService {
    
    AuthorizationRequestEntity initializeRequest(String clientId, String userIp) throws BadRequestException;
    
    AuthorizationRequestEntity initializeRequest(String clientId, String userIp, String pkceChallenge, PKCEMethod pkceMethod) throws BadRequestException;
    
    AuthorizationRequestEntity createAuthorizationCode(String requestId, String userId);
    
    AuthorizationRequestEntity recordPKCEChallenge(String requestId, String pkceChallenge, PKCEMethod pkceMethod);
    
    void removeAuthorizationRequest(String requestId);
    
    Optional<AuthorizationRequestEntity> getRequestEntityById(String requestId);
    
    Optional<AuthorizationRequestEntity> getRequestEntityByIpAndUser(String userIp, String clientId);
    
    Optional<ClientConsentEntity> getClientConsent(String userId, String clientId);
    
    void addClientConsent(String userId, String clientId);
    
    boolean checkIfConsentRequired(String clientId);
    
    boolean validateRedirectUri(String redirectUri, ClientEntity client);
    
    Optional<AuthorizationRequestEntity> getRequestByCode(String authorizationCode, String clientId);

}
