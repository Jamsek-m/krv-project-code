package com.mjamsek.auth.services.impl;

import com.mjamsek.auth.lib.AuthContext;
import com.mjamsek.auth.lib.annotations.PublicResource;
import com.mjamsek.auth.lib.annotations.ScopesRequired;
import com.mjamsek.auth.services.AdminService;
import com.mjamsek.rest.exceptions.ForbiddenException;
import com.mjamsek.rest.exceptions.UnauthorizedException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

@RequestScoped
public class AdminServiceImpl implements AdminService {

    @Inject
    private AuthContext authContext;
    
    @Override
    public void processSecurity(InvocationContext context) throws UnauthorizedException, ForbiddenException {
        ScopesRequired scopes = getScopesRequiredAnnotation(context.getMethod());
        if (scopes != null) {
            if (this.isNotPublic(context.getMethod())) {
                this.validateScopes(scopes);
            }
        }
    }
    
    private ScopesRequired getScopesRequiredAnnotation(Method method) {
        ScopesRequired scopes = method.getAnnotation(ScopesRequired.class);
        if (scopes == null) {
            return method.getDeclaringClass().getAnnotation(ScopesRequired.class);
        }
        return scopes;
    }
    
    private boolean classAnnotatedScopes(Method method) {
        ScopesRequired scopes = method.getAnnotation(ScopesRequired.class);
        return scopes == null;
    }
    
    private <T> boolean isNotPublic(Method method) {
        if (classAnnotatedScopes(method)) {
            PublicResource publicResource = method.getDeclaredAnnotation(PublicResource.class);
            return publicResource == null;
        }
        return true;
    }
    
    private void validateAuthenticated() throws NotAuthorizedException {
        if (!authContext.isAuthenticated()) {
            throw new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
    
    private void validateScopes(ScopesRequired annotation) throws NotAuthorizedException, javax.ws.rs.ForbiddenException {
        this.validateAuthenticated();
        
        Set<String> allowedScopes = Set.of(annotation.value());
        Set<String> userRoles = authContext.getScope();
        
        boolean hasRole = !Collections.disjoint(userRoles, allowedScopes);
        if (!hasRole) {
            throw new javax.ws.rs.ForbiddenException(Response.status(Response.Status.FORBIDDEN).build());
        }
    }
}
