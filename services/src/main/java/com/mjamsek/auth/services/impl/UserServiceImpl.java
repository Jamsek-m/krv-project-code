package com.mjamsek.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.auth.lib.User;
import com.mjamsek.auth.lib.enums.CredentialsType;
import com.mjamsek.auth.lib.enums.ErrorCode;
import com.mjamsek.auth.lib.requests.RegistrationRequest;
import com.mjamsek.auth.lib.requests.UserProfileUpdateRequest;
import com.mjamsek.auth.mappers.UserMapper;
import com.mjamsek.auth.persistence.user.UserAttributeEntity;
import com.mjamsek.auth.persistence.user.UserCredentialsEntity;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.auth.services.UserService;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.services.Validator;
import org.mindrot.jbcrypt.BCrypt;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mjamsek.auth.utils.StringUtil.fieldSet;

@RequestScoped
public class UserServiceImpl implements UserService {
    
    private static final Logger LOG = LogManager.getLogger(UserServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private Validator validator;
    
    @Override
    public EntityList<User> getUsers(QueryParameters queryParameters) {
        List<User> users = JPAUtils.getEntityStream(em, UserEntity.class, queryParameters)
            .map(UserMapper::fromEntity)
            .collect(Collectors.toList());
        
        long usersCount = JPAUtils.queryEntitiesCount(em, UserEntity.class, queryParameters);
        
        return new EntityList<>(users, usersCount);
    }
    
    @Override
    public User getUser(String userId) {
        return getUserEntityById(userId)
            .map(UserMapper::fromEntity)
            .orElseThrow(() -> new NotFoundException(""));
    }
    
    @Override
    public User createUser(User user) {
        validator.assertNotBlank(user.getUsername());
        
        UserEntity entity = new UserEntity();
        entity.setUsername(user.getUsername());
        if (user.getEmail() != null) {
            entity.setEmail(user.getEmail());
        }
        if (user.getFirstName() != null) {
            entity.setFirstName(user.getFirstName());
        }
        if (user.getLastName() != null) {
            entity.setLastName(user.getLastName());
        }
        if (user.getAttributes() != null) {
            List<UserAttributeEntity> attrs = user.getAttributes().stream()
                .map(attr -> {
                    UserAttributeEntity attrEntity = UserMapper.toEntity(attr);
                    attrEntity.setUser(entity);
                    return attrEntity;
                })
                .collect(Collectors.toList());
            entity.setAttributes(attrs);
        }
        
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            
            return UserMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public User patchUser(String userId, User user) {
        UserEntity entity = getUserEntityById(userId)
            .orElseThrow(() -> new NotFoundException(""));
        try {
            em.getTransaction().begin();
            entity.setFirstName(user.getFirstName());
            entity.setLastName(user.getLastName());
            em.flush();
            em.getTransaction().commit();
            return UserMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Optional<ErrorCode> registerUser(RegistrationRequest request) {
        Optional<UserEntity> existingUser = getUserEntityByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            return Optional.of(ErrorCode.USERNAME_TAKEN);
        }
        
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAvatar(request.getAvatar());
    
        UserCredentialsEntity userPassword = new UserCredentialsEntity();
        userPassword.setUser(user);
        userPassword.setType(CredentialsType.PASSWORD);
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12));
        userPassword.setSecret(hashedPassword);
        user.setCredentials(List.of(userPassword));
        
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return Optional.empty();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Optional<ErrorCode> updateUserProfile(String userId, UserProfileUpdateRequest request) {
        Optional<UserEntity> userOpt = getUserEntityById(userId);
        if (userOpt.isEmpty()) {
            return Optional.of(ErrorCode.INVALID_CREDENTIALS);
        }
        UserEntity entity = userOpt.get();
        
        try {
            em.getTransaction().begin();
            if (fieldSet(request.getFirstName())) {
                entity.setFirstName(request.getFirstName());
            }
            if (fieldSet(request.getLastName())) {
                entity.setLastName(request.getLastName());
            }
            if (fieldSet(request.getAvatar())) {
                entity.setAvatar(request.getAvatar());
            }
            if (fieldSet(request.getEmail())) {
                entity.setEmail(request.getEmail());
            }
            
            if (fieldSet(request.getPassword())) {
                UserCredentialsEntity userPassword = new UserCredentialsEntity();
                userPassword.setUser(entity);
                userPassword.setType(CredentialsType.PASSWORD);
                String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12));
                userPassword.setSecret(hashedPassword);
                entity.setCredentials(List.of(userPassword));
            }
        
            em.flush();
            em.getTransaction().commit();
            return Optional.empty();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Optional<UserEntity> getUserEntityById(String userId) {
        return Optional.ofNullable(em.find(UserEntity.class, userId));
    }
    
    @Override
    public Optional<UserEntity> getUserEntityByUsername(String username) {
        TypedQuery<UserEntity> query = em.createNamedQuery(UserEntity.GET_BY_USERNAME, UserEntity.class);
        query.setParameter("username", username);
        
        try {
            UserEntity entity = query.getSingleResult();
            return Optional.of(entity);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    private boolean fieldIsSet(String fieldValue) {
        return fieldValue != null && !fieldValue.trim().isBlank();
    }
}
