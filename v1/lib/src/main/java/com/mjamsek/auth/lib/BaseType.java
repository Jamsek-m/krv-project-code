package com.mjamsek.auth.lib;

import java.time.Instant;
import java.util.UUID;

public class BaseType {
    
    private UUID id;
    
    private Instant timestamp;
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}