package com.mjamsek.auth.persistence.auth;

import com.mjamsek.auth.lib.enums.PKCEMethod;
import com.mjamsek.auth.persistence.BaseEntity;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.user.UserEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "authorization_requests", indexes = {
    @Index(name = "IDX_AUTHREQ_CLIENT_IP_UNIQUE", columnList = "client_id,user_ip", unique = true),
    @Index(name = "IDX_AUTHREQ_CODE_SEARCH", columnList = "code,client_id")
})
@NamedQueries({
    @NamedQuery(name = AuthorizationRequestEntity.CLEANUP_EXPIRED, query = "DELETE FROM AuthorizationRequestEntity a WHERE a.codeExpiration < :nowDate"),
    @NamedQuery(name = AuthorizationRequestEntity.GET_BY_CODE, query = "SELECT a FROM AuthorizationRequestEntity a WHERE a.code = :code AND a.codeExpiration > :nowDate AND a.client.clientId = :clientId"),
    @NamedQuery(name = AuthorizationRequestEntity.GET_BY_CLIENT_IP, query = "SELECT a FROM AuthorizationRequestEntity a WHERE a.userIp = :userIp AND a.client.id = :clientId")
})
public class AuthorizationRequestEntity extends BaseEntity {
    
    public static final String CLEANUP_EXPIRED = "AuthorizationRequestEntity.cleanupExpired";
    public static final String GET_BY_CODE = "AuthorizationRequestEntity.getByCode";
    public static final String GET_BY_CLIENT_IP = "AuthorizationRequestEntity.getByClientIp";
    
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
    
    @Column(name = "pkce_challenge")
    private String pkceChallenge;
    
    @Column(name = "pkce_challenge_method")
    @Enumerated(EnumType.STRING)
    private PKCEMethod pkceMethod;
    
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
    
    public String getPkceChallenge() {
        return pkceChallenge;
    }
    
    public void setPkceChallenge(String pkceChallenge) {
        this.pkceChallenge = pkceChallenge;
    }
    
    public PKCEMethod getPkceMethod() {
        return pkceMethod;
    }
    
    public void setPkceMethod(PKCEMethod pkceMethod) {
        this.pkceMethod = pkceMethod;
    }
}
