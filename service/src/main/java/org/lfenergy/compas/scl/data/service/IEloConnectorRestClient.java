package org.lfenergy.compas.scl.data.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.lfenergy.compas.scl.data.dto.ResourceData;
import org.lfenergy.compas.scl.data.dto.ResourceMetaData;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
//@OidcClientFilter("jwt-secret")
@RegisterRestClient(configKey = "elo-connector-client")
public interface IEloConnectorRestClient {
    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Path("/archiving")
    Uni<ResourceMetaData> createArchivedResource(@Valid @NotNull ResourceData resourceData);


    @GET
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Path("/resources/projects/{project}")
    Uni<List<ResourceMetaData>> retrieveAllProjectResources(@PathParam("project") UUID project);

    @GET
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @Path("/resources/{resourceUuid}")
    Uni<ResourceData> retrieveArchivedResource(@PathParam("resourceUuid") String resourceUuid);
}
