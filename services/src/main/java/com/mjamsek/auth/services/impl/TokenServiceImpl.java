package com.mjamsek.auth.services.impl;

import com.mjamsek.auth.config.TokenConfig;
import com.mjamsek.auth.lib.OidcConfig;
import com.mjamsek.auth.lib.SessionContext;
import com.mjamsek.auth.lib.enums.PKCEMethod;
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
import com.mjamsek.auth.utils.SetUtil;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import static com.mjamsek.auth.lib.constants.JwtClaimsConstants.*;
import static com.mjamsek.auth.lib.constants.ScopeConstants.*;

@RequestScoped
public class TokenServiceImpl implements TokenService {
    
    private static final Set<String> DEFAULT_SCOPES = Set.of(OPENID_SCOPE, PROFILE_SCOPE, EMAIL_SCOPE);
    
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
    private RoleService roleService;
    
    @Inject
    private SessionContext sessionContext;
    
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
        
        Set<String> clientScopes = client.getRawScopes();
        Set<String> requestedScopes = Set.of(req.getScope().split(" "));
        Set<String> scopes = getAppliedScopes(clientScopes, clientScopes, requestedScopes);
        
        return createToken(serviceAccountBuilder, client, null, scopes);
    }
    
    @Override
    public TokenResponse authorizationGrant(AuthorizationCodeRequest req) {
        AuthorizationRequestEntity request = authorizationService.getRequestByCode(req.getCode(), req.getClientId())
            .orElseThrow(() -> new UnauthorizedException("Invalid credentials!"));
        
        authorizationService.removeAuthorizationRequest(request.getId());
        
        ClientEntity client = request.getClient();
        UserEntity user = request.getUser();
        
        if (client.getPkceMethod() != null && !client.getPkceMethod().equals(PKCEMethod.NONE)) {
            verifyPKCEChallenge(request.getPkceChallenge(), req.getCodeVerifier(), client.getPkceMethod());
        }
    
        Set<String> clientScopes = client.getRawScopes();
        Set<String> requestedScopes = new HashSet<>(clientScopes);
        if (req.getScope() != null) {
            requestedScopes = Set.of(req.getScope().split(" "));
        }
        Set<String> userScopes = roleService.getUserScopes(user.getId());
        Set<String> scopes = getAppliedScopes(userScopes, clientScopes, requestedScopes);
        
        return createToken(jwtBuilder, client, user, scopes);
    }
    
    @Override
    public TokenResponse passwordGrant(PasswordRequest req) {
        UserEntity user = credentialsService.checkPasswordCredentials(req.getUsername(), req.getPassword());
        ClientEntity client = clientService.getClientByClientId(req.getClientId())
            .orElseThrow(() -> new UnauthorizedException("Unknown client!"));
    
        Set<String> clientScopes = client.getScopes().stream()
            .map(ClientScopeEntity::getName)
            .collect(Collectors.toSet());
        Set<String> requestedScopes = Set.of(req.getScope().split(" "));
        Set<String> userScopes = roleService.getUserScopes(user.getId());
        Set<String> scopes = getAppliedScopes(userScopes, clientScopes, requestedScopes);
    
        return createToken(jwtBuilder, client, user, scopes);
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
    
        Set<String> clientScopes = client.getScopes().stream()
            .map(ClientScopeEntity::getName)
            .collect(Collectors.toSet());
        Set<String> requestedScopes = Set.of(claims.getBody().get(SCOPE_CLAIM, String.class).split(" "));
        Set<String> userScopes = roleService.getUserScopes(user.getId());
        Set<String> scopes = getAppliedScopes(userScopes, clientScopes, requestedScopes);
    
        return createToken(jwtBuilder, client, user, scopes);
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
    
    private TokenResponse createToken(JwtBuilder builder, ClientEntity client, UserEntity user, Set<String> scopes) {
        TokenResponse response = new TokenResponse();
        
        SigningKeyEntity keyEntity = signingService
            .getEntityByAlgorithm(client.getSigningKeyAlgorithm())
            .or(() -> signingService.getDefaultKey())
            .orElseThrow(() -> new RestException("No keys setup!"));
        Key signingKey = getSigningKey(keyEntity);
        
        String stringifiedScopes = Strings.join(scopes, ' ');
        builder.claim(SCOPE_CLAIM, stringifiedScopes);
        response.setScope(stringifiedScopes);
        
        // Fields if token is for user
        if (user != null) {
            if (scopes.contains(PROFILE_SCOPE)) {
                builder = builder
                    .claim(PREFERRED_USERNAME_CLAIM, user.getUsername())
                    .claim(GIVEN_NAME_CLAIM, user.getFirstName())
                    .claim(FAMILY_NAME_CLAIM, user.getLastName())
                    .claim(NAME_CLAIM, user.getFirstName() + " " + user.getLastName());
            }
            if (scopes.contains(EMAIL_SCOPE)) {
                builder = builder.claim(EMAIL_CLAIM, user.getEmail());
            }
        }
        
        // Common fields
        builder = builder
            .setSubject(user != null ? user.getId() : client.getClientId())
            .setIssuedAt(new Date())
            .setHeaderParam(HEADER_KID_CLAIM, keyEntity.getId());
        
        if (sessionContext.isActive()) {
            builder = builder.claim(SESSION_STATE_CLAIM, sessionContext.getSessionId());
        }
        
        String accessToken = builder
            .claim(TYPE_CLAIM, TokenType.ACCESS.type())
            .setExpiration(DatetimeUtil.getMinutesAfterNow(tokenConfig.getAccessTokenLifetime()))
            .claim(AUTHORIZED_PARTY_CLAIM, client.getClientId())
            .setAudience(client.getClientId())
            .signWith(signingKey)
            .compact();
        response.setAccessToken(accessToken);
        
        if (scopes.contains(OFFLINE_ACCESS_SCOPE)) {
            // Handle offline tokens as part of (non-expiring) session
            String offlineToken = builder
                .claim(TYPE_CLAIM, TokenType.OFFLINE.type())
                .claim(AUTHORIZED_PARTY_CLAIM, client.getClientId())
                .claim(EXPIRATION_TIME_CLAIM, 0)
                .setAudience(client.getClientId())
                .signWith(signingKey)
                .compact();
            response.setRefreshToken(offlineToken);
        } else {
            String refreshToken = builder
                .claim(TYPE_CLAIM, TokenType.REFRESH.type())
                .setExpiration(DatetimeUtil.getMinutesAfterNow(tokenConfig.getRefreshTokenLifetime()))
                .claim(AUTHORIZED_PARTY_CLAIM, client.getClientId())
                .setAudience(client.getClientId())
                .signWith(signingKey)
                .compact();
            response.setRefreshToken(refreshToken);
        }
        
        if (scopes.contains(OPENID_SCOPE)) {
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
    
    private Set<String> getAppliedScopes(Set<String> userScopes, Set<String> clientRequestedScopes, Set<String> requestedScopes) {
        Set<String> userAppliedScopes = SetUtil.union(userScopes, DEFAULT_SCOPES);
        return SetUtil.intersection(SetUtil.intersection(requestedScopes, clientRequestedScopes), userAppliedScopes);
    }
    
    private void verifyPKCEChallenge(String codeChallenge, String codeVerifier, PKCEMethod method) throws UnauthorizedException {
        if (method.equals(PKCEMethod.PLAIN)) {
            System.err.println(codeChallenge + ", " + codeVerifier + " (c,v)");
            if (!codeChallenge.equals(codeVerifier)) {
                throw new UnauthorizedException("Invalid PKCE challenge!");
            }
        }
        
        if (method.equals(PKCEMethod.S256)) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
                String base64UrlEncoded = Base64.getUrlEncoder().encodeToString(hash)
                    .replaceAll("=", "")
                    .replaceAll("/", "_")
                    .replaceAll("\\+", "-");
                if (!base64UrlEncoded.equals(codeChallenge)) {
                    throw new UnauthorizedException("Invalid PKCE challenge!");
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                throw new RestException("error.server");
            }
        }
    }
    
}
