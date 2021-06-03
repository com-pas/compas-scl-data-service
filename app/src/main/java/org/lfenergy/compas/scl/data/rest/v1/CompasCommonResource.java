// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.rest.model.Type;
import org.lfenergy.compas.scl.data.rest.model.TypeListResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/common/v1/")
public class CompasCommonResource {
    @GET
    @Path("/type/list")
    @Produces(MediaType.APPLICATION_XML)
    public TypeListResponse list() {
        var response = new TypeListResponse();
        for (var sclType : SclType.values()) {
            var responseType = new Type(sclType.name(), sclType.getDescription());
            response.getTypes().add(responseType);
        }
        return response;
    }
}
