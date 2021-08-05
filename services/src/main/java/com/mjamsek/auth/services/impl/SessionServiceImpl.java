package com.mjamsek.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.auth.persistence.sessions.SessionEntity;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.auth.services.SessionService;
import com.mjamsek.auth.services.UserService;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.UnauthorizedException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Optional;

@RequestScoped
public class SessionServiceImpl implements SessionService {
    
    private static final Logger LOG = LogManager.getLogger(SessionServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private UserService userService;
    
    @Override
    public SessionEntity startSession(String ipAddress) {
        SessionEntity session = new SessionEntity();
        session.setIpAddress(ipAddress);
        
        try {
            em.getTransaction().begin();
            em.persist(session);
            em.getTransaction().commit();
            return session;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public SessionEntity associateUserWithSession(String sessionId, String userId) {
        SessionEntity entity = getSessionById(sessionId)
            .orElseThrow(() -> new UnauthorizedException("Invalid session!"));
        
        UserEntity user = userService.getUserEntityById(userId)
            .orElseThrow(() -> new UnauthorizedException("Invalid user!"));
        
        try {
            em.getTransaction().begin();
            entity.setUser(user);
            em.flush();
            em.getTransaction().commit();
            return entity;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Optional<SessionEntity> getSession(String sessionId, String ipAddress) {
        TypedQuery<SessionEntity> query = em.createNamedQuery(SessionEntity.GET_SESSION, SessionEntity.class);
        query.setParameter("sessionId", sessionId);
        query.setParameter("ip", ipAddress);
        
        try {
            SessionEntity entity = query.getSingleResult();
            return Optional.of(entity);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Optional<SessionEntity> getSessionById(String sessionId) {
        return Optional.ofNullable(em.find(SessionEntity.class, sessionId));
    }
    
    @Override
    public void endSession(String sessionId) {
        getSessionById(sessionId).ifPresent(session -> {
            try {
                em.getTransaction().begin();
                em.remove(session);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                LOG.error(e);
            }
        });
    }
}
