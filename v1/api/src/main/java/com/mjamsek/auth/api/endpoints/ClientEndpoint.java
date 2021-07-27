package com.mjamsek.auth.api.endpoints;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.auth.lib.Client;
import com.mjamsek.auth.services.ClientService;
import com.mjamsek.rest.Rest;
import com.mjamsek.rest.dto.EntityList;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/clients")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClientEndpoint {
    
    @Inject
    private QueryParameters queryParameters;
    
    @Inject
    private ClientService clientService;
    
    @GET
    public Response queryClients() {
        EntityList<Client> clients = clientService.queryClients(queryParameters);
        return Response.ok(clients.getEntityList())
            .header(Rest.HttpHeaders.X_TOTAL_COUNT, clients.getCount())
            .build();
    }
    
    @GET
    @Path("/{clientId}")
    public Response getClient(@PathParam("clientId") String clientId) {
        Client client = clientService.getClient(clientId);
        return Response.ok(client).build();
    }
    
    @POST
    public Response createClient(Client client) {
        Client createdClient = clientService.createClient(client);
        return Response.status(Response.Status.CREATED).entity(createdClient).build();
    }
    
    @PATCH
    @Path("/{clientId}")
    public Response patchClient(@PathParam("clientId") String clientId, Client client) {
        Client updatedClient = clientService.patchClient(clientId, client);
        return Response.ok(updatedClient).build();
    }
    
    @PATCH
    @Path("/{clientId}/enable")
    
    public Response enableClient(@PathParam("clientId") String clientId) {
        clientService.enableClient(clientId);
        return Response.noContent().build();
    }
    
    @DELETE
    @Path("/{clientId}/disable")
    public Response disableClient(@PathParam("clientId") String clientId) {
        clientService.disableClient(clientId);
        return Response.noContent().build();
    }
    
}
