package com.mjamsek.auth.services.impl;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.mjamsek.auth.lib.WellKnownConfig;
import com.mjamsek.auth.services.WellKnownService;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class WellKnownServiceImpl implements WellKnownService {
    
    
    @Override
    public WellKnownConfig getConfig() {
        ConfigurationUtil conf = ConfigurationUtil.getInstance();
        
        WellKnownConfig wellKnown = new WellKnownConfig();
        String baseUrl = conf.get("kumuluzee.server.base-url").orElse("");
        wellKnown.setIssuer(baseUrl);
        wellKnown.setAuthorizationEndpoint(baseUrl + "/auth");
        wellKnown.setTokenEndpoint(baseUrl + "/token");
        wellKnown.setUserinfoEndpoint(baseUrl + "/userinfo");
        
        return wellKnown;
    }
}
