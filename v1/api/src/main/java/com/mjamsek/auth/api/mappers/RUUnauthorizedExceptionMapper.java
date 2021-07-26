package com.mjamsek.auth.api.mappers;

import com.mjamsek.rest.exceptions.UnauthorizedException;
import com.mjamsek.rest.services.Localizator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@RequestScoped
@Provider
public class RUUnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {
    
    @Inject
    private Localizator localizator;
    
    @Context
    private HttpServletRequest request;
    
    @Override
    public Response toResponse(UnauthorizedException exception) {
        if (exception.getResponse().getParams() != null && exception.getResponse().getParams().length > 0) {
            String message = localizator.getTranslation(
                exception.getResponse().getCode(),
                request.getLocale(),
                exception.getResponse().getParams()
            );
            exception.setMessage(message);
        } else {
            String message = localizator.getTranslation(
                exception.getResponse().getCode(),
                request.getLocale()
            );
            exception.setMessage(message);
        }
        return exception.getResponse().createResponse();
    }
}
