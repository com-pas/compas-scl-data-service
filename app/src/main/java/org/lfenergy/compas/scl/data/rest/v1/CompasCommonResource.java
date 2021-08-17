// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.rest.v1.model.Type;
import org.lfenergy.compas.scl.data.rest.v1.model.TypeListResponse;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

import static org.lfenergy.compas.scl.data.rest.Constants.READ_ROLE;

@Authenticated
@RequestScoped
@Path("/common/v1/")
public class CompasCommonResource {
    @Inject
    SecurityIdentity securityIdentity;

    @GET
    @Path("/type/list")
    @Produces(MediaType.APPLICATION_XML)
    public TypeListResponse list() {
        // Retrieve the roles the loggedin user has.
        var roles = securityIdentity.getRoles();

        var response = new TypeListResponse();
        response.setTypes(
                Arrays.stream(SclType.values())
                        // Filter on the type the user has read rights.
                        .filter(sclType -> roles.contains(sclType.name() + "_" + READ_ROLE))
                        .map(sclType -> new Type(sclType.name(), sclType.getDescription()))
                        .sorted(Comparator.comparing(Type::getDescription))
                        .collect(Collectors.toList()));
        return response;
    }
}
