package com.mjamsek.auth.api.endpoints;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.auth.lib.JsonWebKey;
import com.mjamsek.auth.lib.VerificationKeyWrapper;
import com.mjamsek.auth.lib.annotations.ScopesRequired;
import com.mjamsek.auth.lib.annotations.SecureResource;
import com.mjamsek.auth.lib.requests.CreateSignatureRequest;
import com.mjamsek.auth.lib.responses.PublicSigningKey;
import com.mjamsek.auth.services.SigningService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/signing-keys")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecureResource
public class KeysEndpoint {
    
    @Inject
    private QueryParameters queryParameters;
    
    @Inject
    private SigningService signingService;
    
    @GET
    @ScopesRequired({"admin"})
    public Response getKeys() {
        List<PublicSigningKey> keys = signingService.getSigningKeys(queryParameters);
        return Response.ok(keys).build();
    }
    
    @GET
    @Path("/{keyId}/verification-key")
    @Produces(MediaType.TEXT_PLAIN)
    @ScopesRequired({"admin"})
    public Response getPlainVerificationKey(@PathParam("keyId") String keyId) {
        VerificationKeyWrapper verificationKey = signingService.getPlainSigningKey(keyId);
        return Response.ok()
            .header("X-Key-Id", verificationKey.getKeyId())
            .entity(verificationKey.getPlainKey())
            .build();
    }
    
    @POST
    @ScopesRequired({"admin"})
    public Response createNewKey(CreateSignatureRequest request) {
        JsonWebKey createdKey = signingService.createNewSigningKey(request);
        return Response.status(Response.Status.CREATED).entity(createdKey).build();
    }
    
    @PATCH
    @Path("/{keyId}")
    @ScopesRequired({"admin"})
    public Response patchKey(@PathParam("keyId") String keyId, PublicSigningKey key) {
        PublicSigningKey updatedKey = signingService.patchSigningKey(keyId, key);
        return Response.ok(updatedKey).build();
    }
}
