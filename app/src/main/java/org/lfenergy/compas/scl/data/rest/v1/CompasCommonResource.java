// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import io.smallrye.common.annotation.Blocking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.lfenergy.compas.scl.data.rest.UserInfoProperties;
import org.lfenergy.compas.scl.data.rest.v1.model.Type;
import org.lfenergy.compas.scl.data.rest.v1.model.TypeListResponse;
import org.lfenergy.compas.scl.data.rest.v1.model.UserInfoResponse;
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.lfenergy.compas.scl.data.service.SclFileGroupService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Comparator;

import static org.lfenergy.compas.scl.data.rest.Constants.READ_ROLE;

@Authenticated
@RequestScoped
@Path("/common/v1/")
public class CompasCommonResource {
    private static final Logger LOGGER = LogManager.getLogger(CompasCommonResource.class);

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    UserInfoProperties userInfoProperties;

    @Inject
    SclFileGroupService sclFileGroupService;

    @GET
    @Path("/type/list")
    @Produces(MediaType.APPLICATION_XML)
    @Blocking
    public Uni<TypeListResponse> list() {
        LOGGER.info("Retrieving list of the types of SCL Files");

        var sclFileGroups = sclFileGroupService.listAll();

        // TODO: Filter for user rights
        var response = new TypeListResponse();
        response.setTypes(
                sclFileGroups.stream()
                        .map(sclFileGroup -> new Type(sclFileGroup.code, sclFileGroup.description))
                        .sorted(Comparator.comparing(Type::getDescription))
                        .toList());
        return Uni.createFrom().item(response);

        // Retrieve the roles the logged-in user has.
        /*
        var roles = jsonWebToken.getGroups();

        var response = new TypeListResponse();
        response.setTypes(
                Arrays.stream(SclFileType.values())
                        // Filter on the type the user has read rights.
                        .filter(sclFileType -> roles.contains(sclFileType.name() + "_" + READ_ROLE))
                        .map(sclFileType -> new Type(sclFileType.name(), sclFileType.getDescription()))
                        .sorted(Comparator.comparing(Type::getDescription))
                        .toList());
        return Uni.createFrom().item(response);
        */
    }

    @GET
    @Path("/userinfo")
    @Produces(MediaType.APPLICATION_XML)
    public Uni<UserInfoResponse> getUserInfo() {
        LOGGER.info("Retrieving user information about {}", jsonWebToken.getName());
        var response = new UserInfoResponse();
        response.setName(jsonWebToken.getClaim(userInfoProperties.name()));
        response.setSessionWarning(userInfoProperties.sessionWarning());
        response.setSessionExpires(userInfoProperties.sessionExpires());
        return Uni.createFrom().item(response);
    }
}
