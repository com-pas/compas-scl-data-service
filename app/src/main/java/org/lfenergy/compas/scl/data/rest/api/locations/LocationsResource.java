package org.lfenergy.compas.scl.data.rest.api.locations;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.ILocationMetaItem;
import org.lfenergy.compas.scl.data.rest.api.locations.model.Location;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;

import java.util.List;
import java.util.UUID;

import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.LOCATION_DELETION_NOT_ALLOWED_ERROR_CODE;

@RequestScoped
public class LocationsResource implements LocationsApi {
    private static final Logger LOGGER = LogManager.getLogger(LocationsResource.class);

    private final CompasSclDataService compasSclDataService;

    @Inject
    public LocationsResource(CompasSclDataService compasSclDataService) {
        this.compasSclDataService = compasSclDataService;
    }

    @Override
    public Uni<Void> assignResourceToLocation(UUID locationId, UUID uuid) {
        compasSclDataService.assignResourceToLocation(locationId, uuid);
        return Uni.createFrom().nullItem();
    }

    @Override
    public Uni<Location> createLocation(Location location) {
        return Uni.createFrom()
            .item(() -> compasSclDataService.createLocation(
                location.getKey(),
                location.getName(),
                location.getDescription()
            ))
            .runSubscriptionOn(Infrastructure.getDefaultExecutor())
            .onItem()
            .transform(this::mapToLocation);
    }

    @Override
    public Uni<Void> deleteLocation(UUID locationId) {
        int assignedResourceCount = compasSclDataService.findLocationByUUID(locationId).getAssignedResources();
        if (assignedResourceCount > 0) {
            return Uni.createFrom().failure(new CompasSclDataServiceException(LOCATION_DELETION_NOT_ALLOWED_ERROR_CODE,
                String.format("Deletion of Location %s not allowed, unassign resources before deletion", locationId)));
        }
        compasSclDataService.deleteLocation(locationId);
        return Uni.createFrom().nullItem();
    }

    @Override
    public Uni<Location> getLocation(UUID locationId) {
        LOGGER.info("Retrieving location for ID: {}", locationId);
        return Uni.createFrom()
            .item(() -> compasSclDataService.findLocationByUUID(locationId))
            .runSubscriptionOn(Infrastructure.getDefaultExecutor())
            .onItem()
            .transform(this::mapToLocation);
    }

    @Override
    public Uni<List<Location>> getLocations(Integer page, Integer pageSize) {
        int pageLocation;
        if (page != null && page > 1) {
            pageLocation = page;
        } else {
            pageLocation = 0;
        }
        return Uni.createFrom()
            .item(() -> compasSclDataService.listLocations(pageLocation, pageSize))
            .runSubscriptionOn(Infrastructure.getDefaultExecutor())
            .onItem()
            .transform(list -> list.stream().map(this::mapToLocation).toList());
    }

    @Override
    public Uni<Void> unassignResourceFromLocation(UUID locationId, UUID uuid) {
        compasSclDataService.unassignResourceFromLocation(locationId, uuid);
        return Uni.createFrom().nullItem();
    }

    @Override
    public Uni<Location> updateLocation(UUID locationId, Location location) {
        return Uni.createFrom().
            item(() -> compasSclDataService.updateLocation(locationId, location.getKey(), location.getName(), location.getDescription()))
            .runSubscriptionOn(Infrastructure.getDefaultExecutor())
            .onItem()
            .transform(this::mapToLocation);
    }

    private Location mapToLocation(ILocationMetaItem location) {
        return new Location()
            .uuid(location.getId())
            .name(location.getName())
            .key(location.getKey())
            .description(location.getDescription())
            .assignedResources(location.getAssignedResources());
    }
}
