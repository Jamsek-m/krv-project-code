package com.mjamsek.auth.runnables;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.auth.persistence.auth.AuthorizationRequestEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.util.Date;

@ApplicationScoped
public class AuthorizationRequestCleanupTask implements Runnable {
    
    private static final Logger LOG = LogManager.getLogger(AuthorizationRequestCleanupTask.class.getName());
    
    @Inject
    private EntityManagerFactory emFactory;
    
    @Override
    public void run() {
        EntityManager em = emFactory.createEntityManager();
    
        Query query = em.createNamedQuery(AuthorizationRequestEntity.CLEANUP_EXPIRED);
        query.setParameter("nowDate", new Date());
        
        try {
            em.getTransaction().begin();
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.warn(e);
        }
    }
}
