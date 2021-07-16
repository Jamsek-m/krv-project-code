package com.mjamsek.auth.persistence.client;

import com.mjamsek.auth.lib.enums.ClientStatus;
import com.mjamsek.auth.lib.enums.ClientType;
import com.mjamsek.auth.persistence.BaseEntity;
import com.mjamsek.auth.persistence.converters.ListConverter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "clients", indexes = {
    @Index(name = "IDX_CLIENTS_UNIQUE_CLIENT_ID", columnList = "client_id", unique = true)
})
@NamedQueries({
    @NamedQuery(name = ClientEntity.GET_BY_CLIENT_ID, query = "SELECT c FROM ClientEntity c WHERE c.clientId = :clientId")
})
public class ClientEntity extends BaseEntity {
    
    public static final String GET_BY_CLIENT_ID = "ClientEntity.getByClientId";
    
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
}
