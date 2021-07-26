package com.mjamsek.auth.utils;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import com.mjamsek.auth.lib.enums.KeyType;
import com.mjamsek.auth.lib.requests.CreateSignatureRequest;
import com.mjamsek.auth.persistence.keys.ECSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.HmacSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.RsaSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.SigningKeyEntity;
import com.mjamsek.auth.services.keys.ECSigningKey;
import com.mjamsek.auth.services.keys.HmacSigningKey;
import com.mjamsek.auth.services.keys.RsaSigningKey;
import com.mjamsek.auth.services.keys.SigningKey;
import com.mjamsek.auth.services.utils.KeyUtil;
import com.mjamsek.rest.exceptions.RestException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

import javax.crypto.SecretKey;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SigningServiceUtil {
    
    private static final Logger LOG = LogManager.getLogger(SigningServiceUtil.class.getName());
    
    private SigningServiceUtil() {
    
    }
    
    public static boolean keyExists(EntityManager em, SignatureAlgorithm algorithm) throws RestException {
        return getKeyByAlgorithm(em, algorithm).isPresent();
    }
    
    public static List<SigningKey> getSigningKeys(EntityManager em) {
        List<SigningKeyEntity> entities = JPAUtils.queryEntities(em, SigningKeyEntity.class, new QueryParameters());
        List<SigningKey> keys = new ArrayList<>();
        
        for (SigningKeyEntity key : entities) {
            String kid = key.getId();
            SignatureAlgorithm algorithm = key.getAlgorithm();
            
            if (algorithm.isRsa() && key instanceof RsaSigningKeyEntity) {
                RsaSigningKeyEntity rsaKey = (RsaSigningKeyEntity) key;
                keys.add(new RsaSigningKey(algorithm, kid, rsaKey.getPrivateKey(), rsaKey.getPublicKey()));
            } else if (algorithm.isHmac() && key instanceof HmacSigningKeyEntity) {
                HmacSigningKeyEntity hmacKey = (HmacSigningKeyEntity) key;
                keys.add(new HmacSigningKey(algorithm, kid, hmacKey.getSecretKey()));
            } else if (algorithm.isEllipticCurve() && key instanceof ECSigningKeyEntity) {
                ECSigningKeyEntity ecKey = (ECSigningKeyEntity) key;
                // TODO:
                keys.add(new ECSigningKey(algorithm, kid));
            }
        }
        return keys;
    }
    
    public static Optional<SigningKeyEntity> getDefaultKey(EntityManager em) {
        TypedQuery<SigningKeyEntity> query = em.createNamedQuery(SigningKeyEntity.GET_DEFAULT_KEYS_SORTED, SigningKeyEntity.class);
        query.setMaxResults(1);
        try {
            SigningKeyEntity keyEntity = query.getSingleResult();
            return Optional.of(keyEntity);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            throw new RestException("error.server");
        }
    }
    
    public static SigningKeyEntity createNewSigningKey(EntityManager em, CreateSignatureRequest request) throws RestException {
        SignatureAlgorithm algorithm = SignatureAlgorithm.valueOf(request.getAlgorithm());
        
        getKeyByAlgorithm(em, algorithm).ifPresent(key -> {
            try {
                em.getTransaction().begin();
                em.remove(key);
                em.flush();
                em.getTransaction().commit();
            } catch (PersistenceException e) {
                em.getTransaction().rollback();
                LOG.error(e);
                throw new RestException("error.server");
            }
        });
        
        KeyType keyType = KeyType.getKeyType(algorithm);
        if (algorithm.isRsa()) {
            RsaSigningKeyEntity entity = new RsaSigningKeyEntity();
            entity.setKeyType(KeyType.RSA);
            entity.setAlgorithm(algorithm);
            
            KeyPair keyPair = Keys.keyPairFor(algorithm);
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            
            entity.setPublicKey(KeyUtil.keyToString(publicKey));
            entity.setPrivateKey(KeyUtil.keyToString(privateKey));
            return persistKey(em, entity);
        } else if (algorithm.isHmac()) {
            HmacSigningKeyEntity entity = new HmacSigningKeyEntity();
            entity.setAlgorithm(algorithm);
            entity.setKeyType(keyType);
            
            SecretKey secretKey = Keys.secretKeyFor(algorithm);
            entity.setSecretKey(KeyUtil.keyToString(secretKey));
            return persistKey(em, entity);
        } else if (algorithm.isEllipticCurve()) {
            ECSigningKeyEntity entity = new ECSigningKeyEntity();
            entity.setAlgorithm(algorithm);
            entity.setKeyType(keyType);
            
            Security.addProvider(new BouncyCastleProvider());
            ECNamedCurveParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec(algorithm.getJcaName());
            try {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", "BC");
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                keyPairGenerator.initialize(ecParameterSpec);
                System.err.println(KeyUtil.keyToString(keyPair.getPrivate().getEncoded()));
                System.err.println(KeyUtil.keyToString(keyPair.getPublic().getEncoded()));
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
                e.printStackTrace();
            }
            
            return persistKey(em, entity);
        } else {
            throw new IllegalArgumentException("Invalid algorithm!");
        }
    }
    
    public static Optional<SigningKeyEntity> getKeyByAlgorithm(EntityManager em, SignatureAlgorithm algorithm) {
        TypedQuery<SigningKeyEntity> query = em.createNamedQuery(SigningKeyEntity.GET_BY_ALG, SigningKeyEntity.class);
        query.setParameter("algorithm", algorithm);
        try {
            SigningKeyEntity keyEntity = query.getSingleResult();
            return Optional.of(keyEntity);
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            LOG.error(e);
            throw new RestException("");
        }
    }
    
    private static <E extends SigningKeyEntity> E persistKey(EntityManager em, E entity) {
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.flush();
            em.getTransaction().commit();
            return entity;
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            throw new RestException("error.server");
        }
    }
    
}
