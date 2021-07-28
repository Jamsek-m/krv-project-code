package com.mjamsek.auth.services.settings;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.auth.lib.Settings;
import com.mjamsek.auth.persistence.settings.SettingsEntity;
import com.mjamsek.rest.exceptions.RestException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestScoped
public class SettingsServiceImpl implements SettingsService {
    
    private static final Logger LOG = LogManager.getLogger(SettingsServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Override
    public Map<String, Settings> getSettings(List<String> keys) {
        TypedQuery<SettingsEntity> query = em.createNamedQuery(SettingsEntity.GET_BY_KEYS, SettingsEntity.class);
        query.setParameter("keys", keys);
        try {
            return query.getResultStream()
                .map(SettingsMapper::fromEntity)
                .collect(Collectors.toMap(Settings::getKey, s -> s));
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
}
