// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.entities.Location;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.repository.HistorizedSclFileRepository;
import org.lfenergy.compas.scl.data.repository.LocationRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationsServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private HistorizedSclFileRepository historizedSclFileRepository;

    // @Captor uses Mockito's own reflection to create the captor at runtime;
    // avoids ArgumentCaptor.forClass(Location.class) .class literal which
    // triggers eager superclass resolution and can break in multi-module builds.
    @Captor
    private ArgumentCaptor<Location> entityCaptor;

    private LocationsService locationsService;

    @BeforeEach
    void setUp() {
        locationsService = new LocationsService(locationRepository, historizedSclFileRepository);
    }

    // ---- helper methods ----------------------------------------------------

    private Location buildEntity(UUID id, String key, String name, String description, int assignedResources) {
        var entity = new Location();
        entity.id = id;
        entity.key = key;
        entity.name = name;
        entity.description = description;
        entity.assignedResources = assignedResources;
        return entity;
    }

    private org.lfenergy.compas.scl.data.rest.api.locations.Location buildDto(
            String key, String name, String description) {
        return new org.lfenergy.compas.scl.data.rest.api.locations.Location()
                .key(key).name(name).description(description);
    }

    // ---- createLocation ----------------------------------------------------

    @Test
    void createLocation_WhenNoDuplicate_ThenPersistsAndReturnsDto() {
        var dto = buildDto("LOC_A", "Location A", "desc");
        when(locationRepository.hasDuplicateValues("LOC_A", "Location A")).thenReturn(false);
        var generatedId = UUID.randomUUID();
        // Simulate @GeneratedValue: set entity.id when persist() is called.
        // entityCaptor.capture() resolves persist(Location) unambiguously at compile time;
        // reflection sets the id to avoid referencing Location.id as a field literal.
        doAnswer(invocation -> {
            try {
                Object entity = invocation.getArgument(0);
                entity.getClass().getDeclaredField("id").set(entity, generatedId);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            return null;
        }).when(locationRepository).persist(entityCaptor.capture());

        var result = locationsService.createLocation(dto);

        assertNotNull(result);
        assertEquals(generatedId.toString(), result.getUuid());
        assertEquals("LOC_A", result.getKey());
        assertEquals("Location A", result.getName());
        assertEquals("desc", result.getDescription());
    }

    @Test
    void createLocation_WhenDuplicateKeyOrName_ThenThrowsCompasSclDataServiceException() {
        var dto = buildDto("LOC_A", "Location A", "desc");
        when(locationRepository.hasDuplicateValues("LOC_A", "Location A")).thenReturn(true);

        assertThrows(CompasSclDataServiceException.class,
                () -> locationsService.createLocation(dto));
    }

    // ---- updateLocation ----------------------------------------------------

    @Test
    void updateLocation_WhenEntityExistsAndNoDuplicate_ThenUpdatesAndReturnsDto() {
        var id = UUID.randomUUID();
        var entity = buildEntity(id, "LOC_OLD", "Old Name", "old desc", 0);
        var dto = buildDto("LOC_NEW", "New Name", "new desc");

        when(locationRepository.findByIdOptional(id)).thenReturn(Optional.of(entity));
        when(locationRepository.hasDuplicateValuesExcluding("LOC_NEW", "New Name", id)).thenReturn(false);

        var result = locationsService.updateLocation(id, dto);

        assertNotNull(result);
        assertEquals("LOC_NEW", result.getKey());
        assertEquals("New Name", result.getName());
        assertEquals("new desc", result.getDescription());
    }

    @Test
    void updateLocation_WhenEntityNotFound_ThenThrowsCompasNoDataFoundException() {
        var id = UUID.randomUUID();
        var dto = buildDto("LOC_A", "Location A", "desc");
        when(locationRepository.findByIdOptional(id)).thenReturn(Optional.empty());

        assertThrows(CompasNoDataFoundException.class,
                () -> locationsService.updateLocation(id, dto));
    }

    @Test
    void updateLocation_WhenDuplicateKeyOrName_ThenThrowsCompasSclDataServiceException() {
        var id = UUID.randomUUID();
        var entity = buildEntity(id, "LOC_OLD", "Old Name", "desc", 0);
        var dto = buildDto("LOC_NEW", "New Name", "desc");

        when(locationRepository.findByIdOptional(id)).thenReturn(Optional.of(entity));
        when(locationRepository.hasDuplicateValuesExcluding("LOC_NEW", "New Name", id)).thenReturn(true);

        assertThrows(CompasSclDataServiceException.class,
                () -> locationsService.updateLocation(id, dto));
    }

    // ---- deleteLocation ----------------------------------------------------

    @Test
    void deleteLocation_WhenEntityExistsAndNoAssignedResources_ThenDeletes() {
        var id = UUID.randomUUID();
        var entity = buildEntity(id, "LOC_A", "Location A", "desc", 0);
        when(locationRepository.findByIdOptional(id)).thenReturn(Optional.of(entity));
        doNothing().when(locationRepository).delete(entity);

        locationsService.deleteLocation(id);

        verify(locationRepository).delete(entity);
    }

    @Test
    void deleteLocation_WhenEntityNotFound_ThenThrowsCompasNoDataFoundException() {
        var id = UUID.randomUUID();
        when(locationRepository.findByIdOptional(id)).thenReturn(Optional.empty());

        assertThrows(CompasNoDataFoundException.class,
                () -> locationsService.deleteLocation(id));
        verify(locationRepository, never()).delete(any());
    }

    @Test
    void deleteLocation_WhenHasAssignedResources_ThenThrowsCompasSclDataServiceException() {
        var id = UUID.randomUUID();
        var entity = buildEntity(id, "LOC_A", "Location A", "desc", 3);
        when(locationRepository.findByIdOptional(id)).thenReturn(Optional.of(entity));

        assertThrows(CompasSclDataServiceException.class,
                () -> locationsService.deleteLocation(id));
        verify(locationRepository, never()).delete(any());
    }

    // ---- getLocation -------------------------------------------------------

    @Test
    void getLocation_WhenEntityExists_ThenReturnsMappedDto() {
        var id = UUID.randomUUID();
        var entity = buildEntity(id, "LOC_A", "Location A", "desc", 2);
        when(locationRepository.findByIdOptional(id)).thenReturn(Optional.of(entity));

        var result = locationsService.getLocation(id);

        assertNotNull(result);
        assertEquals(id.toString(), result.getUuid());
        assertEquals("LOC_A", result.getKey());
        assertEquals("Location A", result.getName());
        assertEquals("desc", result.getDescription());
        assertEquals(2, result.getAssignedResources());
    }

    @Test
    void getLocation_WhenEntityNotFound_ThenThrowsCompasNoDataFoundException() {
        var id = UUID.randomUUID();
        when(locationRepository.findByIdOptional(id)).thenReturn(Optional.empty());

        assertThrows(CompasNoDataFoundException.class,
                () -> locationsService.getLocation(id));
    }

    // ---- getLocations ------------------------------------------------------

    @Test
    void getLocations_WhenPaginationProvided_ThenDelegatesToListPaged() {
        var entity = buildEntity(UUID.randomUUID(), "LOC_A", "Location A", "desc", 0);
        when(locationRepository.listPaged(0, 10)).thenReturn(List.of(entity));

        var result = locationsService.getLocations(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(locationRepository).listPaged(0, 10);
        verify(locationRepository, never()).listAll();
    }

    @Test
    void getLocations_WhenNoPaginationProvided_ThenDelegatesToListAll() {
        var entity = buildEntity(UUID.randomUUID(), "LOC_A", "Location A", "desc", 0);
        when(locationRepository.listAll()).thenReturn(List.of(entity));

        var result = locationsService.getLocations(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(locationRepository).listAll();
        verify(locationRepository, never()).listPaged(anyInt(), anyInt());
    }

    @Test
    void getLocations_WhenNoPagination_ThenAllEntitiesAreMapped() {
        var id1 = UUID.randomUUID();
        var id2 = UUID.randomUUID();
        when(locationRepository.listAll()).thenReturn(List.of(
                buildEntity(id1, "K1", "Name1", "desc1", 0),
                buildEntity(id2, "K2", "Name2", "desc2", 5)));

        var result = locationsService.getLocations(null, null);

        assertEquals(2, result.size());
        assertEquals("K1", result.get(0).getKey());
        assertEquals("K2", result.get(1).getKey());
        assertEquals(5, result.get(1).getAssignedResources());
    }

    // ---- assignResourceToLocation ------------------------------------------

    @Test
    void assignResourceToLocation_WhenBothExist_ThenAssignsAndRecalculates() {
        var locationId = UUID.randomUUID();
        var resourceId = UUID.randomUUID();
        var location = buildEntity(locationId, "LOC_A", "Location A", "desc", 0);

        when(locationRepository.findByIdOptional(locationId)).thenReturn(Optional.of(location));
        when(historizedSclFileRepository.countBySclFileId(resourceId)).thenReturn(1L);
        doNothing().when(historizedSclFileRepository).assignToLocation(resourceId, location);
        doNothing().when(locationRepository).recalculateAssignedResources(locationId);

        locationsService.assignResourceToLocation(locationId, resourceId);

        verify(historizedSclFileRepository).assignToLocation(resourceId, location);
        verify(locationRepository).recalculateAssignedResources(locationId);
    }

    @Test
    void assignResourceToLocation_WhenLocationNotFound_ThenThrowsCompasNoDataFoundException() {
        var locationId = UUID.randomUUID();
        var resourceId = UUID.randomUUID();
        when(locationRepository.findByIdOptional(locationId)).thenReturn(Optional.empty());

        assertThrows(CompasNoDataFoundException.class,
                () -> locationsService.assignResourceToLocation(locationId, resourceId));
        verify(historizedSclFileRepository, never()).assignToLocation(any(), any());
    }

    @Test
    void assignResourceToLocation_WhenResourceNotFound_ThenThrowsCompasNoDataFoundException() {
        var locationId = UUID.randomUUID();
        var resourceId = UUID.randomUUID();
        var location = buildEntity(locationId, "LOC_A", "Location A", "desc", 0);

        when(locationRepository.findByIdOptional(locationId)).thenReturn(Optional.of(location));
        when(historizedSclFileRepository.countBySclFileId(resourceId)).thenReturn(0L);

        assertThrows(CompasNoDataFoundException.class,
                () -> locationsService.assignResourceToLocation(locationId, resourceId));
        verify(historizedSclFileRepository, never()).assignToLocation(any(), any());
    }

    // ---- unassignResourceFromLocation --------------------------------------

    @Test
    void unassignResourceFromLocation_WhenBothExist_ThenUnassignsAndRecalculates() {
        var locationId = UUID.randomUUID();
        var resourceId = UUID.randomUUID();
        var location = buildEntity(locationId, "LOC_A", "Location A", "desc", 1);

        when(locationRepository.findByIdOptional(locationId)).thenReturn(Optional.of(location));
        when(historizedSclFileRepository.countBySclFileId(resourceId)).thenReturn(1L);
        doNothing().when(historizedSclFileRepository).unassignFromLocation(resourceId, locationId);
        doNothing().when(locationRepository).recalculateAssignedResources(locationId);

        locationsService.unassignResourceFromLocation(locationId, resourceId);

        verify(historizedSclFileRepository).unassignFromLocation(resourceId, locationId);
        verify(locationRepository).recalculateAssignedResources(locationId);
    }

    @Test
    void unassignResourceFromLocation_WhenLocationNotFound_ThenThrowsCompasNoDataFoundException() {
        var locationId = UUID.randomUUID();
        var resourceId = UUID.randomUUID();
        when(locationRepository.findByIdOptional(locationId)).thenReturn(Optional.empty());

        assertThrows(CompasNoDataFoundException.class,
                () -> locationsService.unassignResourceFromLocation(locationId, resourceId));
        verify(historizedSclFileRepository, never()).unassignFromLocation(any(), any());
    }

    @Test
    void unassignResourceFromLocation_WhenResourceNotFound_ThenThrowsCompasNoDataFoundException() {
        var locationId = UUID.randomUUID();
        var resourceId = UUID.randomUUID();
        when(locationRepository.findByIdOptional(locationId)).thenReturn(Optional.of(
                buildEntity(locationId, "LOC_A", "Location A", "desc", 1)));
        when(historizedSclFileRepository.countBySclFileId(resourceId)).thenReturn(0L);

        assertThrows(CompasNoDataFoundException.class,
                () -> locationsService.unassignResourceFromLocation(locationId, resourceId));
        verify(historizedSclFileRepository, never()).unassignFromLocation(any(), any());
    }
}
