// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.repository.LocationRepository;
import org.lfenergy.compas.scl.data.repository.HistorizedSclFileRepository;
import org.lfenergy.compas.scl.data.rest.api.locations.Location;

import java.util.List;
import java.util.UUID;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.CREATION_ERROR_CODE;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.INVALID_INPUT_ERROR_CODE;

@ApplicationScoped
public class LocationsService {

    private static final String ERR_MSG_LOCATION_NOT_FOUND = "Location not found: ";

    private final LocationRepository locationRepository;
    private final HistorizedSclFileRepository historizedSclFileRepository;

    @Inject
    public LocationsService(LocationRepository locationRepository,
                            HistorizedSclFileRepository historizedSclFileRepository) {
        this.locationRepository = locationRepository;
        this.historizedSclFileRepository = historizedSclFileRepository;
    }

    @Transactional(REQUIRED)
    public Location createLocation(Location locationDto) {
        if (locationRepository.hasDuplicateValues(locationDto.getKey(), locationDto.getName())) {
            throw new CompasSclDataServiceException(CREATION_ERROR_CODE,
                "Duplicate location key or name: " + locationDto.getKey());
        }
        var entity = new org.lfenergy.compas.scl.data.entities.Location();
        entity.key = locationDto.getKey();
        entity.name = locationDto.getName();
        entity.description = locationDto.getDescription();
        locationRepository.persist(entity);
        return toDto(entity);
    }

    @Transactional(REQUIRED)
    public Location updateLocation(UUID locationId, Location locationDto) {
        var entity = locationRepository.findByIdOptional(locationId)
                .orElseThrow(() -> new CompasNoDataFoundException(
                        ERR_MSG_LOCATION_NOT_FOUND + locationId));
        if (locationRepository.hasDuplicateValuesExcluding(
                locationDto.getKey(), locationDto.getName(), locationId)) {
            throw new CompasSclDataServiceException(CREATION_ERROR_CODE,
                    "Duplicate location key or name");
        }
        entity.key = locationDto.getKey();
        entity.name = locationDto.getName();
        entity.description = locationDto.getDescription();
        locationRepository.persist(entity);
        return toDto(entity);
    }

    @Transactional(REQUIRED)
    public void deleteLocation(UUID locationId) {
        var entity = locationRepository.findByIdOptional(locationId)
            .orElseThrow(() -> new CompasNoDataFoundException(
                ERR_MSG_LOCATION_NOT_FOUND + locationId));
        if (entity.assignedResources > 0) {
            throw new CompasSclDataServiceException(INVALID_INPUT_ERROR_CODE,
                "Deletion not allowed, location has " + entity.assignedResources + " assigned resources: " + locationId);
        }
        locationRepository.delete(entity);
    }

    @Transactional(SUPPORTS)
    public Location getLocation(UUID locationId) {
        var entity = locationRepository.findByIdOptional(locationId)
            .orElseThrow(() -> new CompasNoDataFoundException(
                ERR_MSG_LOCATION_NOT_FOUND + locationId));
        return toDto(entity);
    }

    @Transactional(SUPPORTS)
    public List<Location> getLocations(Integer page, Integer pageSize) {
        List<org.lfenergy.compas.scl.data.entities.Location> entities;
        if (page != null && pageSize != null) {
            entities = locationRepository.listPaged(page, pageSize);
        } else {
            entities = locationRepository.listAll();
        }
        return entities.stream().map(this::toDto).toList();
    }

    @Transactional(REQUIRED)
    public void assignResourceToLocation(UUID locationId, UUID uuid) {
        var locationEntity = locationRepository.findByIdOptional(locationId)
                .orElseThrow(() -> new CompasNoDataFoundException(
                    ERR_MSG_LOCATION_NOT_FOUND + locationId));
        if (historizedSclFileRepository.countBySclFileId(uuid) == 0) {
            throw new CompasNoDataFoundException(
                    "Resource not found: " + uuid);
        }
        historizedSclFileRepository.assignToLocation(uuid, locationEntity);
        locationRepository.recalculateAssignedResources(locationId);
    }

    @Transactional(REQUIRED)
    public void unassignResourceFromLocation(UUID locationId, UUID uuid) {
        if (locationRepository.findByIdOptional(locationId).isEmpty()) {
            throw new CompasNoDataFoundException(ERR_MSG_LOCATION_NOT_FOUND + locationId);
        }
        if (historizedSclFileRepository.countBySclFileId(uuid) == 0) {
            throw new CompasNoDataFoundException(
                "Resource not found: " + uuid);
        }
        historizedSclFileRepository.unassignFromLocation(uuid, locationId);
        locationRepository.recalculateAssignedResources(locationId);
    }

    private Location toDto(org.lfenergy.compas.scl.data.entities.Location entity) {
        return new Location()
            .uuid(entity.id.toString())
            .key(entity.key)
            .name(entity.name)
            .description(entity.description)
            .assignedResources(entity.assignedResources);
    }
}
