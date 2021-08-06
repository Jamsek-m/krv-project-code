package com.mjamsek.auth.api.endpoints;

import com.mjamsek.auth.lib.Settings;
import com.mjamsek.auth.lib.annotations.ScopesRequired;
import com.mjamsek.auth.lib.annotations.SecureResource;
import com.mjamsek.auth.services.settings.ConfigProvider;
import com.mjamsek.auth.services.settings.SettingsService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/settings")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@SecureResource
public class SettingsEndpoint {
    
    @Inject
    private ConfigProvider configProvider;
    
    @Inject
    private SettingsService settingsService;
    
    @POST
    @ScopesRequired({"admin"})
    public Response getSettings(List<String> settingsKeys) {
        var settings = settingsService.getSettings(settingsKeys);
        return Response.ok(settings).build();
    }
    
    @PUT
    @Path("/{key}")
    @ScopesRequired({"admin"})
    public Response updateSettings(@PathParam("key") String key, Settings settings) {
        configProvider.set(key, settings.getType(), settings.getValue());
        return Response.noContent().build();
    }
    
    @DELETE
    @Path("/{key}")
    @ScopesRequired({"admin"})
    public Response deleteSettings(@PathParam("key") String key) {
        configProvider.delete(key);
        return Response.noContent().build();
    }
    
}
