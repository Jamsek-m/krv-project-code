package com.mjamsek.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryFilter;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.enums.FilterOperation;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.auth.lib.Client;
import com.mjamsek.auth.lib.enums.ClientStatus;
import com.mjamsek.auth.mappers.ClientMapper;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.auth.services.ClientService;
import com.mjamsek.auth.utils.QueryUtil;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import com.mjamsek.rest.services.Validator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
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
        QueryUtil.setDefaultFilterParam(new QueryFilter("status", FilterOperation.EQ, ClientStatus.ENABLED.name()), queryParameters);
        
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
        TypedQuery<ClientEntity> query = em.createNamedQuery(ClientEntity.GET_BY_CLIENT_ID, ClientEntity.class);
        query.setParameter("clientId", clientId);
        
        try {
            ClientEntity entity = query.getSingleResult();
            return Optional.of(entity);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public Client createClient(Client client) {
        validator.assertNotBlank(client.getClientId());
        validator.assertNotBlank(client.getName());
        
        ClientEntity entity = new ClientEntity();
        entity.setClientId(client.getClientId());
        entity.setName(client.getName());
        entity.setStatus(ClientStatus.ENABLED);
        entity.setSecret(UUID.randomUUID().toString());
        entity.setRedirectUris(client.getRedirectUris());
        
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
    public void enableClient(String clientId) {
        updateClientStatus(clientId, ClientStatus.ENABLED);
    }
    
    @Override
    public void disableClient(String clientId) {
        updateClientStatus(clientId, ClientStatus.DISABLED);
    }
    
    @Override
    public UserEntity validateServiceAccount(String clientId, String clientSecret) {
        ClientEntity client = getClientByClientId(clientId)
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials!"));
        
        if (!client.getSecret().equals(clientSecret)) {
            throw new UnauthorizedException("Invalid credentials!");
        }
        
        UserEntity serviceAccount = new UserEntity();
        serviceAccount.setId(client.getId());
        serviceAccount.setEmail(client.getClientId() + "-service@service.org");
        serviceAccount.setUsername(client.getClientId() + "-service");
        return serviceAccount;
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
