package com.mjamsek.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.auth.lib.enums.CredentialsType;
import com.mjamsek.auth.persistence.user.UserCredentialsEntity;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.auth.services.CredentialsService;
import com.mjamsek.auth.services.UserService;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import org.mindrot.jbcrypt.BCrypt;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

@RequestScoped
public class CredentialsServiceImpl implements CredentialsService {
    
    private static final Logger LOG = LogManager.getLogger(CredentialsServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private UserService userService;
    
    @Override
    public void checkPasswordCredentials(String username, String password) throws UnauthorizedException {
        UserEntity entity = userService.getUserEntityByUsername(username)
            .orElseThrow(() -> new UnauthorizedException(""));
        
        boolean validCredentials = entity.getCredentials().stream()
            .filter(cr -> cr.getType().equals(CredentialsType.PASSWORD))
            .anyMatch(cr -> BCrypt.checkpw(password, cr.getSecret()));
        
        if (!validCredentials) {
            throw new UnauthorizedException("");
        }
    }
    
    @Override
    public void assignPasswordCredential(String userId, String newPassword) {
        UserEntity user = userService.getUserEntityById(userId)
            .orElseThrow(() -> new NotFoundException(""));
        
        UserCredentialsEntity entity = new UserCredentialsEntity();
        entity.setType(CredentialsType.PASSWORD);
        entity.setUser(user);
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        entity.setSecret(hashedPassword);
        
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("");
        }
    }
}
