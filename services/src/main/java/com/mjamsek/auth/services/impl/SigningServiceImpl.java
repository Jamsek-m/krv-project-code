package com.mjamsek.auth.services.impl;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.auth.lib.JsonWebKey;
import com.mjamsek.auth.lib.requests.CreateSignatureRequest;
import com.mjamsek.auth.lib.responses.PublicSigningKey;
import com.mjamsek.auth.mappers.KeyMapper;
import com.mjamsek.auth.mappers.SigningKeyMapper;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.keys.AsymmetricSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.ECSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.RsaSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.SigningKeyEntity;
import com.mjamsek.auth.services.ClientService;
import com.mjamsek.auth.services.SigningService;
import com.mjamsek.auth.services.keys.SigningKey;
import com.mjamsek.auth.services.registry.KeyRegistry;
import com.mjamsek.auth.services.utils.KeyUtil;
import com.mjamsek.auth.utils.SigningServiceUtil;
import com.mjamsek.rest.exceptions.NotFoundException;
import com.mjamsek.rest.exceptions.RestException;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class SigningServiceImpl implements SigningService {
    
    private static final Logger LOG = LogManager.getLogger(SigningServiceImpl.class.getName());
    
    @Inject
    private EntityManager em;
    
    @Inject
    private ClientService clientService;
    
    @Inject
    private KeyRegistry keyRegistry;
    
    private KeyFactory rsaKeyFactory;
    
    private KeyFactory ecKeyFactory;
    
    @PostConstruct
    private void init() {
        try {
            this.rsaKeyFactory = KeyFactory.getInstance("RSA");
            this.ecKeyFactory = KeyFactory.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public JsonWebKey createNewSigningKey(CreateSignatureRequest request) {
        SigningKeyEntity keyEntity = SigningServiceUtil.createNewSigningKey(em, request);
        
        // Reload key cache on added key
        List<SigningKey> keys = SigningServiceUtil.getSigningKeys(em);
        keyRegistry.clearRegistry();
        keyRegistry.loadKeys(keys);
        
        return SigningKeyMapper.fromEntityToJwk(keyEntity);
    }
    
    @Override
    public PublicSigningKey getSigningKey(SignatureAlgorithm algorithm) {
        return getEntityByAlgorithm(algorithm)
            .map(KeyMapper::fromEntity)
            .orElseThrow(() -> new RestException("No keys setup!"));
    }
    
    @Override
    public Optional<SigningKeyEntity> getEntityByAlgorithm(SignatureAlgorithm algorithm) {
        TypedQuery<SigningKeyEntity> query = em.createNamedQuery(SigningKeyEntity.GET_BY_ALG, SigningKeyEntity.class);
        query.setParameter("algorithm", algorithm);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            throw new RestException("");
        }
    }
    
    @Override
    public Optional<SigningKeyEntity> getDefaultKey() {
        return SigningServiceUtil.getDefaultKey(em);
    }
    
    @Override
    public List<SigningKeyEntity> getKeys() {
        return JPAUtils.queryEntities(em, SigningKeyEntity.class, new QueryParameters());
    }
    
    @Override
    public void assignKeyToClient(SignatureAlgorithm algorithm, String clientId) {
        getEntityByAlgorithm(algorithm)
            .orElseThrow(() -> new NotFoundException("Unknown key!"));
        
        ClientEntity client = clientService.getClientByClientId(clientId)
            .orElseThrow(() -> new NotFoundException("Unknown key!"));
        
        try {
            em.getTransaction().begin();
            client.setSigningKeyAlorithm(algorithm);
            em.merge(client);
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
    @Override
    public PrivateKey getPrivateKeyFromEntity(SigningKeyEntity entity) {
        if (entity instanceof AsymmetricSigningKeyEntity) {
            AsymmetricSigningKeyEntity asymmetricKey = (AsymmetricSigningKeyEntity) entity;
            String privateKey = asymmetricKey.getPrivateKey();
            if (asymmetricKey instanceof ECSigningKeyEntity) {
                return KeyUtil.loadPrivateKey(privateKey, ecKeyFactory);
            } else if (asymmetricKey instanceof RsaSigningKeyEntity) {
                return KeyUtil.loadPrivateKey(privateKey, rsaKeyFactory);
            }
        }
        throw new IllegalArgumentException("Invalid key algorithm!");
    }
    
    @Override
    public PublicKey getPublicKeyFromEntity(SigningKeyEntity entity) {
        if (entity instanceof AsymmetricSigningKeyEntity) {
            AsymmetricSigningKeyEntity asymmetricKey = (AsymmetricSigningKeyEntity) entity;
            String publicKey = asymmetricKey.getPublicKey();
            if (asymmetricKey instanceof ECSigningKeyEntity) {
                return KeyUtil.loadPublicKey(publicKey, ecKeyFactory);
            } else if (asymmetricKey instanceof RsaSigningKeyEntity) {
                return KeyUtil.loadPublicKey(publicKey, rsaKeyFactory);
            }
        }
        throw new IllegalArgumentException("Invalid key algorithm!");
    }
}
