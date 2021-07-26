package com.mjamsek.auth.services.registry;

import com.mjamsek.auth.services.keys.SigningKey;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class KeyRegistry {
    
    private final Map<UUID, SigningKey> registry = new ConcurrentHashMap<>();
    
    public void loadKeys(List<SigningKey> keys) {
        keys.forEach(key -> {
            registry.put(key.getKid(), key);
        });
    }
    
    public void clearRegistry() {
        this.registry.clear();
    }
    
    public synchronized Map<UUID, SigningKey> getKeys() {
        return new ConcurrentHashMap<>(registry);
    }
    
}
