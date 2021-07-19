package com.mjamsek.auth.persistence.client;

import com.mjamsek.auth.persistence.BaseEntity;
import com.mjamsek.auth.persistence.user.UserEntity;

import javax.persistence.*;

@Entity
@Table(name = "client_consents")
@NamedQueries({
    @NamedQuery(name = ClientConsentEntity.GET_BY_USER, query = "SELECT c FROM ClientConsentEntity c WHERE c.user.id = :userId AND c.client.clientId = :clientId")
})
public class ClientConsentEntity extends BaseEntity {
    
    public static final String GET_BY_USER = "ClientConsentEntity.getByUser";
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientEntity client;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    public ClientEntity getClient() {
        return client;
    }
    
    public void setClient(ClientEntity client) {
        this.client = client;
    }
    
    public UserEntity getUser() {
        return user;
    }
    
    public void setUser(UserEntity user) {
        this.user = user;
    }
}
