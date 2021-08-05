package com.mjamsek.auth.api.endpoints;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.mjamsek.auth.lib.Role;
import com.mjamsek.auth.lib.requests.RoleGrantRequest;
import com.mjamsek.auth.services.RoleService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/roles")
@RequestScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RoleEndpoint {
    
    @Inject
    private RoleService roleService;
    
    @Inject
    private QueryParameters queryParams;
    
    @GET
    public Response getRoles() {
        return Response.ok(roleService.getRoles(queryParams)).build();
    }
    
    @GET
    @Path("/{roleId}")
    public Response getRole(@PathParam("roleId") String roleId) {
        return Response.ok(roleService.getRole(roleId)).build();
    }
    
    @POST
    public Response createRole(Role role) {
        Role createdRole = roleService.createRole(role);
        return Response.status(Response.Status.CREATED).entity(createdRole).build();
    }
    
    @PATCH
    @Path("/{roleId}")
    public Response patchRole(@PathParam("roleId") String roleId, Role role) {
        Role updatedRole = roleService.patchRole(roleId, role);
        return Response.ok(updatedRole).build();
    }
    
    @DELETE
    @Path("/{roleId}")
    public Response deleteRole(@PathParam("roleId") String roleId) {
        roleService.deleteRole(roleId);
        return Response.noContent().build();
    }
    
    @POST
    @Path("/assign")
    public Response assignRoleToUser(RoleGrantRequest request) {
        roleService.assignRoleToUser(request.getUserId(), request.getRoleId());
        return Response.noContent().build();
    }
    
    @DELETE
    @Path("/unassign")
    public Response unassignRoleToUser(RoleGrantRequest request) {
        roleService.removeRoleFromUser(request.getUserId(), request.getRoleId());
        return Response.noContent().build();
    }
}
