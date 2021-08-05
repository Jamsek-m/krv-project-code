package com.mjamsek.auth.services.impl;

import com.mjamsek.auth.lib.responses.AnalyticsOverview;
import com.mjamsek.auth.services.AnalyticsService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

@RequestScoped
public class AnalyticsServiceImpl implements AnalyticsService {
    
    @Inject
    private EntityManager em;
    
    @Override
    public AnalyticsOverview getOverview() {
        AnalyticsOverview overview = new AnalyticsOverview();
    
        long usersCount = countQuery("SELECT COUNT(u) FROM UserEntity u");
        overview.setUsersCount(usersCount);
        long clientsCount = countQuery("SELECT COUNT(c) FROM ClientEntity c");
        overview.setClientsCount(clientsCount);
        long rolesCount = countQuery("SELECT COUNT(r) FROM RoleEntity r");
        overview.setRolesCount(rolesCount);
        long keysCount = countQuery("SELECT COUNT(k) FROM SigningKeyEntity k");
        overview.setKeysCount(keysCount);
        
        return overview;
    }
    
    private long countQuery(String jpql) {
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        try {
            return query.getSingleResult();
        } catch (PersistenceException e) {
            return 0;
        }
    }
}
