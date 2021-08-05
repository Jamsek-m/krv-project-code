package com.mjamsek.auth.api.endpoints;

import com.mjamsek.auth.services.AnalyticsService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/analytics")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AnalyticsEndpoint {
    
    @Inject
    private AnalyticsService analyticsService;
    
    @GET
    @Path("/overview")
    public Response getAnalyticsOverview() {
        return Response.ok(analyticsService.getOverview()).build();
    }
}
