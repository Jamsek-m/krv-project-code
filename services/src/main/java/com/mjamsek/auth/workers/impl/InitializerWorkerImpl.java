package com.mjamsek.auth.workers.impl;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.mjamsek.auth.lib.Role;
import com.mjamsek.auth.lib.enums.*;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.client.ClientScopeEntity;
import com.mjamsek.auth.persistence.keys.RsaSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.SigningKeyEntity;
import com.mjamsek.auth.persistence.settings.SettingsEntity;
import com.mjamsek.auth.persistence.user.RoleEntity;
import com.mjamsek.auth.persistence.user.UserCredentialsEntity;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.auth.persistence.user.UserRoleEntity;
import com.mjamsek.auth.services.keys.SigningKey;
import com.mjamsek.auth.services.registry.KeyRegistry;
import com.mjamsek.auth.services.utils.KeyUtil;
import com.mjamsek.auth.utils.SigningServiceUtil;
import com.mjamsek.auth.workers.InitializerWorker;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.mindrot.jbcrypt.BCrypt;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.*;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mjamsek.auth.lib.constants.ScopeConstants.*;

@ApplicationScoped
public class InitializerWorkerImpl implements InitializerWorker {
    
    private static final Logger LOG = LogManager.getLogger(InitializerWorkerImpl.class.getName());
    
    @Inject
    private EntityManagerFactory emFactory;
    
    @Inject
    private KeyRegistry keyRegistry;
    
    @Override
    public void initializeApplication() {
        EntityManager em = emFactory.createEntityManager();
        
        try {
            em.getTransaction().begin();
    
            this.createAdminConsoleClient(em);
            this.createSigningKey(em);
            this.createSettings(em);
            this.createAdminRole(em).ifPresent(adminRole -> {
                this.createAdminUser(em, adminRole);
            });
            em.flush();
            em.getTransaction().commit();
    
            this.loadSigningKeyRegistry(em);
            
        } catch (PersistenceException e) {
            em.getTransaction().rollback();
            LOG.error(e);
            System.exit(1);
        }
    }
    
    private void createAdminConsoleClient(EntityManager em) {
        if (adminConsoleExists(em)) {
            return;
        }
    
        ClientEntity adminConsoleClient = new ClientEntity();
        adminConsoleClient.setClientId("admin-console");
        adminConsoleClient.setName("Admin Console");
        adminConsoleClient.setType(ClientType.PUBLIC);
        adminConsoleClient.setRequireConsent(false);
        adminConsoleClient.setStatus(ClientStatus.ENABLED);
        
        adminConsoleClient.setPkceMethod(PKCEMethod.S256);
        adminConsoleClient.setSecret(UUID.randomUUID().toString());
    
        String baseUrl = ConfigurationUtil.getInstance().get("kumuluzee.server.base-url").orElse("http://localhost:8080");
        String envName = ConfigurationUtil.getInstance().get("kumuluzee.env.name").orElse("dev");
        if (envName.equals("dev")) {
            adminConsoleClient.setRedirectUris(List.of(baseUrl + "/auth/callback/oidc", "http://localhost:4200/auth/callback/oidc"));
            adminConsoleClient.setWebOrigins(List.of(baseUrl, "http://localhost:4200"));
        } else {
            adminConsoleClient.setRedirectUris(List.of(baseUrl + "/auth/callback/oidc"));
            adminConsoleClient.setWebOrigins(List.of(baseUrl));
        }
    
        ClientScopeEntity openidScope = new ClientScopeEntity();
        openidScope.setClient(adminConsoleClient);
        openidScope.setName(OPENID_SCOPE);
    
        ClientScopeEntity emailScope = new ClientScopeEntity();
        emailScope.setClient(adminConsoleClient);
        emailScope.setName(EMAIL_SCOPE);
    
        ClientScopeEntity profileScope = new ClientScopeEntity();
        profileScope.setClient(adminConsoleClient);
        profileScope.setName(PROFILE_SCOPE);
    
        ClientScopeEntity adminScope = new ClientScopeEntity();
        adminScope.setClient(adminConsoleClient);
        adminScope.setName("admin");
        
        adminConsoleClient.setScopes(List.of(openidScope, emailScope, profileScope, adminScope));
    
        em.persist(adminConsoleClient);
    }
    
