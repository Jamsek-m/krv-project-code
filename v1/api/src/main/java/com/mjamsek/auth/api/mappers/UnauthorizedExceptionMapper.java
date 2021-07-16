package com.mjamsek.auth.api.mappers;

import com.mjamsek.rest.exceptions.dto.ExceptionResponse;
import com.mjamsek.rest.services.Localizator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@RequestScoped
@Provider
public class UnauthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {
    
    private static final String ERROR_CODE = "error.unauthorized";
    
    @Inject
    private Localizator localizator;
    
    @Context
    private HttpServletRequest request;
    
    @Override
    public Response toResponse(NotAuthorizedException exception) {
        String message = localizator.getTranslation(ERROR_CODE, request.getLocale());
        
        ExceptionResponse error = new ExceptionResponse();
        error.setCode(ERROR_CODE);
        error.setMessage(message);
        error.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
    
        return Response.status(Response.Status.UNAUTHORIZED)
            .entity(error)
            .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer realm=\"ping-pong\", charset=\"UTF-8\"")
            .build();
    }
}
