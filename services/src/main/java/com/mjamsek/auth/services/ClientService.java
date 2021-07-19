package com.mjamsek.auth.services;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.auth.lib.Client;
import com.mjamsek.auth.persistence.client.ClientEntity;
import com.mjamsek.auth.persistence.user.UserEntity;
import com.mjamsek.rest.dto.EntityList;

import java.util.Optional;

public interface ClientService {

    EntityList<Client> queryClients(QueryParameters queryParameters);
    
    Client getClient(String clientId);
    
    Optional<ClientEntity> getClientByClientId(String clientId);
    
    Client createClient(Client client);
    
    void enableClient(String clientId);
    
    void disableClient(String clientId);
    
    UserEntity validateServiceAccount(String clientId, String clientSecret);

}
