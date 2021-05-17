// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.v1;

import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.rest.model.*;
import org.lfenergy.compas.scl.data.service.CompasDataService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static org.lfenergy.compas.scl.data.rest.Constants.TYPE_PATH_PARAM;
import static org.lfenergy.compas.scl.data.rest.Constants.UUID_PATH_PARAM;

@Path("/scl/v1/{" + TYPE_PATH_PARAM + "}")
public class CompasSclDataResource {
    private CompasDataService compasDataService;

    @Inject
    public CompasSclDataResource(CompasDataService compasDataService) {
        this.compasDataService = compasDataService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public CreateResponse create(@PathParam(TYPE_PATH_PARAM) SclType type, CreateRequest request) {
        var response = new CreateResponse();
        response.setUuid(compasDataService.create(type, request.getName(), request.getScl()));
        return response;
    }

    @GET
    @Path("/{" + UUID_PATH_PARAM + "}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public GetResponse findSCLByUUID(@PathParam(TYPE_PATH_PARAM) SclType type, @PathParam(UUID_PATH_PARAM) String uuid) {
        var response = new GetResponse();
        response.setScl(compasDataService.findSCLByUUID(type, UUID.fromString(uuid)));
        return response;
    }

    @PUT
    @Path("/{" + UUID_PATH_PARAM + "}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public UpdateResponse update(@PathParam(TYPE_PATH_PARAM) SclType type, @PathParam(UUID_PATH_PARAM) String uuid, UpdateRequest request) {
        var response = new UpdateResponse();
        response.setUuid(compasDataService.update(type, UUID.fromString(uuid), request.getUpdateType(), request.getScl()));
        return response;
    }

    @DELETE
    @Path("/{" + UUID_PATH_PARAM + "}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public void delete(@PathParam(TYPE_PATH_PARAM) SclType type, @PathParam(UUID_PATH_PARAM) String uuid) {
        compasDataService.delete(type, UUID.fromString(uuid));
    }
}