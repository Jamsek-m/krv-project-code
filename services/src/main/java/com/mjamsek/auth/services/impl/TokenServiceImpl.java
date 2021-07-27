package com.mjamsek.auth.services.impl;

import com.mjamsek.auth.config.TokenConfig;
import com.mjamsek.auth.lib.OidcConfig;
import com.mjamsek.auth.lib.enums.TokenType;
import com.mjamsek.auth.lib.requests.token.AuthorizationCodeRequest;
import com.mjamsek.auth.lib.requests.token.ClientCredentialsRequest;
import com.mjamsek.auth.lib.requests.token.PasswordRequest;
import com.mjamsek.auth.lib.requests.token.RefreshTokenRequest;
import com.mjamsek.auth.lib.responses.TokenResponse;
import com.mjamsek.auth.persistence.auth.AuthorizationRequestEntity;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.client.ClientScopeEntity;
import com.mjamsek.auth.persistence.keys.AsymmetricSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.HmacSigningKeyEntity;
import com.mjamsek.auth.persistence.keys.SigningKeyEntity;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.auth.services.*;
import com.mjamsek.auth.services.registry.KeyRegistry;
import com.mjamsek.auth.services.resolvers.KeyResolver;
import com.mjamsek.rest.exceptions.RestException;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import com.mjamsek.rest.utils.DatetimeUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.util.Strings;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mjamsek.auth.lib.constants.JwtClaimsConstants.*;
import static com.mjamsek.auth.lib.constants.ScopeConstants.*;

@RequestScoped
public class TokenServiceImpl implements TokenService {
    
    private static final List<String> DEFAULT_SCOPES = List.of(OPENID_SCOPE, PROFILE_SCOPE, EMAIL_SCOPE);
    
    @Inject
    private ClientService clientService;
    
    @Inject
    private CredentialsService credentialsService;
    
    @Inject
    private AuthorizationService authorizationService;
    
    @Inject
    private SigningService signingService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private TokenConfig tokenConfig;
    
    @Inject
    private OidcConfig oidcConfig;
    
    @Inject
    private KeyRegistry keyRegistry;
    
    private JwtBuilder jwtBuilder;
    
    @PostConstruct
    private void init() {
        this.jwtBuilder = Jwts.builder()
            .setIssuer(oidcConfig.getIssuer());
    }
    
    @Override
    public TokenResponse clientCredentialsGrant(ClientCredentialsRequest req) {
        ClientEntity client = clientService.validateServiceAccount(req.getClientId(), req.getClientSecret());
        
        JwtBuilder serviceAccountBuilder = this.jwtBuilder
            .claim("user_type", "Service");
        
        List<String> requestedScopes = client.getScopes().stream()
            .map(ClientScopeEntity::getName)
            .collect(Collectors.toList());
        if (req.getScope() != null && !req.getScope().isBlank()) {
            requestedScopes = Arrays.asList(req.getScope().split(" "));
        }
        
        return createToken(serviceAccountBuilder, client, null, requestedScopes);
    }
    
    @Override
    public TokenResponse authorizationGrant(AuthorizationCodeRequest req) {
        AuthorizationRequestEntity request = authorizationService.getRequestByCode(req.getCode(), req.getClientId())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials!"));
        
        authorizationService.removeAuthorizationRequest(request.getId());
        
        ClientEntity client = request.getClient();
        UserEntity user = request.getUser();
        
        return createToken(jwtBuilder, client, user);
    }
    
    @Override
    public TokenResponse passwordGrant(PasswordRequest req) {
        UserEntity user = credentialsService.checkPasswordCredentials(req.getUsername(), req.getPassword());
        ClientEntity client = clientService.getClientByClientId(req.getClientId())
            .orElseThrow(() -> new UnauthorizedException("Unknown client!"));
        
        List<String> requestedScopes = client.getScopes().stream()
            .map(ClientScopeEntity::getName)
            .collect(Collectors.toList());
        if (req.getScope() != null && !req.getScope().isBlank()) {
            requestedScopes = Arrays.asList(req.getScope().split(" "));
        }
        
        return createToken(jwtBuilder, client, user, requestedScopes);
    }
    
    @Override
    public TokenResponse refreshTokenGrant(RefreshTokenRequest req) {
        Jws<Claims> claims = validateToken(req.getRefreshToken())
            .orElseThrow(() -> new UnauthorizedException("Invalid token!"));
        
        String clientId = claims.getBody().get(AUTHORIZED_PARTY_CLAIM, String.class);
        if (clientId == null) {
            throw new UnauthorizedException("Invalid token!");
        }
        String userId = claims.getBody().getSubject();
        if (userId == null) {
            throw new UnauthorizedException("Invalid token!");
        }
        
        if (!claims.getBody().get(TYPE_CLAIM, String.class).equals(TokenType.REFRESH.type())) {
            throw new UnauthorizedException("Invalid token!");
        }
        
        ClientEntity client = clientService.getClientByClientId(clientId)
            .orElseThrow(() -> new UnauthorizedException("Invalid token!"));
        
        UserEntity user = userService.getUserEntityById(userId)
            .orElseThrow(() -> new UnauthorizedException("Invalid token!"));
        
        List<String> requestedScopes = Arrays.asList(
            claims.getBody().get(SCOPE_CLAIM, String.class).split(" ")
        );
        if (req.getScope() != null && !req.getScope().isBlank()) {
            requestedScopes = Arrays.asList(req.getScope().split(" "));
        }
        return createToken(jwtBuilder, client, user, requestedScopes);
    }
    
