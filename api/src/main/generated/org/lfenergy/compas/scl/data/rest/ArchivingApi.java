// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResource;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResources;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResourcesHistory;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResourcesSearch;

import java.io.File;
import java.util.UUID;


@Path("/api/archive")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2024-12-06T09:13:22.882514600+01:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public interface ArchivingApi {

    @POST
    @Path("/referenced-resource/{id}/versions/{version}")
    @Produces({ "application/json" })
    Uni<ArchivedResource> archiveResource(@PathParam("id") UUID id, @PathParam("version") String version, @HeaderParam("X-author")   String xAuthor, @HeaderParam("X-approver")   String xApprover, @HeaderParam("Content-Type")   String contentType, @HeaderParam("X-filename")   String xFilename, @Valid File body);

    @POST
    @Path("/scl/{id}/versions/{version}")
    @Produces({ "application/json" })
    Uni<ArchivedResource> archiveSclResource(@PathParam("id") UUID id, @PathParam("version") String version);

    @GET
    @Path("/resources/{id}/versions")
    @Produces({ "application/json" })
    Uni<ArchivedResourcesHistory> retrieveArchivedResourceHistory(@PathParam("id") UUID id);

    @POST
    @Path("/resources/search")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    Uni<ArchivedResources> searchArchivedResources(@Valid ArchivedResourcesSearch archivedResourcesSearch);
}
