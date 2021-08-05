package com.mjamsek.auth.services;

import com.mjamsek.auth.persistence.sessions.SessionEntity;

import java.util.Optional;

public interface SessionService {
    
    SessionEntity startSession(String ipAddress);
    
    SessionEntity associateUserWithSession(String sessionId, String userId);
    
    Optional<SessionEntity> getSession(String sessionId, String ipAddress);
    
    Optional<SessionEntity> getSessionById(String sessionId);
    
    void endSession(String sessionId);
    
}
