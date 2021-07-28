package com.mjamsek.auth.api;

import com.mjamsek.auth.api.endpoints.ClientEndpoint;
import com.mjamsek.auth.api.endpoints.KeysEndpoint;
import com.mjamsek.auth.api.endpoints.SettingsEndpoint;
import com.mjamsek.auth.api.endpoints.UserEndpoint;
import com.mjamsek.auth.api.mappers.*;

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
        classes.add(KeysEndpoint.class);
        classes.add(SettingsEndpoint.class);
        
        classes.add(DefaultExceptionMapper.class);
        classes.add(GenericExceptionMapper.class);
        classes.add(UnauthorizedExceptionMapper.class);
        classes.add(RUUnauthorizedExceptionMapper.class);
        classes.add(ValidationExceptionMapper.class);
        classes.add(ForbiddenExceptionMapper.class);
        
        return classes;
    }
    
}
