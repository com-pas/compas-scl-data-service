// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.rest.api.locations.Location;

import java.util.List;
import java.util.UUID;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
public class LocationsService {

    private final CompasSclDataRepository repository;

    @Inject
    public LocationsService(CompasSclDataRepository repository) {
        this.repository = repository;
    }

    @Transactional(REQUIRED)
    public void assignResourceToLocation(UUID locationId, UUID uuid) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Transactional(REQUIRED)
    public Location createLocation(Location location) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Transactional(REQUIRED)
    public void deleteLocation(UUID locationId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Transactional(SUPPORTS)
    public Location getLocation(UUID locationId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Transactional(SUPPORTS)
    public List<Location> getLocations(Integer page, Integer pageSize) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Transactional(REQUIRED)
    public void unassignResourceFromLocation(UUID locationId, UUID uuid) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Transactional(REQUIRED)
    public Location updateLocation(UUID locationId, Location location) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