    private boolean adminConsoleExists(EntityManager em) {
        TypedQuery<ClientEntity> query = em.createNamedQuery(ClientEntity.GET_BY_CLIENT_ID, ClientEntity.class);
        query.setParameter("clientId", "admin-console");
        
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
    
    private void createSigningKey(EntityManager em) {
        if (RS256KeyExists(em)) {
            return;
        }
    
        RsaSigningKeyEntity rsaSigningKey = new RsaSigningKeyEntity();
        rsaSigningKey.setAlgorithm(SignatureAlgorithm.RS256);
        rsaSigningKey.setKeyType(KeyType.RSA);
    
        KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        rsaSigningKey.setPublicKey(KeyUtil.keyToString(publicKey));
        rsaSigningKey.setPrivateKey(KeyUtil.keyToString(privateKey));
        rsaSigningKey.setPriority(100);
        
        em.persist(rsaSigningKey);
    }
    
    private boolean RS256KeyExists(EntityManager em) {
        TypedQuery<SigningKeyEntity> query = em.createNamedQuery(SigningKeyEntity.GET_BY_ALG, SigningKeyEntity.class);
        query.setParameter("algorithm", SignatureAlgorithm.RS256);
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
    
    private void loadSigningKeyRegistry(EntityManager em) {
        List<SigningKey> keyList = SigningServiceUtil.getSigningKeys(em);
        keyRegistry.loadKeys(keyList);
    }
    
    private void createSettings(EntityManager em) {
        if (registrationSettingsExists(em)) {
            return;
        }
        
        SettingsEntity entity = new SettingsEntity();
        entity.setValue("true");
        entity.setType(SettingsValueType.BOOLEAN);
        entity.setKey("static.config.registration.enabled");
    
        em.persist(entity);
    }
    
    private boolean registrationSettingsExists(EntityManager em) {
        TypedQuery<SettingsEntity> query = em.createNamedQuery(SettingsEntity.GET_BY_KEY, SettingsEntity.class);
        query.setParameter("key", "static.config.registration.enabled");
        
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
    
    private Optional<RoleEntity> createAdminRole(EntityManager em) {
        if (adminRoleExists(em)) {
            return Optional.empty();
        }
    
        RoleEntity adminRole = new RoleEntity();
        adminRole.setName("admin");
        adminRole.setDescription("Administrator role");
        adminRole.setGrantedScopes(List.of("admin"));
    
        em.persist(adminRole);
        
        return Optional.of(adminRole);
    }
    
    private boolean adminRoleExists(EntityManager em) {
        TypedQuery<RoleEntity> query = em.createNamedQuery(RoleEntity.GET_ROLE_BY_NAME, RoleEntity.class);
        query.setParameter("roleName", "admin");
    
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
    
    private void createAdminUser(EntityManager em, RoleEntity adminRole) {
        if (adminUserExists(em)) {
            return;
        }
        
        UserEntity adminUser = new UserEntity();
        adminUser.setUsername("admin");
    
        UserCredentialsEntity adminPassword = new UserCredentialsEntity();
        adminPassword.setUser(adminUser);
        adminPassword.setType(CredentialsType.PASSWORD);
        String password = ConfigurationUtil.getInstance().get("config.admin.password")
            .orElseThrow(() -> new PersistenceException("Invalid admin password set!"));
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        adminPassword.setSecret(hashedPassword);
        adminUser.setCredentials(List.of(adminPassword));
        
        em.persist(adminUser);
    
        UserRoleEntity adminUserRole = new UserRoleEntity();
        adminUserRole.setUser(adminUser);
        adminUserRole.setRole(adminRole);
        
        em.persist(adminUserRole);
    }
    
    private boolean adminUserExists(EntityManager em) {
        TypedQuery<UserEntity> query = em.createNamedQuery(UserEntity.GET_BY_USERNAME, UserEntity.class);
        query.setParameter("username", "admin");
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
}
