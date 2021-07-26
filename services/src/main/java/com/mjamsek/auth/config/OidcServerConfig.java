package com.mjamsek.auth.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle(".")
public class OidcServerConfig {
    
    @ConfigValue(value = "kumuluzee.server.base-url", watch = true)
    private String issuer;
    
    public String getIssuer() {
        return issuer;
    }
    
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
