package com.mjamsek.auth.services.impl;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.mjamsek.auth.lib.JsonWebKey;
import com.mjamsek.auth.lib.JwksConfig;
import com.mjamsek.auth.lib.WellKnownConfig;
import com.mjamsek.auth.lib.enums.TokenGrantType;
import com.mjamsek.auth.mappers.SigningKeyMapper;
import com.mjamsek.auth.services.SigningService;
import com.mjamsek.auth.services.WellKnownService;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

import static com.mjamsek.auth.lib.constants.JwtClaimsConstants.*;
import static com.mjamsek.auth.lib.constants.ScopeConstants.*;
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
        wellKnown.setEndSessionEndpoint(baseUrl + END_SESSION_SERVLET_PATH);
        wellKnown.setCheckSessionIframe(baseUrl + CHECK_SESSION_IFRAME_SERVLET_PATH);
        
        wellKnown.setGrantTypesSupported(TokenGrantType.rawValues());
        wellKnown.setIdTokenSigningAlgValuesSupported(getSupportedAlgorithms());
        wellKnown.setUserinfoSigningAlgValuesSupported(getSupportedAlgorithms());
        wellKnown.setTokenEndpointAuthSigningAlgValuesSupported(getSupportedAlgorithms());
        wellKnown.setClaimsSupported(getSupportedClaims());
        wellKnown.setScopesSupported(getSupportedScopes());
        wellKnown.setCodeChallengeMethodsSupported(List.of("plain", "S256"));
        
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
    
    private List<String> getSupportedAlgorithms() {
        return List.of(
            SignatureAlgorithm.HS256,
            SignatureAlgorithm.HS384,
            SignatureAlgorithm.HS512,
            
            SignatureAlgorithm.RS256,
            SignatureAlgorithm.RS384,
            SignatureAlgorithm.RS512,
            
            SignatureAlgorithm.ES256,
            SignatureAlgorithm.ES384,
            SignatureAlgorithm.ES512
        ).stream()
            .map(SignatureAlgorithm::getValue)
            .collect(Collectors.toList());
    }
    
    private List<String> getSupportedClaims() {
        return List.of(
            AUDIENCE_CLAIM,
            SUBJECT_CLAIM,
            ISSUER_CLAIM,
            NAME_CLAIM,
            GIVEN_NAME_CLAIM,
            FAMILY_NAME_CLAIM,
            PREFERRED_USERNAME_CLAIM,
            EMAIL_CLAIM
        );
    }
    
    private List<String> getSupportedScopes() {
        return List.of(
            OPENID_SCOPE,
            PROFILE_SCOPE,
            EMAIL_SCOPE,
            OFFLINE_ACCESS_SCOPE
        );
    }
}
