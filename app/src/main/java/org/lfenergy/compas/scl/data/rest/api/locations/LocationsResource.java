package org.lfenergy.compas.scl.data.rest.api.locations;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.lfenergy.compas.scl.data.model.ILocationMetaItem;
import org.lfenergy.compas.scl.data.rest.UserInfoProperties;
import org.lfenergy.compas.scl.data.rest.api.locations.model.Location;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequestScoped
public class LocationsResource implements LocationsApi {
    private static final Logger LOGGER = LogManager.getLogger(LocationsResource.class);

    private final CompasSclDataService compasSclDataService;
    private final JsonWebToken jsonWebToken;
    private final UserInfoProperties userInfoProperties;

    @Inject
    public LocationsResource(CompasSclDataService compasSclDataService, JsonWebToken jsonWebToken, UserInfoProperties userInfoProperties) {
        this.compasSclDataService = compasSclDataService;
        this.jsonWebToken = jsonWebToken;
        this.userInfoProperties = userInfoProperties;
    }

    @Override
    public Uni<Void> assignResourceToLocation(UUID locationId, UUID uuid) {
        LOGGER.info("Assigning resource '{}' to location '{}'", uuid, locationId);
        compasSclDataService.assignResourceToLocation(locationId, uuid);
        return Uni.createFrom().nullItem();
    }

    @Override
    public Uni<Location> createLocation(Location location) {
        LOGGER.info("Creating location '{}'", location.getName());
        return Uni.createFrom()
            .item(() -> compasSclDataService.createLocation(
                location.getKey(),
                location.getName(),
                location.getDescription(),
                jsonWebToken.getClaim(userInfoProperties.name())
            ))
            .runSubscriptionOn(Infrastructure.getDefaultExecutor())
            .onItem()
            .transform(this::mapToLocation);
    }

    @Override
    public Uni<Void> deleteLocation(UUID locationId) {
        LOGGER.info("Deleting location with ID '{}'", locationId);
        compasSclDataService.deleteLocation(locationId);
        return Uni.createFrom().nullItem();
    }

    @Override
    public Uni<Location> getLocation(UUID locationId) {
        LOGGER.info("Retrieving location for ID '{}'", locationId);
        return Uni.createFrom()
            .item(() -> compasSclDataService.findLocationByUUID(locationId))
            .runSubscriptionOn(Infrastructure.getDefaultExecutor())
            .onItem()
            .transform(this::mapToLocation);
    }

    @Override
    public Uni<List<Location>> getLocations(Integer page, Integer pageSize) {
        int pageLocation = Objects.requireNonNullElse(page, 0);
        LOGGER.info("Retrieving locations for page '{}' and pageSize '{}'", pageLocation, pageSize);
        return Uni.createFrom()
            .item(() -> compasSclDataService.listLocations(pageLocation, pageSize))
            .runSubscriptionOn(Infrastructure.getDefaultExecutor())
            .onItem()
            .transform(list -> list.stream().map(this::mapToLocation).toList());
    }

    @Override
    public Uni<Void> unassignResourceFromLocation(UUID locationId, UUID uuid) {
        LOGGER.info("Unassigning resource '{}' from location '{}'", uuid, locationId);
        compasSclDataService.unassignResourceFromLocation(locationId, uuid);
        return Uni.createFrom().nullItem();
    }

    @Override
    public Uni<Location> updateLocation(UUID locationId, Location location) {
        LOGGER.info("Updating resource '{}'", locationId);
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
