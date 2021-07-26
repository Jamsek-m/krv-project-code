package com.mjamsek.auth.api.listeners;

import com.mjamsek.auth.workers.AuthorizationWorker;
import com.mjamsek.auth.workers.SigningWorker;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@ApplicationScoped
public class OnStartupListener implements ServletContextListener {
    
    @Inject
    private AuthorizationWorker authorizationWorker;
    
    @Inject
    private SigningWorker signingWorker;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        authorizationWorker.cleanupExpiredRequests();
        
        signingWorker.loadKeys();
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    
    }
}
