package com.mjamsek.auth.workers.impl;

import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.utils.ClientServiceUtil;
import com.mjamsek.auth.workers.ClientWorker;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.Optional;

@ApplicationScoped
public class ClientWorkerImpl implements ClientWorker {
    
    @Inject
    private EntityManagerFactory emFactory;
    
    @Override
    public Optional<ClientEntity> getClientByClientId(String clientId) {
        EntityManager em = emFactory.createEntityManager();
        return ClientServiceUtil.getClientByClientId(em, clientId);
    }
}
