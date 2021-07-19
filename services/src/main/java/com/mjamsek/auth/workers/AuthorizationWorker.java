package com.mjamsek.auth.workers;

public interface AuthorizationWorker {
    
    void cleanupExpiredRequests();
    
}
