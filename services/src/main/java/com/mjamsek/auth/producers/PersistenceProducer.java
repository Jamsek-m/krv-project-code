package com.mjamsek.auth.producers;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

public class PersistenceProducer {
    
    @PersistenceContext(unitName = "main-jpa-unit")
    private EntityManager em;
    
    @PersistenceUnit(unitName = "main-jpa-unit")
    private EntityManagerFactory emFactory;
    
    @Produces
    @Dependent
    public EntityManager getDefaultEntityManager() {
        return em;
    }
    
    @Produces
    @Dependent
    public EntityManagerFactory getDefaultEntityManagerFactory() {
        return emFactory;
    }
    
    public void close(@Disposes EntityManager em) {
        em.close();
    }
    
    public void closeFactory(@Disposes EntityManagerFactory emFactory) {
        emFactory.close();
    }
    
}
