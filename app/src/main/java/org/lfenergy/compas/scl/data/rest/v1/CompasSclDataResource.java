// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.v1;

import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.rest.Constants;
import org.lfenergy.compas.scl.data.rest.model.CreateRequest;
import org.lfenergy.compas.scl.data.rest.model.CreateResponse;
import org.lfenergy.compas.scl.data.rest.model.GetResponse;
import org.lfenergy.compas.scl.data.rest.model.UpdateRequest;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static org.lfenergy.compas.scl.data.rest.Constants.*;

@Path("/scl/v1/{" + TYPE_PATH_PARAM + "}")
public class CompasSclDataResource {
    private CompasSclDataService compasSclDataService;

    @Inject
    public CompasSclDataResource(CompasSclDataService compasSclDataService) {
        this.compasSclDataService = compasSclDataService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public CreateResponse create(@PathParam(TYPE_PATH_PARAM) SclType type,
                                 CreateRequest request) {
        var response = new CreateResponse();
        response.setUuid(compasSclDataService.create(type, request.getName(), request.getScl()));
        return response;
    }

    @GET
    @Path("/{" + UUID_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_XML)
    public GetResponse findSCLByUUID(@PathParam(TYPE_PATH_PARAM) SclType type,
                                     @PathParam(UUID_PATH_PARAM) UUID uuid) {
        var response = new GetResponse();
        response.setScl(compasSclDataService.findSCLByUUID(type, uuid));
        return response;
    }

    @GET
    @Path("/{" + UUID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_XML)
    public GetResponse findSCLByUUIDAnfVersion(@PathParam(TYPE_PATH_PARAM) SclType type,
                                               @PathParam(UUID_PATH_PARAM) UUID uuid,
                                               @PathParam(Constants.VERSION_PATH_PARAM) Version version) {
        var response = new GetResponse();
        response.setScl(compasSclDataService.findSCLByUUID(type, uuid, version));
        return response;
    }

    @GET
    @Path("/{" + UUID_PATH_PARAM + "}/scl")
    @Produces(MediaType.APPLICATION_XML)
    public SCL findRawSCLByUUID(@PathParam(TYPE_PATH_PARAM) SclType type,
                                @PathParam(UUID_PATH_PARAM) UUID uuid) {
        return compasSclDataService.findSCLByUUID(type, uuid);
    }

    @GET
    @Path("/{" + UUID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}/scl")
    @Produces(MediaType.APPLICATION_XML)
    public SCL findRawSCLByUUIDAndVersion(@PathParam(TYPE_PATH_PARAM) SclType type,
                                          @PathParam(UUID_PATH_PARAM) UUID uuid,
                                          @PathParam(Constants.VERSION_PATH_PARAM) Version version) {
        return compasSclDataService.findSCLByUUID(type, uuid, version);
    }

    @PUT
    @Path("/{" + UUID_PATH_PARAM + "}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public void update(@PathParam(TYPE_PATH_PARAM) SclType type,
                       @PathParam(UUID_PATH_PARAM) UUID uuid,
                       UpdateRequest request) {
        compasSclDataService.update(type, uuid, request.getChangeSetType(), request.getScl());
    }

    @DELETE
    @Path("/{" + UUID_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_XML)
    public void deleteAll(@PathParam(TYPE_PATH_PARAM) SclType type,
                          @PathParam(UUID_PATH_PARAM) UUID uuid) {
        compasSclDataService.delete(type, uuid);
    }

    @DELETE
    @Path("/{" + UUID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_XML)
    public void deleteVersion(@PathParam(TYPE_PATH_PARAM) SclType type,
                              @PathParam(UUID_PATH_PARAM) UUID uuid,
                              @PathParam(Constants.VERSION_PATH_PARAM) Version version) {
        compasSclDataService.delete(type, uuid, version);
    }
}