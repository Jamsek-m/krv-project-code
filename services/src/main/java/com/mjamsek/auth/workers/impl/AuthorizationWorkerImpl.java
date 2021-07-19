package com.mjamsek.auth.workers.impl;

import com.mjamsek.auth.runnables.AuthorizationRequestCleanupTask;
import com.mjamsek.auth.workers.AuthorizationWorker;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class AuthorizationWorkerImpl implements AuthorizationWorker {
    
    private ScheduledExecutorService executorService;
    
    private AuthorizationRequestCleanupTask cleanupTask;
    
    @PostConstruct
    private void init() {
        this.executorService = Executors.newScheduledThreadPool(1);
        this.cleanupTask = CDI.current().select(AuthorizationRequestCleanupTask.class).get();
    }
    
    @Override
    public void cleanupExpiredRequests() {
        executorService.scheduleAtFixedRate(cleanupTask, 0, 1, TimeUnit.HOURS);
    }
    
}
