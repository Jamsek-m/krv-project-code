package com.mjamsek.auth.api.endpoints;

import com.mjamsek.auth.lib.JsonWebKey;
import com.mjamsek.auth.lib.VerificationKeyWrapper;
import com.mjamsek.auth.lib.requests.CreateSignatureRequest;
import com.mjamsek.auth.lib.responses.PublicSigningKey;
import com.mjamsek.auth.services.SigningService;
import io.jsonwebtoken.SignatureAlgorithm;

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
public class KeysEndpoint {
    
    @Inject
    private SigningService signingService;
    
    @GET
    public Response getKeys() {
        List<PublicSigningKey> keys = signingService.getSigningKeys();
        return Response.ok(keys).build();
    }
    
    @GET
    @Path("/{keyId}/verification-key")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getPlainVerificationKey(@PathParam("keyId") String keyId) {
        VerificationKeyWrapper verificationKey = signingService.getPlainSigningKey(keyId);
        return Response.ok()
            .header("X-Key-Id", verificationKey.getKeyId())
            .entity(verificationKey.getPlainKey())
            .build();
    }
    
    @POST
    public Response createNewKey(CreateSignatureRequest request) {
        JsonWebKey createdKey = signingService.createNewSigningKey(request);
        return Response.status(Response.Status.CREATED).entity(createdKey).build();
    }
    
    @PATCH
    @Path("/{clientId}")
    public Response assignKeyToClient(@PathParam("clientId") String clientId, CreateSignatureRequest request) {
        SignatureAlgorithm algorithm = SignatureAlgorithm.valueOf(request.getAlgorithm());
        signingService.assignKeyToClient(algorithm, clientId);
        return Response.noContent().build();
    }
}
