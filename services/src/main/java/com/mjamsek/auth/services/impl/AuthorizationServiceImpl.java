package com.mjamsek.auth.services.impl;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.auth.lib.enums.PKCEMethod;
import com.mjamsek.auth.persistence.auth.AuthorizationRequestEntity;
import com.mjamsek.auth.persistence.client.ClientConsentEntity;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.sessions.SessionEntity;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.auth.services.AuthorizationService;
import com.mjamsek.auth.services.ClientService;
import com.mjamsek.auth.services.SessionService;
import com.mjamsek.auth.services.UserService;
import com.mjamsek.auth.utils.StringUtil;
import com.mjamsek.auth.utils.UrlUtil;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import com.mjamsek.rest.utils.DatetimeUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Context;
import java.util.Date;
import java.util.Optional;

@RequestScoped
public class AuthorizationServiceImpl implements AuthorizationService {
    
    private static final Logger LOG = LogManager.getLogger(AuthorizationServiceImpl.class.getName());
    
    @Context
    private HttpServletRequest httpRequest;
    
    @Inject
    private EntityManager em;
    
    @Inject
    private UserService userService;
    
    @Inject
    private ClientService clientService;
    
    @Inject
    private SessionService sessionService;
    
    @Override
    public AuthorizationRequestEntity initializeRequest(String clientId, String userIp) throws BadRequestException {
        return initializeRequest(clientId, userIp, null, null);
    }
    
