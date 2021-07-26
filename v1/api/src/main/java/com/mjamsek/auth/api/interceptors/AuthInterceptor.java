package com.mjamsek.auth.api.interceptors;

import com.mjamsek.auth.lib.annotations.SecureResource;
import com.mjamsek.auth.services.AdminService;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.Priorities;

@SecureResource
@Interceptor
@Priority(Priorities.AUTHENTICATION)
public class AuthInterceptor {
    
    @Inject
    private AdminService adminService;
    
    @AroundInvoke
    public Object authenticate(InvocationContext context) throws Exception {
        adminService.processSecurity(context);
        return context.proceed();
    }
    
}
