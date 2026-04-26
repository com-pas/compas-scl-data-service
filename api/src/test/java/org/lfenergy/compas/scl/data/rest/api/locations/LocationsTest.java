// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.locations;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocationsTest {

    private Location createLocation() {
        return new Location()
                .uuid("uuid-1")
                .key("LOC_KEY")
                .name("NAME")
                .assignedResources(3);
    }

    private Pagination createPagination() {
        return new Pagination().page(0).pageSize(20);
    }

    @Test
    void shouldSetAndGetProperties() {
        Locations locations = new Locations();
        List<Location> list = List.of(createLocation());
        Pagination pagination = createPagination();

        locations.setLocations(list);
        locations.setPagination(pagination);

        assertEquals(list, locations.getLocations());
        assertEquals(pagination, locations.getPagination());
    }

    @Test
    void shouldSupportFluentSetters() {
        Location location = createLocation();
        Pagination pagination = createPagination();

        Locations locations = new Locations()
                .locations(new ArrayList<>())
                .addLocationsItem(location)
                .pagination(pagination);

        assertEquals(1, locations.getLocations().size());
        assertEquals(location, locations.getLocations().get(0));
        assertEquals(pagination, locations.getPagination());
    }

    @Test
    void addLocationsItemShouldInitializeListIfNull() {
        Locations locations = new Locations();
        locations.setLocations(null);

        Location location = createLocation();
        locations.addLocationsItem(location);

        assertNotNull(locations.getLocations());
        assertEquals(1, locations.getLocations().size());
        assertEquals(location, locations.getLocations().get(0));
    }

    @Test
    void removeLocationsItemShouldRemoveItem() {
        Location location = createLocation();

        Locations locations = new Locations()
                .addLocationsItem(location);

        locations.removeLocationsItem(location);

        assertTrue(locations.getLocations().isEmpty());
    }

    @Test
    void removeLocationsItemShouldHandleNullsGracefully() {
        Locations locations = new Locations();
        locations.setLocations(null);

        assertDoesNotThrow(() -> locations.removeLocationsItem(null));
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        Location location = createLocation();
        Pagination pagination = createPagination();

        Locations a = new Locations().addLocationsItem(location).pagination(pagination);
        Locations b = new Locations().addLocationsItem(location).pagination(pagination);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        Locations locations = new Locations()
                .addLocationsItem(createLocation())
                .pagination(createPagination());

        assertNotEquals(null, locations);
        assertNotEquals(new Object(), locations);
    }

    @Test
    void toStringShouldContainFields() {
        Locations locations = new Locations()
                .addLocationsItem(createLocation())
                .pagination(createPagination());
        String result = locations.toString();

        assertNotNull(result);
        assertTrue(result.contains("class Locations"));
        assertTrue(result.contains("locations"));
        assertTrue(result.contains("pagination"));
    }
}
