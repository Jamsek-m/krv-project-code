package com.mjamsek.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.auth.lib.Role;
import com.mjamsek.auth.mappers.RoleMapper;
import com.mjamsek.auth.persistence.user.RoleEntity;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.auth.persistence.user.UserRoleEntity;
import com.mjamsek.auth.services.RoleService;
import com.mjamsek.auth.services.UserService;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.services.Validator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mjamsek.auth.lib.constants.ScopeConstants.*;

@RequestScoped
public class RoleServiceImpl implements RoleService {
    
    private static final Set<String> DEFAULT_SCOPES = Set.of(OPENID_SCOPE, PROFILE_SCOPE, EMAIL_SCOPE);
    
    private static final Logger LOG = LogManager.getLogger(RoleServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private UserService userService;
    
    @Inject
    private Validator validator;
    
    @Override
    public List<Role> getUserRoles(String userId) {
        return getUserRoleEntities(userId)
            .stream()
            .map(RoleMapper::fromEntity)
            .collect(Collectors.toList());
    }
    
    @Override
    public Set<String> getUserScopes(String userId) {
        Set<String> userScopes = new HashSet<>();
        List<RoleEntity> userRoles = getUserRoleEntities(userId);
        userRoles.forEach(userRole -> {
            userScopes.addAll(userRole.getGrantedScopes());
        });
        userScopes.addAll(DEFAULT_SCOPES);
        return userScopes;
    }
    
    @Override
    public List<RoleEntity> getUserRoleEntities(String userId) {
        TypedQuery<RoleEntity> query = em.createNamedQuery(UserRoleEntity.GET_USER_ROLES, RoleEntity.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    @Override
    public List<Role> getRoles(QueryParameters queryParameters) {
        return JPAUtils.getEntityStream(em, RoleEntity.class, queryParameters)
            .map(RoleMapper::fromEntity)
            .collect(Collectors.toList());
    }
    
    @Override
    public Role getRole(String roleId) {
        return getEntityById(roleId)
            .map(RoleMapper::fromEntity)
            .orElseThrow(() -> new NotFoundException(""));
    }
    
    @Override
    public void assignRoleToUser(String userId, String roleId) {
        UserEntity user = userService.getUserEntityById(userId)
            .orElseThrow(() -> new NotFoundException(""));
        
        RoleEntity role = getEntityById(roleId)
            .orElseThrow(() -> new NotFoundException(""));
        
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUser(user);
        userRole.setRole(role);
        
        try {
            em.getTransaction().begin();
            em.persist(userRole);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void removeRoleFromUser(String userId, String roleId) {
        Query query = em.createNamedQuery(UserRoleEntity.DELETE_USER_ROLE);
        query.setParameter("userId", userId);
        query.setParameter("roleId", roleId);
        try {
            em.getTransaction().begin();
            query.executeUpdate();
            em.flush();
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Role createRole(Role role) {
        validator.assertNotBlank(role.getName());
        RoleEntity entity = new RoleEntity();
        entity.setName(role.getName());
        entity.setDescription(role.getDescription());
        
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return RoleMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Role patchRole(String roleId, Role role) {
        RoleEntity entity = getEntityById(roleId)
            .orElseThrow(() -> new NotFoundException(""));
        
        try {
            em.getTransaction().begin();
            entity.setGrantedScopes(role.getGrantedScopes());
            em.flush();
            em.getTransaction().commit();
            return RoleMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void deleteRole(String roleId) {
        getEntityById(roleId).ifPresent(entity -> {
            try {
                em.getTransaction().begin();
                em.remove(entity);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                LOG.error(e);
                throw new RestException("error.server");
            }
        });
    }
    
    @Override
    public Optional<RoleEntity> getEntityById(String roleId) {
        return Optional.ofNullable(em.find(RoleEntity.class, roleId));
    }
    
    @Override
    public Optional<RoleEntity> getEntityByName(String roleName) {
        TypedQuery<RoleEntity> query = em.createNamedQuery(RoleEntity.GET_ROLE_BY_NAME, RoleEntity.class);
        query.setParameter("roleName", roleName);
        
        try {
            RoleEntity entity = query.getSingleResult();
            return Optional.of(entity);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
}
