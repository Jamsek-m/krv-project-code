package com.mjamsek.auth.services;

import com.mjamsek.rest.exceptions.ForbiddenException;
import com.mjamsek.rest.exceptions.UnauthorizedException;

import javax.interceptor.InvocationContext;

public interface AdminService {
    
    void processSecurity(InvocationContext context) throws UnauthorizedException, ForbiddenException;
    
}
