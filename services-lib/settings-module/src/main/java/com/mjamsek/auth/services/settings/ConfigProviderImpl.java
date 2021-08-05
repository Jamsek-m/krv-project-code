package com.mjamsek.auth.services.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.auth.lib.enums.SettingsValueType;
import com.mjamsek.auth.persistence.settings.SettingsEntity;
import com.mjamsek.rest.exceptions.RestException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.*;
import java.util.Optional;
import java.util.function.Function;

import static com.mjamsek.auth.persistence.settings.SettingsEntity.CONFIG_FILE_PREFIX;

@RequestScoped
public class ConfigProviderImpl implements ConfigProvider {
    
    private static final Logger LOG = LogManager.getLogger(ConfigProviderImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    private ObjectMapper objectMapper;
    
    @PostConstruct
    private void init() {
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public void set(String key, SettingsValueType type, String value) {
        var existing = getSettingsEntityByKey(key);
        if (existing.isPresent()) {
            SettingsEntity entity = existing.get();
            try {
                em.getTransaction().begin();
                entity.setValue(value);
                em.merge(entity);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                LOG.error(e);
                throw new RestException("error.server");
            }
        } else {
            SettingsEntity entity = new SettingsEntity();
            entity.setKey(key);
            entity.setType(type);
            entity.setValue(value);
            try {
                em.getTransaction().begin();
                em.persist(entity);
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                LOG.error(e);
                throw new RestException("error.server");
            }
        }
    }
    
    @Override
    public void delete(String key) {
        Query query = em.createNamedQuery(SettingsEntity.DELETE_BY_KEY);
        query.setParameter("key", key);
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
    public Optional<String> getString(String key) {
        return getSettingsEntityByKey(key)
            .filter(entity -> entity.getType().equals(SettingsValueType.STRING))
            .map(SettingsEntity::getValue)
            .or(() -> getFromStaticConfig(key, parsedKey -> ConfigurationUtil.getInstance().get(parsedKey)));
    }
    
    @Override
    public Optional<Integer> getInteger(String key) {
        return getSettingsEntityByKey(key)
            .filter(entity -> entity.getType().equals(SettingsValueType.INTEGER))
            .map(SettingsEntity::getValue)
            .map(Integer::parseInt)
            .or(() -> getFromStaticConfig(key, parsedKey -> ConfigurationUtil.getInstance().getInteger(parsedKey)));
    }
    
    @Override
    public Optional<Double> getFloat(String key) {
        return getSettingsEntityByKey(key)
            .filter(entity -> entity.getType().equals(SettingsValueType.FLOAT) || entity.getType().equals(SettingsValueType.NUMBER))
            .map(SettingsEntity::getValue)
            .map(Double::parseDouble)
            .or(() -> getFromStaticConfig(key, parsedKey -> ConfigurationUtil.getInstance().getDouble(parsedKey)));
    }
    
    @Override
    public Optional<Double> getNumber(String key) {
        return getFloat(key);
    }
    
    @Override
    public Optional<Boolean> getBoolean(String key) {
        return getSettingsEntityByKey(key)
            .filter(entity -> entity.getType().equals(SettingsValueType.BOOLEAN))
            .map(SettingsEntity::getValue)
            .map(Boolean::parseBoolean)
            .or(() -> getFromStaticConfig(key, parsedKey -> ConfigurationUtil.getInstance().getBoolean(parsedKey)));
    }
    
    @Override
    public Optional<JsonNode> getJson(String key) {
        return getSettingsEntityByKey(key)
            .filter(entity -> entity.getType().equals(SettingsValueType.JSON))
            .map(SettingsEntity::getValue)
            .or(() -> getFromStaticConfig(key, parsedKey -> ConfigurationUtil.getInstance().get(parsedKey)))
            .map(value -> {
                try {
                    return objectMapper.readTree(value);
                } catch (JsonProcessingException e) {
                    throw new IllegalArgumentException("Not a valid JSON string!");
                }
            });
    }
    
    private <T> Optional<T> getFromStaticConfig(String key, Function<String, Optional<T>> staticConfigSupplier) {
        String parsedKey = key;
        if (parsedKey.startsWith(CONFIG_FILE_PREFIX)) {
            parsedKey = parsedKey.replace(CONFIG_FILE_PREFIX + ".", "");
        }
        return staticConfigSupplier.apply(parsedKey);
    }
    
    private Optional<SettingsEntity> getSettingsEntityByKey(String key) {
        TypedQuery<SettingsEntity> query = em.createNamedQuery(SettingsEntity.GET_BY_KEY, SettingsEntity.class);
        query.setParameter("key", key);
        try {
            SettingsEntity entity = query.getSingleResult();
            return Optional.of(entity);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
}
