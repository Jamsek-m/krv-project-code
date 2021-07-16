package com.mjamsek.auth.api.mappers;

import com.mjamsek.rest.exceptions.ValidationException;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@RequestScoped
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
    
    @Context
    private HttpServletRequest request;
    
    @Override
    public Response toResponse(ValidationException exception) {
        return exception.getResponse().createResponse();
    }
}
