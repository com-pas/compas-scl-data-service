// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.rest.v1.model.Type;
import org.lfenergy.compas.scl.data.rest.v1.model.TypeListResponse;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.lfenergy.compas.scl.data.rest.Constants.READ_ROLE;

@Path("/common/v1/")
public class CompasCommonResource {
    @GET
    @Path("/type/list")
    @RolesAllowed(READ_ROLE)
    @Produces(MediaType.APPLICATION_XML)
    public TypeListResponse list() {
        var response = new TypeListResponse();
        response.setTypes(
                Arrays.stream(SclType.values())
                        .map(sclType -> new Type(sclType.name(), sclType.getDescription()))
                        .collect(Collectors.toList()));
        return response;
    }
}
