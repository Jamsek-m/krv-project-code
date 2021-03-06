package com.mjamsek.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.auth.lib.Client;
import com.mjamsek.auth.lib.enums.ClientStatus;
import com.mjamsek.auth.lib.enums.ClientType;
import com.mjamsek.auth.mappers.ClientMapper;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.client.ClientScopeEntity;
import com.mjamsek.auth.services.ClientService;
import com.mjamsek.auth.utils.ClientServiceUtil;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import com.mjamsek.rest.services.Validator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@RequestScoped
public class ClientServiceImpl implements ClientService {
    
    private static final Logger LOG = LogManager.getLogger(ClientServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private Validator validator;
    
    @Override
    public EntityList<Client> queryClients(QueryParameters queryParameters) {
        List<Client> clients = JPAUtils.getEntityStream(em, ClientEntity.class, queryParameters)
            .map(ClientMapper::fromEntity)
            .collect(Collectors.toList());
        
        long clientCount = JPAUtils.queryEntitiesCount(em, ClientEntity.class, queryParameters);
        
        return new EntityList<>(clients, clientCount);
    }
    
    @Override
    public Client getClient(String clientId) {
        return getClientByClientId(clientId)
            .map(ClientMapper::fromEntity)
            .orElseThrow(() -> new NotFoundException(""));
    }
    
    @Override
    public Optional<ClientEntity> getClientByClientId(String clientId) {
        return ClientServiceUtil.getClientByClientId(em, clientId);
    }
    
    @Override
    public Optional<ClientEntity> getEntityById(String id) {
        return Optional.ofNullable(em.find(ClientEntity.class, id));
    }
    
    @Override
    public Client createClient(Client client) {
        validator.assertNotBlank(client.getClientId());
        validator.assertNotBlank(client.getName());
        
        ClientEntity entity = new ClientEntity();
        entity.setClientId(client.getClientId());
        entity.setName(client.getName());
        entity.setStatus(ClientStatus.ENABLED);
        entity.setRedirectUris(client.getRedirectUris());
        entity.setRequireConsent(true);
        entity.setType(client.getType());
        if (client.getType().equals(ClientType.CONFIDENTIAL)) {
            entity.setSecret(UUID.randomUUID().toString());
        }
        
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return ClientMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Client patchClient(String clientId, Client client) {
        ClientEntity entity = getEntityById(clientId)
            .orElseThrow(() -> new NotFoundException(""));
        
        try {
            em.getTransaction().begin();
            if (client.getType() != null) {
                if (entity.getType().equals(ClientType.CONFIDENTIAL) && !client.getType().equals(ClientType.CONFIDENTIAL)) {
                    entity.setSecret(null);
                } else if (!entity.getType().equals(ClientType.CONFIDENTIAL) && client.getType().equals(ClientType.CONFIDENTIAL)) {
                    entity.setSecret(UUID.randomUUID().toString());
                }
                entity.setType(client.getType());
            }
            if (client.getName() != null) {
                entity.setName(client.getName());
            }
            if (client.getRedirectUris() != null && client.getRedirectUris().size() > 0) {
                entity.setRedirectUris(client.getRedirectUris());
            }
            if (client.isRequireConsent() != null) {
                entity.setRequireConsent(client.isRequireConsent());
            }
            if (client.getPkceMethod() != null) {
                entity.setPkceMethod(client.getPkceMethod());
            }
            if (client.getSigningKeyAlgorithm() != null) {
                entity.setSigningKeyAlgorithm(client.getSigningKeyAlgorithm());
            }
            if (client.getScopes() != null) {
                processScopes(entity, client);
            }
            
            em.getTransaction().commit();
            return ClientMapper.fromEntity(entity);
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public void regenerateClientSecret(String clientId) {
        ClientEntity entity = getEntityById(clientId)
            .orElseThrow(() -> new NotFoundException(""));
        try {
            em.getTransaction().begin();
            entity.setSecret(UUID.randomUUID().toString());
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    private void processScopes(ClientEntity entity, Client client) {
        List<ClientScopeEntity> currentScopes = entity.getScopes();
        List<String> newScopes = client.getScopes();
        
        List<ClientScopeEntity> toBeRemoved = currentScopes.stream()
            .filter(scopeEntity -> !newScopes.contains(scopeEntity.getName()))
            .collect(Collectors.toList());
        
        for (var e : toBeRemoved) {
            em.remove(e);
        }
        em.flush();
        
        
        List<String> rawCurrentScopes = currentScopes.stream().map(ClientScopeEntity::getName)
            .collect(Collectors.toList());
        
        List<ClientScopeEntity> toBeAdded = newScopes.stream()
            .filter(scope -> !rawCurrentScopes.contains(scope))
            .map(scope -> {
                ClientScopeEntity scopeEntity = new ClientScopeEntity();
                scopeEntity.setClient(entity);
                scopeEntity.setName(scope);
                return scopeEntity;
            })
            .collect(Collectors.toList());
        
        for (var e : toBeAdded) {
            em.persist(e);
        }
        em.flush();
    }
    
    @Override
    public void enableClient(String clientId) {
        updateClientStatus(clientId, ClientStatus.ENABLED);
    }
    
    @Override
    public void disableClient(String clientId) {
        updateClientStatus(clientId, ClientStatus.DISABLED);
    }
    
    @Override
    public ClientEntity validateServiceAccount(String clientId, String clientSecret) throws UnauthorizedException {
        ClientEntity client = getClientByClientId(clientId)
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials!"));
        
        if (!client.getSecret().equals(clientSecret)) {
            throw new UnauthorizedException("Invalid credentials!");
        }
        return client;
    }
    
    private void updateClientStatus(String clientId, ClientStatus newStatus) {
        ClientEntity entity = getClientByClientId(clientId)
            .orElseThrow(() -> new NotFoundException(""));
        
        try {
            em.getTransaction().begin();
            entity.setStatus(newStatus);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            LOG.error(e);
            em.getTransaction().rollback();
            throw new RestException("error.server");
        }
    }
}
