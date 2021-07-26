package com.mjamsek.auth.services.impl;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.mjamsek.auth.lib.JsonWebKey;
import com.mjamsek.auth.lib.JwksConfig;
import com.mjamsek.auth.lib.WellKnownConfig;
import com.mjamsek.auth.mappers.SigningKeyMapper;
import com.mjamsek.auth.services.SigningService;
import com.mjamsek.auth.services.WellKnownService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.mjamsek.auth.lib.constants.ServerPaths.*;

@RequestScoped
public class WellKnownServiceImpl implements WellKnownService {
    
    @Inject
    private SigningService signingService;
    
    @Override
    public WellKnownConfig getConfig() {
        ConfigurationUtil conf = ConfigurationUtil.getInstance();
        
        WellKnownConfig wellKnown = new WellKnownConfig();
        String baseUrl = conf.get("kumuluzee.server.base-url").orElse("");
        wellKnown.setIssuer(baseUrl);
        wellKnown.setAuthorizationEndpoint(baseUrl + AUTHORIZATION_SERVLET_PATH);
        wellKnown.setTokenEndpoint(baseUrl + TOKEN_SERVLET_PATH);
        wellKnown.setUserinfoEndpoint(baseUrl + USERINFO_SERVLET_PATH);
        wellKnown.setJwksUri(baseUrl + JWKS_SERVLET_PATH);
        
        return wellKnown;
    }
    
    @Override
    public JwksConfig getJwksConfig() {
        List<JsonWebKey> keys = signingService.getKeys()
            .stream().map(SigningKeyMapper::fromEntityToJwk)
            .collect(Collectors.toList());
        
        JwksConfig jwksConfig = new JwksConfig();
        jwksConfig.setKeys(keys);
        
        return jwksConfig;
    }
    
}
