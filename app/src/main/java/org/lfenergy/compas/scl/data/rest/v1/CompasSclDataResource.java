// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import org.jboss.logging.Logger;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.rest.v1.model.*;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static org.lfenergy.compas.scl.data.rest.Constants.*;

@Path("/scl/v1/{" + TYPE_PATH_PARAM + "}")
public class CompasSclDataResource {
    private static final Logger LOG = Logger.getLogger(CompasSclDataResource.class);

    private CompasSclDataService compasSclDataService;

    private ElementConverter converter = new ElementConverter();

    @Inject
    public CompasSclDataResource(CompasSclDataService compasSclDataService) {
        this.compasSclDataService = compasSclDataService;
    }

    @POST
    @RolesAllowed("Create")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public CreateResponse create(@PathParam(TYPE_PATH_PARAM) SclType type,
                                 @Valid CreateRequest request) {
        var response = new CreateResponse();
        response.setId(compasSclDataService.create(type, request.getName(), request.getElements().get(0)));
        return response;
    }

    @GET
    @Path("/list")
    @RolesAllowed("Read")
    @Produces(MediaType.APPLICATION_XML)
    public ListResponse list(@PathParam(TYPE_PATH_PARAM) SclType type) {
        var response = new ListResponse();
        response.setItems(compasSclDataService.list(type));
        return response;
    }

    @GET
    @Path("/{" + ID_PATH_PARAM + "}/versions")
    @RolesAllowed("Read")
    @Produces(MediaType.APPLICATION_XML)
    public VersionsResponse listVersionsByUUID(@PathParam(TYPE_PATH_PARAM) SclType type,
                                               @PathParam(ID_PATH_PARAM) UUID id) {
        var response = new VersionsResponse();
        response.setItems(compasSclDataService.listVersionsByUUID(type, id));
        return response;
    }

    @GET
    @Path("/{" + ID_PATH_PARAM + "}")
    @RolesAllowed("Read")
    @Produces(MediaType.APPLICATION_XML)
    public GetResponse findByUUID(@PathParam(TYPE_PATH_PARAM) SclType type,
                                  @PathParam(ID_PATH_PARAM) UUID id) {
        var response = new GetResponse();
        response.setScl(compasSclDataService.findByUUID(type, id));
        return response;
    }

    @GET
    @Path("/{" + ID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}")
    @RolesAllowed("Read")
    @Produces(MediaType.APPLICATION_XML)
    public GetResponse findByUUIDAndVersion(@PathParam(TYPE_PATH_PARAM) SclType type,
                                            @PathParam(ID_PATH_PARAM) UUID id,
                                            @PathParam(VERSION_PATH_PARAM) Version version) {
        var response = new GetResponse();
        response.setScl(compasSclDataService.findByUUID(type, id, version));
        return response;
    }

    @GET
    @Path("/{" + ID_PATH_PARAM + "}/scl")
    @RolesAllowed("Read")
    @Produces(MediaType.APPLICATION_XML)
    public String findRawSCLByUUID(@PathParam(TYPE_PATH_PARAM) SclType type,
                                   @PathParam(ID_PATH_PARAM) UUID id) {
        var scl = compasSclDataService.findByUUID(type, id);
        return converter.convertToString(scl, false);
    }

    @GET
    @Path("/{" + ID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}/scl")
    @RolesAllowed("Read")
    @Produces(MediaType.APPLICATION_XML)
    public String findRawSCLByUUIDAndVersion(@PathParam(TYPE_PATH_PARAM) SclType type,
                                             @PathParam(ID_PATH_PARAM) UUID id,
                                             @PathParam(VERSION_PATH_PARAM) Version version) {
        var scl = compasSclDataService.findByUUID(type, id, version);
        return converter.convertToString(scl, false);
    }

    @PUT
    @Path("/{" + ID_PATH_PARAM + "}")
    @RolesAllowed("Update")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public void update(@PathParam(TYPE_PATH_PARAM) SclType type,
                       @PathParam(ID_PATH_PARAM) UUID id,
                       @Valid UpdateRequest request) {
        compasSclDataService.update(type, id, request.getChangeSetType(), request.getElements().get(0));
    }

    @DELETE
    @Path("/{" + ID_PATH_PARAM + "}")
    @RolesAllowed("Delete")
    @Produces(MediaType.APPLICATION_XML)
    public void deleteAll(@PathParam(TYPE_PATH_PARAM) SclType type,
                          @PathParam(ID_PATH_PARAM) UUID id) {
        compasSclDataService.delete(type, id);
    }

    @DELETE
    @Path("/{" + ID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}")
    @RolesAllowed("Delete")
    @Produces(MediaType.APPLICATION_XML)
    public void deleteVersion(@PathParam(TYPE_PATH_PARAM) SclType type,
                              @PathParam(ID_PATH_PARAM) UUID id,
                              @PathParam(VERSION_PATH_PARAM) Version version) {
        compasSclDataService.delete(type, id, version);
    }
}