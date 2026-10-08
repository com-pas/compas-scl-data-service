// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourceHistory;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourceSearch;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourcesResult;

import java.io.File;
import java.util.UUID;


@Path("/api/scl")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-04-24T08:07:06.426123+02:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public interface HistoryApi {

    @GET
    @Path("/{id}/version/{version}")
    @Produces({ "application/octet-stream", "application/json" })
    Uni<File> retrieveDataResourceByVersion(@PathParam("id") UUID id,@PathParam("version") String version);

    @GET
    @Path("/{id}/versions")
    @Produces({ "application/json" })
    Uni<DataResourceHistory> retrieveDataResourceHistory(@PathParam("id") UUID id);

    @POST
    @Path("/search")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    Uni<DataResourcesResult> searchForResources(@Valid @NotNull DataResourceSearch dataResourceSearch);
}
