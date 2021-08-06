package com.mjamsek.auth.api.listeners;

import com.mjamsek.auth.workers.AuthorizationWorker;
import com.mjamsek.auth.workers.InitializerWorker;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@ApplicationScoped
public class OnStartupListener implements ServletContextListener {
    
    @Inject
    private AuthorizationWorker authorizationWorker;
    
    @Inject
    private InitializerWorker initializerWorker;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        authorizationWorker.cleanupExpiredRequests();
        
        initializerWorker.initializeApplication();
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    
    }
}
