// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.security.Authenticated;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.lfenergy.compas.scl.data.config.FeatureFlagService;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.rest.UserInfoProperties;
import org.lfenergy.compas.scl.data.rest.v1.model.*;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import java.util.UUID;

import static org.lfenergy.compas.scl.data.rest.Constants.*;

@Authenticated
@RequestScoped
@Path("/scl/v1/{" + TYPE_PATH_PARAM + "}")
public class CompasSclDataResource {
    private static final Logger LOGGER = LogManager.getLogger(CompasSclDataResource.class);

    private final CompasSclDataService compasSclDataService;

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    UserInfoProperties userInfoProperties;

    @Inject
    FeatureFlagService featureFlagService;

    @Inject
    public CompasSclDataResource(CompasSclDataService compasSclDataService) {
        this.compasSclDataService = compasSclDataService;
    }

    @POST
    @Blocking
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Uni<CreateResponse> create(@PathParam(TYPE_PATH_PARAM) SclFileType type,
                                      @Valid CreateRequest request) {
        LOGGER.info("Adding new SCL File for type {} to storage.", type);
        String who = jsonWebToken.getClaim(userInfoProperties.who());
        LOGGER.trace("Username used for Who {}", who);

        var response = new CreateResponse();
        response.setSclData(compasSclDataService.create(
                type, request.getName(), who, request.getComment(), request.getSclData(), featureFlagService.isHistoryEnabled()
        ));
        return Uni.createFrom().item(response);
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_XML)
    public Uni<ListResponse> list(@PathParam(TYPE_PATH_PARAM) SclFileType type) {
        LOGGER.info("Listing SCL Files for type {} from storage.", type);
        var response = new ListResponse();
        response.setItems(compasSclDataService.list(type));
        return Uni.createFrom().item(response);
    }

    @GET
    @Path("/{" + ID_PATH_PARAM + "}/versions")
    @Produces(MediaType.APPLICATION_XML)
    public Uni<VersionsResponse> listVersionsByUUID(@PathParam(TYPE_PATH_PARAM) SclFileType type,
                                                    @PathParam(ID_PATH_PARAM) UUID id) {
        LOGGER.info("Listing versions of SCL File {} for type {} from storage.", id, type);
        var response = new VersionsResponse();
        response.setItems(compasSclDataService.listVersionsByUUID(type, id));
        return Uni.createFrom().item(response);
    }

    @GET
    @Path("/{" + ID_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_XML)
    public Uni<GetResponse> findByUUID(@PathParam(TYPE_PATH_PARAM) SclFileType type,
                                       @PathParam(ID_PATH_PARAM) UUID id) {
        LOGGER.info("Retrieving latest version of SCL File {} for type {} from storage.", id, type);
        var response = new GetResponse();
        response.setSclData(compasSclDataService.findByUUID(type, id));
        return Uni.createFrom().item(response);
    }

    @GET
    @Path("/{" + ID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_XML)
    public Uni<GetResponse> findByUUIDAndVersion(@PathParam(TYPE_PATH_PARAM) SclFileType type,
                                                 @PathParam(ID_PATH_PARAM) UUID id,
                                                 @PathParam(VERSION_PATH_PARAM) Version version) {
        LOGGER.info("Retrieving version {} of SCL File {} for type {} from storage.", version, id, type);
        var response = new GetResponse();
        response.setSclData(compasSclDataService.findByUUID(type, id, version));
        return Uni.createFrom().item(response);
    }

    @PUT
    @Blocking
    @Path("/{" + ID_PATH_PARAM + "}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Uni<UpdateResponse> update(@PathParam(TYPE_PATH_PARAM) SclFileType type,
                                      @PathParam(ID_PATH_PARAM) UUID id,
                                      @Valid UpdateRequest request) {
        LOGGER.info("Updating SCL File {} for type {} to storage.", id, type);
        String who = jsonWebToken.getClaim(userInfoProperties.who());
        LOGGER.trace("Username used for Who {}", who);

        var response = new UpdateResponse();
        response.setSclData(compasSclDataService.update(type, id, request.getChangeSetType(), who, request.getComment(),
                request.getSclData(), featureFlagService.isHistoryEnabled()));
        return Uni.createFrom().item(response);
    }

    @DELETE
    @Blocking
    @Path("/{" + ID_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_XML)
    public Uni<Void> deleteAll(@PathParam(TYPE_PATH_PARAM) SclFileType type,
                               @PathParam(ID_PATH_PARAM) UUID id) {
        LOGGER.info("Removing all versions of SCL File {} for type {} from storage.", id, type);
        compasSclDataService.delete(type, id, featureFlagService.keepDeletedFiles());
        return Uni.createFrom().nullItem();
    }

    @DELETE
    @Blocking
    @Path("/{" + ID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}")
    @Produces(MediaType.APPLICATION_XML)
    public Uni<Void> deleteVersion(@PathParam(TYPE_PATH_PARAM) SclFileType type,
                                   @PathParam(ID_PATH_PARAM) UUID id,
                                   @PathParam(VERSION_PATH_PARAM) Version version) {
        LOGGER.info("Removing version {} of SCL File {} for type {} from storage.", version, id, type);
        compasSclDataService.deleteVersion(type, id, version, featureFlagService.keepDeletedFiles());
        return Uni.createFrom().nullItem();
    }

    @POST
    @Path("/checkname")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Uni<DuplicateNameCheckResponse> checkDuplicateName(@PathParam(TYPE_PATH_PARAM) SclFileType type,
                                                              @Valid DuplicateNameCheckRequest request) {
        LOGGER.info("Checking for duplicate SCL File name.");

        var response = new DuplicateNameCheckResponse();
        response.setDuplicate(compasSclDataService.hasDuplicateSclName(type, request.getName()));
        return Uni.createFrom().item(response);
    }
}
