package com.mjamsek.auth.workers;

import com.mjamsek.auth.persistence.client.ClientEntity;

import java.util.Optional;

public interface ClientWorker {
    
    Optional<ClientEntity> getClientByClientId(String clientId);
    
}
