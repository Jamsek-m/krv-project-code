package com.mjamsek.auth.api.endpoints;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.auth.lib.User;
import com.mjamsek.auth.lib.requests.PasswordCredentialRequest;
import com.mjamsek.auth.services.CredentialsService;
import com.mjamsek.auth.services.UserService;
import com.mjamsek.rest.Rest;
import com.mjamsek.rest.dto.EntityList;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserEndpoint {
    
    @Inject
    private QueryParameters queryParameters;
    
    @Inject
    private UserService userService;
    
    @Inject
    private CredentialsService credentialsService;
    
    @GET
    public Response queryUsers() {
        EntityList<User> users = userService.getUsers(queryParameters);
        return Response.ok(users.getEntityList())
            .header(Rest.HttpHeaders.X_TOTAL_COUNT, users.getCount())
            .build();
    }
    
    @GET
    @Path("/{userId}")
    public Response getUser(@PathParam("userId") String userId) {
        User user = userService.getUser(userId);
        return Response.ok(user).build();
    }
    
    @POST
    public Response createUser(User user) {
        User createdUser = userService.createUser(user);
        return Response.status(Response.Status.CREATED).entity(createdUser).build();
    }
    
    @POST
    @Path("/{userId}/credentials")
    public Response createUserCredentials(@PathParam("userId") String userId, PasswordCredentialRequest req) {
        credentialsService.assignPasswordCredential(userId, req.getPassword());
        return Response.noContent().build();
    }
    
}