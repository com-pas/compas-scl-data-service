// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.lfenergy.compas.scl.data.rest.UserInfoProperties;
import org.lfenergy.compas.scl.data.rest.v1.model.Type;
import org.lfenergy.compas.scl.data.rest.v1.model.TypeListResponse;
import org.lfenergy.compas.scl.data.rest.v1.model.UserInfoResponse;
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Comparator;

import static org.lfenergy.compas.scl.data.rest.Constants.READ_ROLE;

@Authenticated
@RequestScoped
@Path("/common/v1/")
public class CompasCommonResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasCommonResource.class);

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    UserInfoProperties userInfoProperties;

    @GET
    @Path("/type/list")
    @Produces(MediaType.APPLICATION_XML)
    public Uni<TypeListResponse> list(@HeaderParam("Authorization") String authHeader) {
        LOGGER.trace("Authorization Header '{}'", authHeader);

        // Retrieve the roles the logged-in user has.
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
    }

    @GET
    @Path("/userinfo")
    @Produces(MediaType.APPLICATION_XML)
    public Uni<UserInfoResponse> getUserInfo(@HeaderParam("Authorization") String authHeader) {
        LOGGER.trace("Authorization Header '{}'", authHeader);

        var response = new UserInfoResponse();
        response.setName(jsonWebToken.getClaim(userInfoProperties.name()));
        response.setSessionWarning(userInfoProperties.sessionWarning());
        response.setSessionExpires(userInfoProperties.sessionExpires());
        return Uni.createFrom().item(response);
    }
}
