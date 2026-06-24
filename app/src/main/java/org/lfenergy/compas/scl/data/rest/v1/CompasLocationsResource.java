// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lfenergy.compas.scl.data.rest.LocationsApi;
import org.lfenergy.compas.scl.data.rest.api.locations.Location;
import org.lfenergy.compas.scl.data.service.LocationsService;

import java.util.List;
import java.util.UUID;

@Blocking
@RequestScoped
public class CompasLocationsResource implements LocationsApi {

    private static final Logger LOGGER = LogManager.getLogger(CompasLocationsResource.class);

    private final LocationsService locationsService;

    @Inject
    public CompasLocationsResource(LocationsService locationsService) {
        this.locationsService = locationsService;
    }

    @Override
    public Uni<Void> assignResourceToLocation(UUID locationId, UUID uuid) {
        LOGGER.info("Assigning resource with id {} to location with id {}", uuid, locationId);
        return Uni.createFrom().voidItem().invoke(() -> locationsService.assignResourceToLocation(locationId, uuid));
    }

    @Override
    public Uni<Location> createLocation(Location location) {
        LOGGER.info("Creating new location with name {}", location.getName());
        return Uni.createFrom().item(() -> locationsService.createLocation(location));
    }

    @Override
    public Uni<Void> deleteLocation(UUID locationId) {
        LOGGER.info("Deleting location with id {}", locationId);
        return Uni.createFrom().voidItem().invoke(() -> locationsService.deleteLocation(locationId));
    }

    @Override
    public Uni<Location> getLocation(UUID locationId) {
        LOGGER.debug("Retrieving location with id {}", locationId);
        return Uni.createFrom().item(() -> locationsService.getLocation(locationId));
    }

    @Override
    public Uni<List<Location>> getLocations(Integer page, Integer pageSize) {
        LOGGER.debug("Retrieving locations with pagination - page: {}, pageSize: {}", page, pageSize);
        return Uni.createFrom().item(() -> locationsService.getLocations(page, pageSize));
    }

    @Override
    public Uni<Void> unassignResourceFromLocation(UUID locationId, UUID uuid) {
        LOGGER.info("Unassigning resource with id {} from location with id {}", uuid, locationId);
        return Uni.createFrom().voidItem().invoke(() -> locationsService.unassignResourceFromLocation(locationId, uuid));
    }

    @Override
    public Uni<Location> updateLocation(UUID locationId, Location location) {
        LOGGER.info("Updating location with id {}", locationId);
        return Uni.createFrom().item(() -> locationsService.updateLocation(locationId, location));
    }
}
