package com.mjamsek.auth.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ConfigBundle("config.token")
@ApplicationScoped
public class TokenConfig {
    
    @ConfigValue("access.expiration")
    private Integer accessTokenLifetime;
    
    @ConfigValue("refresh.expiration")
    private Integer refreshTokenLifetime;
    
    @ConfigValue("id.expiration")
    private Integer idTokenLifeTime;
    
    public Integer getAccessTokenLifetime() {
        return accessTokenLifetime;
    }
    
    public void setAccessTokenLifetime(Integer accessTokenLifetime) {
        this.accessTokenLifetime = accessTokenLifetime;
    }
    
    public Integer getRefreshTokenLifetime() {
        return refreshTokenLifetime;
    }
    
    public void setRefreshTokenLifetime(Integer refreshTokenLifetime) {
        this.refreshTokenLifetime = refreshTokenLifetime;
    }
    
    public Integer getIdTokenLifeTime() {
        return idTokenLifeTime;
    }
    
    public void setIdTokenLifeTime(Integer idTokenLifeTime) {
        this.idTokenLifeTime = idTokenLifeTime;
    }
}
