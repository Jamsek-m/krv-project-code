package com.mjamsek.auth.workers.impl;

import com.mjamsek.auth.lib.requests.CreateSignatureRequest;
import com.mjamsek.auth.services.keys.SigningKey;
import com.mjamsek.auth.services.registry.KeyRegistry;
import com.mjamsek.auth.utils.SigningServiceUtil;
import com.mjamsek.auth.workers.SigningWorker;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.List;

@ApplicationScoped
public class SigningWorkerImpl implements SigningWorker {
    
    @Inject
    private EntityManagerFactory emFactory;
    
    @Inject
    private KeyRegistry keyRegistry;
    
    @Override
    public void loadKeys() {
        EntityManager em = emFactory.createEntityManager();
        
        // Create RSA key for testing
        boolean keyExists = SigningServiceUtil.keyExists(em, SignatureAlgorithm.RS256);
        if (!keyExists) {
            CreateSignatureRequest signatureRequest = new CreateSignatureRequest();
            signatureRequest.setAlgorithm(SignatureAlgorithm.RS256.getValue());
            SigningServiceUtil.createNewSigningKey(em, signatureRequest);
        }
        
        // Load keys into registry
        List<SigningKey> keyList = SigningServiceUtil.getSigningKeys(em);
        keyRegistry.loadKeys(keyList);
    }
}