    @Override
    public Optional<Jws<Claims>> validateToken(String token) {
        KeyResolver keyResolver = new KeyResolver(keyRegistry.getKeys());
        JwtParser jwtParser = Jwts.parserBuilder()
            .setSigningKeyResolver(keyResolver)
            .requireIssuer(oidcConfig.getIssuer())
            .setAllowedClockSkewSeconds(2)
            .build();
        
        try {
            return Optional.of(jwtParser.parseClaimsJws(token));
        } catch (JwtException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    @Override
    public void validTokenOrThrow(String token) throws RestException {
        validateToken(token).orElseThrow(() -> new RestException("Invalid token"));
    }
    
    private TokenResponse createToken(JwtBuilder builder, ClientEntity client, UserEntity user, List<String> scopes) {
        TokenResponse response = new TokenResponse();
        
        SigningKeyEntity keyEntity = signingService
            .getEntityByAlgorithm(client.getSigningKeyAlorithm())
            .or(() -> signingService.getDefaultKey())
            .orElseThrow(() -> new RestException("No keys setup!"));
        Key signingKey = getSigningKey(keyEntity);
        
        List<String> appliedScopes = getScopeIntersection(client.getScopes(), scopes);
        String stringifiedScopes = Strings.join(appliedScopes, ' ');
        builder.claim(SCOPE_CLAIM, stringifiedScopes);
        response.setScope(stringifiedScopes);
        
        // Fields if token is for user
        if (user != null) {
            if (appliedScopes.contains(PROFILE_SCOPE)) {
                builder = builder
                    .claim(PREFERRED_USERNAME_CLAIM, user.getUsername())
                    .claim(GIVEN_NAME_CLAIM, user.getFirstName())
                    .claim(FAMILY_NAME_CLAIM, user.getLastName())
                    .claim(NAME_CLAIM, user.getFirstName() + " " + user.getLastName());
            }
            if (appliedScopes.contains(EMAIL_SCOPE)) {
                builder = builder.claim(EMAIL_CLAIM, user.getEmail());
            }
        }
        
        // Common fields
        builder = builder
            .setSubject(user != null ? user.getId() : client.getClientId())
            .setIssuedAt(new Date())
            .setHeaderParam(HEADER_KID_CLAIM, keyEntity.getId());
        
        String accessToken = builder
            .claim(TYPE_CLAIM, TokenType.ACCESS.type())
            .setExpiration(DatetimeUtil.getMinutesAfterNow(tokenConfig.getAccessTokenLifetime()))
            .claim(AUTHORIZED_PARTY_CLAIM, client.getClientId())
            .setAudience(client.getClientId())
            .signWith(signingKey)
            .compact();
        response.setAccessToken(accessToken);
        
        String refreshToken = builder
            .claim(TYPE_CLAIM, TokenType.REFRESH.type())
            .setExpiration(DatetimeUtil.getMinutesAfterNow(tokenConfig.getRefreshTokenLifetime()))
            .claim(AUTHORIZED_PARTY_CLAIM, client.getClientId())
            .setAudience(client.getClientId())
            .signWith(signingKey)
            .compact();
        response.setRefreshToken(refreshToken);
        
        if (appliedScopes.contains(OPENID_SCOPE)) {
            String idToken = builder
                .claim(TYPE_CLAIM, TokenType.ID.type())
                .setExpiration(DatetimeUtil.getMinutesAfterNow(tokenConfig.getIdTokenLifeTime()))
                .claim(AUTHORIZED_PARTY_CLAIM, client.getClientId())
                .setAudience(client.getClientId())
                .signWith(signingKey)
                .compact();
            response.setIdToken(idToken);
        }
        response.setExpiresIn(tokenConfig.getAccessTokenLifetime() * 60);
        return response;
    }
    
    private TokenResponse createToken(JwtBuilder builder, ClientEntity client, UserEntity user) {
        return createToken(builder, client, user, DEFAULT_SCOPES);
    }
    
    private Key getSigningKey(SigningKeyEntity keyEntity) {
        if (keyEntity instanceof AsymmetricSigningKeyEntity) {
            AsymmetricSigningKeyEntity asymmetricKey = (AsymmetricSigningKeyEntity) keyEntity;
            return signingService.getPrivateKeyFromEntity(asymmetricKey);
        } else if (keyEntity instanceof HmacSigningKeyEntity) {
            HmacSigningKeyEntity hmacKey = (HmacSigningKeyEntity) keyEntity;
            return Keys.hmacShaKeyFor(hmacKey.getSecretKey().getBytes(StandardCharsets.UTF_8));
        }
        throw new IllegalArgumentException("Invalid signing algorithm!");
    }
    
    private List<String> getScopeIntersection(List<ClientScopeEntity> clientScopes, List<String> requestedScopes) {
        if (requestedScopes != null && requestedScopes.size() > 0) {
            return clientScopes.stream().map(ClientScopeEntity::getName)
                .distinct()
                .filter(requestedScopes::contains)
                .collect(Collectors.toList());
        }
        return DEFAULT_SCOPES;
    }
}