    @Override
    public AuthorizationRequestEntity initializeRequest(String clientId, String userIp, String pkceChallenge, PKCEMethod pkceMethod) throws BadRequestException {
        ClientEntity client = clientService.getClientByClientId(clientId)
            .orElseThrow(() -> new NotFoundException(""));
    
        try {
            em.getTransaction().begin();
        
            getRequestEntityByIpAndUser(userIp, client.getId())
                .ifPresent(request -> {
                    em.remove(request);
                    em.flush();
                });
        
            AuthorizationRequestEntity request = new AuthorizationRequestEntity();
            request.setClient(client);
            request.setUserIp(userIp);
    
            // If client has defined PKCE method, PKCE params must be present
            if (client.getPkceMethod() != null && !client.getPkceMethod().equals(PKCEMethod.NONE)) {
                if (pkceChallenge == null || pkceMethod == null) {
                    throw new BadRequestException("Missing PKCE challenge!");
                }
                if (!pkceMethod.equals(client.getPkceMethod())) {
                    throw new BadRequestException("Invalid PKCE challenge method!");
                }
                request.setPkceChallenge(pkceChallenge);
                request.setPkceMethod(pkceMethod);
            }
        
            em.persist(request);
            em.getTransaction().commit();
            return request;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public AuthorizationRequestEntity createAuthorizationCode(String requestId, String userId) {
        AuthorizationRequestEntity request = getRequestEntityById(requestId)
            .orElseThrow(() -> new NotFoundException(""));
        
        UserEntity user = userService.getUserEntityById(userId)
            .orElseThrow(() -> new NotFoundException(""));
        
        final int codeValidTime = ConfigurationUtil.getInstance().getInteger("config.auth.code.expiration").orElse(5);
        final int codeLength = ConfigurationUtil.getInstance().getInteger("config.auth.code.length").orElse(12);
        
        try {
            em.getTransaction().begin();
            request.setCode(StringUtil.getRandomAlphanumericString(codeLength));
            request.setCodeExpiration(DatetimeUtil.getMinutesAfterNow(codeValidTime));
            request.setUser(user);
            em.getTransaction().commit();
            return request;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public AuthorizationRequestEntity initializeSessionRequest(String clientId, String sessionId, String ipAddress) {
        return initializeSessionRequest(clientId, sessionId, ipAddress, null, null);
    }
    
    @Override
    public AuthorizationRequestEntity initializeSessionRequest(String clientId, String sessionId, String ipAddress, String pkceChallenge, PKCEMethod pkceMethod) {
        ClientEntity client = clientService.getClientByClientId(clientId)
            .orElseThrow(() -> new UnauthorizedException("invalid_client_id"));
    
        SessionEntity session = sessionService.getSession(sessionId, ipAddress)
            .orElseThrow(() -> new UnauthorizedException("invalid_session"));
    
        final int codeValidTime = ConfigurationUtil.getInstance().getInteger("config.auth.code.expiration").orElse(5);
        final int codeLength = ConfigurationUtil.getInstance().getInteger("config.auth.code.length").orElse(12);
    
    
        try {
            em.getTransaction().begin();
        
            getRequestEntityByIpAndUser(session.getIpAddress(), client.getId())
                .ifPresent(request -> {
                    em.remove(request);
                    em.flush();
                });
        
            AuthorizationRequestEntity request = new AuthorizationRequestEntity();
            request.setClient(client);
            request.setUserIp(session.getIpAddress());
            request.setCode(StringUtil.getRandomAlphanumericString(codeLength));
            request.setCodeExpiration(DatetimeUtil.getMinutesAfterNow(codeValidTime));
            request.setUser(session.getUser());
        
            // If client has defined PKCE method, PKCE params must be present
            if (client.getPkceMethod() != null && !client.getPkceMethod().equals(PKCEMethod.NONE)) {
                if (pkceChallenge == null || pkceMethod == null) {
                    throw new BadRequestException("Missing PKCE challenge!");
                }
                if (!pkceMethod.equals(client.getPkceMethod())) {
                    throw new BadRequestException("Invalid PKCE challenge method!");
                }
                request.setPkceChallenge(pkceChallenge);
                request.setPkceMethod(pkceMethod);
            }
        
            em.persist(request);
            em.getTransaction().commit();
            return request;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public AuthorizationRequestEntity recordPKCEChallenge(String requestId, String pkceChallenge, PKCEMethod pkceMethod) {
        AuthorizationRequestEntity request = getRequestEntityById(requestId)
            .orElseThrow(() -> new NotFoundException(""));
        try {
            em.getTransaction().begin();
            request.setPkceChallenge(pkceChallenge);
            request.setPkceMethod(pkceMethod);
            em.getTransaction().commit();
            return request;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("");
        }
    }
    
    @Override
    public void removeAuthorizationRequest(String requestId) {
        getRequestEntityById(requestId).ifPresent(request -> {
            try {
                em.getTransaction().begin();
                em.remove(request);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                LOG.error(e);
                throw new RestException("");
            }
        });
    }
    
    @Override
    public Optional<AuthorizationRequestEntity> getRequestEntityById(String requestId) {
        return Optional.ofNullable(em.find(AuthorizationRequestEntity.class, requestId));
    }
    
    @Override
    public Optional<AuthorizationRequestEntity> getRequestEntityByIpAndUser(String userIp, String clientId) {
        TypedQuery<AuthorizationRequestEntity> query = em.createNamedQuery(AuthorizationRequestEntity.GET_BY_CLIENT_IP, AuthorizationRequestEntity.class);
        query.setParameter("clientId", clientId);
        query.setParameter("userIp", userIp);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            throw new RestException("");
        }
    }
    
    @Override
    public Optional<ClientConsentEntity> getClientConsent(String userId, String clientId) {
        TypedQuery<ClientConsentEntity> query = em.createNamedQuery(ClientConsentEntity.GET_BY_USER, ClientConsentEntity.class);
        query.setParameter("userId", userId);
        query.setParameter("clientId", clientId);
        
        try {
            ClientConsentEntity entity = query.getSingleResult();
            return Optional.of(entity);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("");
        }
    }
    
    @Override
    public void addClientConsent(String userId, String clientId) {
        UserEntity user = userService.getUserEntityById(userId)
            .orElseThrow(() -> new NotFoundException(""));
        
        ClientEntity client = clientService.getClientByClientId(clientId)
            .orElseThrow(() -> new NotFoundException(""));
        
        ClientConsentEntity entity = new ClientConsentEntity();
        entity.setClient(client);
        entity.setUser(user);
        
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("");
        }
    }
    
    @Override
    public boolean checkIfConsentRequired(String clientId) {
        TypedQuery<Boolean> query = em.createNamedQuery(ClientEntity.CHECK_CONSENT_REQUIREMENT, Boolean.class);
        query.setParameter("clientId", clientId);
        try {
            return query.getSingleResult();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("");
        }
    }
    
    @Override
    public boolean validateRedirectUri(String redirectUri, ClientEntity client) {
        if (client.getRedirectUris() == null) {
            return false;
        }
        return client.getRedirectUris().stream().anyMatch(uri -> UrlUtil.checkMatchingUri(uri, redirectUri));
    }
    
    @Override
    public Optional<AuthorizationRequestEntity> getRequestByCode(String authorizationCode, String clientId) {
        TypedQuery<AuthorizationRequestEntity> query = em.createNamedQuery(AuthorizationRequestEntity.GET_BY_CODE, AuthorizationRequestEntity.class);
        query.setParameter("code", authorizationCode);
        query.setParameter("clientId", clientId);
        query.setParameter("nowDate", new Date());
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("");
        }
    }
}
