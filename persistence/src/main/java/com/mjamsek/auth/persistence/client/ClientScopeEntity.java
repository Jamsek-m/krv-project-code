package com.mjamsek.auth.persistence.client;

import com.mjamsek.auth.persistence.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "client_scopes", indexes = {
    @Index(name = "IDX_UNIQUE_CLIENT_SCOPES", columnList = "name,client_id", unique = true)
})
public class ClientScopeEntity extends BaseEntity {
    
    @Column(name = "name")
    private String name;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientEntity client;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ClientEntity getClient() {
        return client;
    }
    
    public void setClient(ClientEntity client) {
        this.client = client;
    }
}
