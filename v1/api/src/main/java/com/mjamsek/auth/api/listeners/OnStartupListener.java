package com.mjamsek.auth.api.listeners;

import com.mjamsek.auth.workers.AuthorizationWorker;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@ApplicationScoped
public class OnStartupListener implements ServletContextListener {
    
    @Inject
    private AuthorizationWorker authorizationWorker;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        authorizationWorker.cleanupExpiredRequests();
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    
    }
}
