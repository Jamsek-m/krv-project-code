package com.mjamsek.auth.producers;

import com.mjamsek.auth.config.OidcServerConfig;
import com.mjamsek.auth.lib.OidcConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class OidcConfigProducer {
    
    @Inject
    private OidcServerConfig serverConfig;
    
    @Produces
    @RequestScoped
    OidcConfig produceOidcConfig() {
        OidcConfig config = new OidcConfig();
        config.setIssuer(serverConfig.getIssuer());
        return config;
    }
    
}
