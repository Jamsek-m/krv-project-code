package com.mjamsek.auth.mappers;

import com.mjamsek.auth.lib.Client;
import com.mjamsek.auth.persistence.client.ClientEntity;

import java.util.Collections;

public class ClientMapper {
    
    public static Client fromEntity(ClientEntity entity) {
        Client client = BaseMapper.mapBase(entity, new Client());
        
        client.setClientId(entity.getClientId());
        client.setName(entity.getName());
        client.setStatus(entity.getStatus());
        client.setType(entity.getType());
        if (entity.getRedirectUris() != null) {
            client.setRedirectUris(entity.getRedirectUris());
        } else {
            client.setRedirectUris(Collections.emptyList());
        }
        
        return client;
    }
    
}
