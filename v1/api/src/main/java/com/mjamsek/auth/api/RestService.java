package com.mjamsek.auth.api;

import com.mjamsek.auth.api.endpoints.ClientEndpoint;
import com.mjamsek.auth.api.endpoints.UserEndpoint;
import com.mjamsek.auth.api.mappers.DefaultExceptionMapper;
import com.mjamsek.auth.api.mappers.GenericExceptionMapper;
import com.mjamsek.auth.api.mappers.UnauthorizedExceptionMapper;
import com.mjamsek.auth.api.mappers.ValidationExceptionMapper;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/admin")
public class RestService extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        classes.add(ClientEndpoint.class);
        classes.add(UserEndpoint.class);
        
        classes.add(DefaultExceptionMapper.class);
        classes.add(GenericExceptionMapper.class);
        classes.add(UnauthorizedExceptionMapper.class);
        classes.add(ValidationExceptionMapper.class);
        
        return classes;
    }
    
}
