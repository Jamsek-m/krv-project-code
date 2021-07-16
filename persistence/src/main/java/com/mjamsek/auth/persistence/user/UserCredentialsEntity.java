package com.mjamsek.auth.persistence.user;

import com.mjamsek.auth.lib.enums.CredentialsType;
import com.mjamsek.auth.persistence.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "user_credentials")
public class UserCredentialsEntity extends BaseEntity {
    
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private CredentialsType type;
    
    @Column(name = "secret")
    private String secret;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    public CredentialsType getType() {
        return type;
    }
    
    public void setType(CredentialsType type) {
        this.type = type;
    }
    
    public String getSecret() {
        return secret;
    }
    
    public void setSecret(String secret) {
        this.secret = secret;
    }
    
    public UserEntity getUser() {
        return user;
    }
    
    public void setUser(UserEntity user) {
        this.user = user;
    }
}
