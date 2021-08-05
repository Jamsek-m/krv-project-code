package com.mjamsek.auth.mappers;

import com.mjamsek.auth.lib.Client;
import com.mjamsek.auth.lib.ClientScope;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.client.ClientScopeEntity;

import java.util.Collections;
import java.util.stream.Collectors;

public class ClientMapper {
    
    public static Client fromEntity(ClientEntity entity) {
        Client client = BaseMapper.mapBase(entity, new Client());
        
        client.setClientId(entity.getClientId());
        client.setName(entity.getName());
        client.setStatus(entity.getStatus());
        client.setType(entity.getType());
        client.setRequireConsent(entity.isRequireConsent());
        client.setPkceMethod(entity.getPkceMethod());
        client.setSigningKeyAlgorithm(entity.getSigningKeyAlgorithm());
        client.setRequireConsent(entity.isRequireConsent());
        client.setSecret(entity.getSecret());
        if (entity.getRedirectUris() != null) {
            client.setRedirectUris(entity.getRedirectUris());
        } else {
            client.setRedirectUris(Collections.emptyList());
        }
        if (entity.getWebOrigins() != null) {
            client.setWebOrigins(entity.getWebOrigins());
        } else {
            client.setWebOrigins(Collections.emptyList());
        }
        if (entity.getScopes() != null) {
            client.setScopes(entity.getScopes().stream().map(ClientMapper::toScopeString).collect(Collectors.toList()));
        }
        
        return client;
    }
    
    public static ClientScope fromEntity(ClientScopeEntity entity) {
        ClientScope scope = BaseMapper.mapBase(entity, new ClientScope());
        scope.setName(entity.getName());
        scope.setClientId(entity.getClient().getId());
        return scope;
    }
    
    public static String toScopeString(ClientScopeEntity entity) {
        return entity.getName();
    }
    
}
