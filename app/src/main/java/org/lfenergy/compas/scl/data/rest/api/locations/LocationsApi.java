package org.lfenergy.compas.scl.data.rest.api.locations;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import org.lfenergy.compas.scl.data.rest.api.locations.model.Location;

import java.util.List;
import java.util.UUID;

@Path("/api/locations")
public interface LocationsApi {
    @POST
    @Blocking
    @Path("/{locationId}/resources/{uuid}/assign")
    @Produces({ "application/json" })
    Uni<Void> assignResourceToLocation(@PathParam("locationId") UUID locationId, @PathParam("uuid") UUID uuid);

    @POST
    @Blocking
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    Uni<Location> createLocation(@Valid @NotNull Location location);

    @DELETE
    @Blocking
    @Path("/{locationId}")
    @Produces({ "application/json" })
    Uni<Void> deleteLocation(@PathParam("locationId") UUID locationId);

    @GET
    @Path("/{locationId}")
    @Produces({ "application/json" })
    Uni<Location> getLocation(@PathParam("locationId") UUID locationId);

    @GET
    @Produces({ "application/json" })
    Uni<List<Location>> getLocations(@QueryParam("page")   Integer page, @QueryParam("pageSize") @DefaultValue("25")   Integer pageSize);

    @POST
    @Blocking
    @Path("/{locationId}/resources/{uuid}/unassign")
    @Produces({ "application/json" })
    Uni<Void> unassignResourceFromLocation(@PathParam("locationId") UUID locationId,@PathParam("uuid") UUID uuid);

    @PUT
    @Path("/{locationId}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    Uni<Location> updateLocation(@PathParam("locationId") UUID locationId,@Valid @NotNull Location location);
}
