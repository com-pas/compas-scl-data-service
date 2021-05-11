package org.lfenergy.compas.scl.data.rest;

import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.rest.model.*;
import org.lfenergy.compas.scl.data.service.CompasDataService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/scl/v1")
public class CompasDataResource {
    private CompasDataService compasDataService;

    @Inject
    public CompasDataResource(CompasDataService compasDataService) {
        this.compasDataService = compasDataService;
    }

    @GET
    @Path("/{type}/{uuid}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public GetResponse findSCLByUUID(@PathParam("type") SclType type, @PathParam("uuid") String uuid) {
        GetResponse response = new GetResponse();
        response.setScl(compasDataService.findSCLByUUID(type, UUID.fromString(uuid)));
        return response;
    }

    @POST
    @Path("/{type}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public CreateResponse create(@PathParam("type") SclType type, CreateRequest request) {
        CreateResponse response = new CreateResponse();
        response.setUuid(compasDataService.create(type, request.getName(), request.getScl()));
        return response;
    }

    @PUT
    @Path("/{type}/{uuid}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public UpdateResponse update(@PathParam("type") SclType type, @PathParam("uuid") String uuid, UpdateRequest request) {
        UpdateResponse response = new UpdateResponse();
        response.setUuid(compasDataService.update(type, UUID.fromString(uuid), request.getUpdateType(), request.getScl()));
        return response;
    }

    @DELETE
    @Path("/{type}/{uuid}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public void delete(@PathParam("type") SclType type, @PathParam("uuid") String uuid) {
        compasDataService.delete(type, UUID.fromString(uuid));
    }
}