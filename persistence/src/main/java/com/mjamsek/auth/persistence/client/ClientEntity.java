package com.mjamsek.auth.persistence.client;

import com.mjamsek.auth.lib.enums.ClientStatus;
import com.mjamsek.auth.lib.enums.ClientType;
import com.mjamsek.auth.persistence.BaseEntity;
import com.mjamsek.auth.persistence.converters.ListConverter;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "clients", indexes = {
    @Index(name = "IDX_CLIENTS_UNIQUE_CLIENT_ID", columnList = "client_id", unique = true)
})
@NamedQueries({
    @NamedQuery(name = ClientEntity.GET_BY_CLIENT_ID, query = "SELECT c FROM ClientEntity c WHERE c.clientId = :clientId"),
    @NamedQuery(name = ClientEntity.CHECK_CONSENT_REQUIREMENT, query = "SELECT c.requireConsent FROM ClientEntity c WHERE c.clientId = :clientId")
})
public class ClientEntity extends BaseEntity {
    
    public static final String GET_BY_CLIENT_ID = "ClientEntity.getByClientId";
    public static final String CHECK_CONSENT_REQUIREMENT = "ClientEntity.checkConsentRequirement";
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "client_id", unique = true)
    private String clientId;
    
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ClientType type;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ClientStatus status;
    
    @Convert(converter = ListConverter.class)
    @Column(name = "redirect_uris")
    private List<String> redirectUris;
    
    @Column(name = "secret")
    private String secret;
    
    @Column(name = "require_consent")
    private boolean requireConsent;
    
    @OneToMany(mappedBy = "client")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ClientScopeEntity> scopes;
    
    @Column(name = "signing_key_alg")
    @Enumerated(EnumType.STRING)
    private SignatureAlgorithm signingKeyAlorithm;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<String> getRedirectUris() {
        return redirectUris;
    }
    
    public void setRedirectUris(List<String> redirectUrls) {
        this.redirectUris = redirectUrls;
    }
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public ClientStatus getStatus() {
        return status;
    }
    
    public void setStatus(ClientStatus status) {
        this.status = status;
    }
    
    public ClientType getType() {
        return type;
    }
    
    public void setType(ClientType type) {
        this.type = type;
    }
    
    public boolean isRequireConsent() {
        return requireConsent;
    }
    
    public void setRequireConsent(boolean requireConsent) {
        this.requireConsent = requireConsent;
    }
    
    public List<ClientScopeEntity> getScopes() {
        return scopes;
    }
    
    public void setScopes(List<ClientScopeEntity> scopes) {
        this.scopes = scopes;
    }
    
    public SignatureAlgorithm getSigningKeyAlorithm() {
        return signingKeyAlorithm;
    }
    
    public void setSigningKeyAlorithm(SignatureAlgorithm signingKeyAlorithm) {
        this.signingKeyAlorithm = signingKeyAlorithm;
    }
}
