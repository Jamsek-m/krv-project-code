package com.mjamsek.auth.api.mappers;

import com.mjamsek.rest.exceptions.dto.ExceptionResponse;
import com.mjamsek.rest.services.Localizator;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {
    
    private static final String ERROR_CODE = "error.forbidden";
    
    @Inject
    private Localizator localizator;
    
    @Context
    private HttpServletRequest request;
    
    @Override
    public Response toResponse(ForbiddenException exception) {
        String message = localizator.getTranslation(ERROR_CODE, request.getLocale());
    
        ExceptionResponse error = new ExceptionResponse();
        error.setCode(ERROR_CODE);
        error.setMessage(message);
        error.setStatus(Response.Status.FORBIDDEN.getStatusCode());
    
        return Response.status(Response.Status.FORBIDDEN)
            .entity(error)
            .build();
    }
}
