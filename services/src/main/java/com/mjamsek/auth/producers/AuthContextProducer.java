package com.mjamsek.auth.producers;

import com.mjamsek.auth.lib.AuthContext;
import com.mjamsek.auth.services.TokenService;
import com.mjamsek.rest.exceptions.UnauthorizedException;
import io.jsonwebtoken.Claims;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.util.Optional;

import static com.mjamsek.auth.lib.constants.JwtClaimsConstants.*;

@RequestScoped
public class AuthContextProducer {
    
    @Context
    private HttpServletRequest httpRequest;
    
    @Inject
    private TokenService tokenService;
    
    @Produces
    @RequestScoped
    public AuthContext produceAuthContext() {
        Optional<String> token = getHeaderValue();
        if (token.isEmpty()) {
            throw new UnauthorizedException("No credentials!");
        }
        return token.flatMap(t -> {
                return tokenService.validateToken(token.get());
            }).map(claims -> {
                Claims tokenClaims = claims.getBody();
                AuthContext.Builder contextBuilder = AuthContext.Builder.newBuilder();
                contextBuilder.authenticated(true);
                contextBuilder.token(token.get());
                contextBuilder.payload(tokenClaims);
                contextBuilder.id(tokenClaims.get(Claims.SUBJECT, String.class));
                contextBuilder.email(tokenClaims.get(EMAIL_CLAIM, String.class));
                contextBuilder.username(tokenClaims.get(PREFERRED_USERNAME_CLAIM, String.class));
                contextBuilder.scope(tokenClaims.get(SCOPE_CLAIM, String.class));
                return contextBuilder.build();
            }).orElse(AuthContext.Builder.newEmptyContext());
    }
    
    private Optional<String> getHeaderValue() {
        return Optional.ofNullable(httpRequest.getHeader(HttpHeaders.AUTHORIZATION))
            .map(AuthContextProducer::trimAuthorizationHeader);
    }
    
    private static String trimAuthorizationHeader(String authorizationHeaderValue) {
        if (authorizationHeaderValue == null) {
            return null;
        }
        
        if (authorizationHeaderValue.startsWith("Bearer ")) {
            return authorizationHeaderValue.replace("Bearer ", "");
        }
        
        return authorizationHeaderValue;
    }
    
}
