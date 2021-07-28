package com.mjamsek.auth.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.auth.lib.Client;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.rest.dto.EntityList;
import com.mjamsek.rest.exceptions.UnauthorizedException;

import java.util.Optional;

public interface ClientService {

    EntityList<Client> queryClients(QueryParameters queryParameters);
    
    Client getClient(String clientId);
    
    Optional<ClientEntity> getClientByClientId(String clientId);
    
    Optional<ClientEntity> getEntityById(String id);
    
    Client createClient(Client client);
    
    Client patchClient(String clientId, Client client);
    
    void regenerateClientSecret(String clientId);
    
    void enableClient(String clientId);
    
    void disableClient(String clientId);
    
    ClientEntity validateServiceAccount(String clientId, String clientSecret) throws UnauthorizedException;

}
