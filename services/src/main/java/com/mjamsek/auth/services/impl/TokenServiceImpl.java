package com.mjamsek.auth.services.impl;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.mjamsek.auth.lib.enums.TokenType;
import com.mjamsek.auth.lib.requests.TokenRequest;
import com.mjamsek.auth.lib.responses.TokenResponse;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.auth.services.*;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

@RequestScoped
public class TokenServiceImpl implements TokenService {
    
    @Inject
    private ClientService clientService;
    
    @Inject
    private CredentialsService credentialsService;
    
    @Inject
    private AuthorizationService authorizationService;
    
    private JwtBuilder jwtBuilder;
    
    private KeyPair signingKeyPair;
    
    @PostConstruct
    private void init() {
        ConfigurationUtil configUtil = ConfigurationUtil.getInstance();
    
        this.signingKeyPair = Keys.keyPairFor(SignatureAlgorithm.RS512);
        
        String issuer = configUtil.get("kumuluzee.server.base-url").orElse("");
        this.jwtBuilder = Jwts.builder()
            .setIssuer(issuer);
            // .claim("iss", issuer);
    }
    
    @Override
    public TokenResponse clientCredentialsFlow(TokenRequest.ClientCredentialsTokenRequest req) {
        UserEntity serviceAccount = clientService.validateServiceAccount(req.getClientId(), req.getClientSecret());
        return createToken(serviceAccount);
    }
    
    @Override
    public TokenResponse authorizationFlow(TokenRequest.AuthorizationCodeTokenRequest req) {
        UserEntity authorizedUser = authorizationService.getUserByCode(req.getCode())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials!"));
        return createToken(authorizedUser);
    }
    
    @Override
    public TokenResponse passwordFlow(TokenRequest.PasswordTokenRequest req) {
        UserEntity authorizedUser = credentialsService.checkPasswordCredentials(req.getUsername(), req.getPassword());
        return createToken(authorizedUser);
    }
    
    private TokenResponse createToken(UserEntity user) {
        
        JwtBuilder userBuilder = this.jwtBuilder
            .setSubject(user.getId())
            // .claim("sub", user.getId())
            .claim("preferred_username", user.getUsername())
            .claim("given_name", user.getFirstName())
            .claim("family_name", user.getLastName())
            .claim("email", user.getEmail());
        
        String accessToken = userBuilder.claim("typ", TokenType.ACCESS.type())
            .signWith(signingKeyPair.getPrivate())
            .compact();
        String refreshToken = userBuilder.claim("typ", TokenType.REFRESH.type())
            .signWith(signingKeyPair.getPrivate())
            .compact();
        
        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }
}
