package com.mjamsek.auth.persistence.auth;

import com.mjamsek.auth.persistence.BaseEntity;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.user.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "authorization_requests")
@NamedQueries({
    @NamedQuery(name = AuthorizationRequestEntity.CLEANUP_EXPIRED, query = "DELETE FROM AuthorizationRequestEntity a WHERE a.codeExpiration < :nowDate"),
    @NamedQuery(name = AuthorizationRequestEntity.GET_BY_CODE, query = "SELECT a.user FROM AuthorizationRequestEntity a WHERE a.code = :code AND a.codeExpiration > :nowDate")
})
public class AuthorizationRequestEntity extends BaseEntity {
    
    public static final String CLEANUP_EXPIRED = "AuthorizationRequestEntity.cleanupExpired";
    public static final String GET_BY_CODE = "AuthorizationRequestEntity.getByCode";
    
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    
    @Column(name = "user_ip", nullable = false)
    private String userIp;
    
    @Column(name = "code")
    private String code;
    
    @Column(name = "code_expiration")
    @Temporal(TemporalType.TIMESTAMP)
    private Date codeExpiration;
    
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
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public Date getCodeExpiration() {
        return codeExpiration;
    }
    
    public void setCodeExpiration(Date codeExpiration) {
        this.codeExpiration = codeExpiration;
    }
    
    public String getUserIp() {
        return userIp;
    }
    
    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }
    
}
