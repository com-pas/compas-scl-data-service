// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.locations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    private Location createSample() {
        return new Location()
                .uuid("uuid-1")
                .key("LOC_KEY")
                .name("NAME")
                .description("DESCRIPTION")
                .assignedResources(5);
    }

    @Test
    void shouldSetAndGetProperties() {
        Location location = new Location();

        location.setUuid("uuid-1");
        location.setKey("LOC_KEY");
        location.setName("NAME");
        location.setDescription("DESCRIPTION");
        location.setAssignedResources(5);

        assertEquals("uuid-1", location.getUuid());
        assertEquals("LOC_KEY", location.getKey());
        assertEquals("NAME", location.getName());
        assertEquals("DESCRIPTION", location.getDescription());
        assertEquals(5, location.getAssignedResources());
    }

    @Test
    void shouldSupportFluentSetters() {
        Location location = new Location()
                .uuid("uuid-1")
                .key("LOC_KEY")
                .name("NAME")
                .description("DESCRIPTION")
                .assignedResources(5);

        assertEquals("uuid-1", location.getUuid());
        assertEquals("LOC_KEY", location.getKey());
        assertEquals("NAME", location.getName());
        assertEquals("DESCRIPTION", location.getDescription());
        assertEquals(5, location.getAssignedResources());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        Location a = new Location()
                .uuid("uuid-1").key("LOC_KEY").name("NAME")
                .description("DESCRIPTION").assignedResources(5);

        Location b = new Location()
                .uuid("uuid-1").key("LOC_KEY").name("NAME")
                .description("DESCRIPTION").assignedResources(5);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentValues() {
        Location a = new Location().uuid("uuid-1").key("KEY_A").name("NAME");
        Location b = new Location().uuid("uuid-1").key("KEY_B").name("NAME");

        assertNotEquals(a, b);
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        Location location = createSample();

        assertNotEquals(null, location);
        assertNotEquals(new Object(), location);
    }

    @Test
    void toStringShouldContainFields() {
        Location location = createSample();
        String result = location.toString();

        assertNotNull(result);
        assertTrue(result.contains("class Location"));
        assertTrue(result.contains("uuid"));
        assertTrue(result.contains("key"));
        assertTrue(result.contains("name"));
        assertTrue(result.contains("description"));
        assertTrue(result.contains("assignedResources"));
    }
}
