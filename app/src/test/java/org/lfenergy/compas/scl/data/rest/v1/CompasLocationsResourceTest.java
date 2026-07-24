// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.rest.api.locations.Location;
import org.lfenergy.compas.scl.data.service.LocationsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasLocationsResourceTest {

    @Mock
    private LocationsService locationsService;

    @InjectMocks
    private CompasLocationsResource resource;

    private Location buildLocation(String key, String name) {
        return new Location().key(key).name(name).description("desc");
    }

    // ---- createLocation ----------------------------------------------------

    @Test
    void createLocation_WhenCalled_ThenDelegatesToServiceAndReturnsUni() {
        var dto = buildLocation("LOC_A", "Location A");
        var expected = buildLocation("LOC_A", "Location A").uuid(UUID.randomUUID().toString());
        when(locationsService.createLocation(dto)).thenReturn(expected);

        var uni = resource.createLocation(dto);

        assertNotNull(uni);
        assertSame(expected, uni.await().indefinitely());
        verify(locationsService).createLocation(dto);
    }

    @Test
    void createLocation_WhenServiceThrowsException_ThenUniPropagatesException() {
        var dto = buildLocation("LOC_A", "Location A");
        when(locationsService.createLocation(dto)).thenThrow(new RuntimeException("duplicate"));

        var uni = resource.createLocation(dto);

        assertThrows(RuntimeException.class, () -> uni.await().indefinitely());
    }

    // ---- updateLocation ----------------------------------------------------

    @Test
    void updateLocation_WhenCalled_ThenDelegatesToServiceAndReturnsUni() {
        var id = UUID.randomUUID();
        var dto = buildLocation("LOC_NEW", "New Name");
        var expected = buildLocation("LOC_NEW", "New Name").uuid(id.toString());
        when(locationsService.updateLocation(id, dto)).thenReturn(expected);

        var uni = resource.updateLocation(id, dto);

        assertNotNull(uni);
        assertSame(expected, uni.await().indefinitely());
        verify(locationsService).updateLocation(id, dto);
    }

    // ---- deleteLocation ----------------------------------------------------

    @Test
    void deleteLocation_WhenCalled_ThenDelegatesToServiceAndReturnsVoidUni() {
        var id = UUID.randomUUID();
        doNothing().when(locationsService).deleteLocation(id);

        var uni = resource.deleteLocation(id);

        assertNotNull(uni);
        assertDoesNotThrow(() -> uni.await().indefinitely());
        verify(locationsService).deleteLocation(id);
    }

    @Test
    void deleteLocation_WhenServiceThrowsException_ThenUniPropagatesException() {
        var id = UUID.randomUUID();
        doThrow(new RuntimeException("has resources")).when(locationsService).deleteLocation(id);

        var uni = resource.deleteLocation(id);

        assertThrows(RuntimeException.class, () -> uni.await().indefinitely());
    }

    // ---- getLocation -------------------------------------------------------

    @Test
    void getLocation_WhenCalled_ThenDelegatesToServiceAndReturnsUni() {
        var id = UUID.randomUUID();
        var expected = buildLocation("LOC_A", "Location A").uuid(id.toString());
        when(locationsService.getLocation(id)).thenReturn(expected);

        var uni = resource.getLocation(id);

        assertNotNull(uni);
        assertSame(expected, uni.await().indefinitely());
        verify(locationsService).getLocation(id);
    }

    @Test
    void getLocation_WhenServiceThrowsException_ThenUniPropagatesException() {
        var id = UUID.randomUUID();
        when(locationsService.getLocation(id)).thenThrow(new RuntimeException("not found"));

        var uni = resource.getLocation(id);

        assertThrows(RuntimeException.class, () -> uni.await().indefinitely());
    }

    // ---- getLocations ------------------------------------------------------

    @Test
    void getLocations_WhenCalledWithPagination_ThenDelegatesToServiceAndReturnsUni() {
        var loc1 = buildLocation("K1", "Name1");
        var loc2 = buildLocation("K2", "Name2");
        when(locationsService.getLocations(0, 10)).thenReturn(List.of(loc1, loc2));

        var uni = resource.getLocations(0, 10);

        assertNotNull(uni);
        var result = uni.await().indefinitely();
        assertEquals(2, result.size());
        verify(locationsService).getLocations(0, 10);
    }

    @Test
    void getLocations_WhenCalledWithoutPagination_ThenDelegatesToServiceAndReturnsUni() {
        when(locationsService.getLocations(null, null)).thenReturn(List.of());

        var uni = resource.getLocations(null, null);

        assertNotNull(uni);
        assertTrue(uni.await().indefinitely().isEmpty());
        verify(locationsService).getLocations(null, null);
    }

    // ---- assignResourceToLocation ------------------------------------------

    @Test
    void assignResourceToLocation_WhenCalled_ThenDelegatesToServiceAndReturnsVoidUni() {
        var locationId = UUID.randomUUID();
        var resourceId = UUID.randomUUID();
        doNothing().when(locationsService).assignResourceToLocation(locationId, resourceId);

        var uni = resource.assignResourceToLocation(locationId, resourceId);

        assertNotNull(uni);
        assertDoesNotThrow(() -> uni.await().indefinitely());
        verify(locationsService).assignResourceToLocation(locationId, resourceId);
    }

    @Test
    void assignResourceToLocation_WhenServiceThrowsException_ThenUniPropagatesException() {
        var locationId = UUID.randomUUID();
        var resourceId = UUID.randomUUID();
        doThrow(new RuntimeException("resource not found"))
                .when(locationsService).assignResourceToLocation(locationId, resourceId);

        var uni = resource.assignResourceToLocation(locationId, resourceId);

        assertThrows(RuntimeException.class, () -> uni.await().indefinitely());
    }

    // ---- unassignResourceFromLocation --------------------------------------

    @Test
    void unassignResourceFromLocation_WhenCalled_ThenDelegatesToServiceAndReturnsVoidUni() {
        var locationId = UUID.randomUUID();
        var resourceId = UUID.randomUUID();
        doNothing().when(locationsService).unassignResourceFromLocation(locationId, resourceId);

        var uni = resource.unassignResourceFromLocation(locationId, resourceId);

        assertNotNull(uni);
        assertDoesNotThrow(() -> uni.await().indefinitely());
        verify(locationsService).unassignResourceFromLocation(locationId, resourceId);
    }

    @Test
    void unassignResourceFromLocation_WhenServiceThrowsException_ThenUniPropagatesException() {
        var locationId = UUID.randomUUID();
        var resourceId = UUID.randomUUID();
        doThrow(new RuntimeException("not assigned"))
                .when(locationsService).unassignResourceFromLocation(locationId, resourceId);

        var uni = resource.unassignResourceFromLocation(locationId, resourceId);

        assertThrows(RuntimeException.class, () -> uni.await().indefinitely());
    }
}
