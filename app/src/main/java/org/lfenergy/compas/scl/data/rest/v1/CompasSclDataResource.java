// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.rest.UserInfoProperties;
import org.lfenergy.compas.scl.data.rest.v1.model.*;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

import static org.lfenergy.compas.scl.data.rest.Constants.*;

@Authenticated
@RequestScoped
@Path("/scl/v1/{" + TYPE_PATH_PARAM + "}")
public class CompasSclDataResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasSclDataResource.class);

    private final CompasSclDataService compasSclDataService;
    private final ElementConverter converter;

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    UserInfoProperties userInfoProperties;

    @Inject
    public CompasSclDataResource(CompasSclDataService compasSclDataService,
                                 ElementConverter converter) {
        this.compasSclDataService = compasSclDataService;
        this.converter = converter;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public CreateResponse create(@PathParam(TYPE_PATH_PARAM) SclType type,
                                 @Valid CreateRequest request) {
        String who = jsonWebToken.getClaim(userInfoProperties.who());
        LOGGER.trace("Username used for Who {}", who);

        var response = new CreateResponse();
        response.setId(compasSclDataService.create(type, request.getName(), who, request.getComment(), request.getElements().get(0)));
        return response;
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_XML)
    public ListResponse list(@PathParam(TYPE_PATH_PARAM) SclType type) {
        var response = new ListResponse();
        response.setItems(compasSclDataService.list(type));
        return response;
    }

    @GET
    @Path("/{" + ID_PATH_PARAM + "}/versions")
    @Produces(MediaType.APPLICATION_XML)
    public VersionsResponse listVersionsByUUID(@PathParam(TYPE_PATH_PARAM) SclType type,
                                               @PathParam(ID_PATH_PARAM) UUID id) {
        var response = new VersionsResponse();
        response.setItems(compasSclDataService.listVersionsByUUID(type, id));
        return response;
    }

    @GET
    @Path("/{" + ID_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_XML)
    public GetResponse findByUUID(@PathParam(TYPE_PATH_PARAM) SclType type,
                                  @PathParam(ID_PATH_PARAM) UUID id) {
        var response = new GetResponse();
        response.setScl(compasSclDataService.findByUUID(type, id));
        return response;
    }

    @GET
    @Path("/{" + ID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}")
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
    @Produces(MediaType.APPLICATION_XML)
    public String findRawSCLByUUID(@PathParam(TYPE_PATH_PARAM) SclType type,
                                   @PathParam(ID_PATH_PARAM) UUID id) {
        var scl = compasSclDataService.findByUUID(type, id);
        return converter.convertToString(scl, false);
    }

    @GET
    @Path("/{" + ID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}/scl")
    @Produces(MediaType.APPLICATION_XML)
    public String findRawSCLByUUIDAndVersion(@PathParam(TYPE_PATH_PARAM) SclType type,
                                             @PathParam(ID_PATH_PARAM) UUID id,
                                             @PathParam(VERSION_PATH_PARAM) Version version) {
        var scl = compasSclDataService.findByUUID(type, id, version);
        return converter.convertToString(scl, false);
    }

    @PUT
    @Path("/{" + ID_PATH_PARAM + "}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public void update(@PathParam(TYPE_PATH_PARAM) SclType type,
                       @PathParam(ID_PATH_PARAM) UUID id,
                       @Valid UpdateRequest request) {
        String who = jsonWebToken.getClaim(userInfoProperties.who());
        LOGGER.trace("Username used for Who {}", who);

        compasSclDataService.update(type, id, request.getChangeSetType(), who, request.getComment(), request.getElements().get(0));
    }

    @DELETE
    @Path("/{" + ID_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_XML)
    public void deleteAll(@PathParam(TYPE_PATH_PARAM) SclType type,
                          @PathParam(ID_PATH_PARAM) UUID id) {
        compasSclDataService.delete(type, id);
    }

    @DELETE
    @Path("/{" + ID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_XML)
    public void deleteVersion(@PathParam(TYPE_PATH_PARAM) SclType type,
                              @PathParam(ID_PATH_PARAM) UUID id,
                              @PathParam(VERSION_PATH_PARAM) Version version) {
        compasSclDataService.delete(type, id, version);
    }
}