package org.lfenergy.compas.scl.data.rest.api.archive;

import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import org.lfenergy.compas.scl.data.rest.api.archive.model.ArchivedResource;
import org.lfenergy.compas.scl.data.rest.api.archive.model.ArchivedResources;
import org.lfenergy.compas.scl.data.rest.api.archive.model.ArchivedResourcesSearch;

import java.io.File;
import java.util.UUID;


@Path("/api/archive")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2024-11-18T15:39:21.464141400+01:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public interface ArchivingApi {

    @POST
    @Path("/referenced-resource/{id}/versions/{version}")
    @Consumes({ "application/octet-stream" })
    @Produces({ "application/json" })
    Uni<ArchivedResource> archiveResource(@PathParam("id") UUID id, @PathParam("version") String version, @HeaderParam("X-author")   String xAuthor, @HeaderParam("X-approver")   String xApprover, @HeaderParam("Content-Type")   String contentType, @HeaderParam("X-filename")   String xFilename, @Valid File body);

    @POST
    @Path("/scl/{id}/versions/{version}")
    @Produces({ "application/json" })
    Uni<ArchivedResource> archiveSclResource(@PathParam("id") UUID id,@PathParam("version") String version);

    @POST
    @Path("/resources/search")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    Uni<ArchivedResources> searchArchivedResources(@Valid ArchivedResourcesSearch archivedResourcesSearch);
}
