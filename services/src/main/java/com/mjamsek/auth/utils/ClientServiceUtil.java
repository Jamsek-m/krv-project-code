package com.mjamsek.auth.utils;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.rest.exceptions.RestException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.Optional;

public class ClientServiceUtil {
    
    private static final Logger LOG = LogManager.getLogger(ClientServiceUtil.class.getName());
    
    public static Optional<ClientEntity> getClientByClientId(EntityManager em, String clientId) {
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
    
}
