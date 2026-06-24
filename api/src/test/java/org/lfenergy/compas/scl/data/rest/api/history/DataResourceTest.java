// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.history;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataResourceTest {

    private DataResource createSample() {
        return new DataResource()
                .uuid(UUID.randomUUID())
                .name("NAME")
                .author("AUTHOR")
                .type("SCD")
                .changedAt(OffsetDateTime.now())
                .version("1.0.0")
                .available(true)
                .deleted(false)
                .location("LOCATION");
    }

    @Test
    void shouldSetAndGetProperties() {
        DataResource resource = new DataResource();
        UUID uuid = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        resource.setUuid(uuid);
        resource.setName("NAME");
        resource.setAuthor("AUTHOR");
        resource.setType("SCD");
        resource.setChangedAt(now);
        resource.setVersion("1.0.0");
        resource.setAvailable(true);
        resource.setDeleted(false);
        resource.setLocation("LOCATION");

        assertEquals(uuid, resource.getUuid());
        assertEquals("NAME", resource.getName());
        assertEquals("AUTHOR", resource.getAuthor());
        assertEquals("SCD", resource.getType());
        assertEquals(now, resource.getChangedAt());
        assertEquals("1.0.0", resource.getVersion());
        assertTrue(resource.getAvailable());
        assertFalse(resource.getDeleted());
        assertEquals("LOCATION", resource.getLocation());
    }

    @Test
    void shouldSupportFluentSetters() {
        UUID uuid = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        DataResource resource = new DataResource()
                .uuid(uuid)
                .name("NAME")
                .author("AUTHOR")
                .type("SCD")
                .changedAt(now)
                .version("1.0.0")
                .available(true)
                .deleted(false)
                .location("LOCATION");

        assertEquals(uuid, resource.getUuid());
        assertEquals("NAME", resource.getName());
        assertEquals("AUTHOR", resource.getAuthor());
        assertEquals("SCD", resource.getType());
        assertEquals(now, resource.getChangedAt());
        assertEquals("1.0.0", resource.getVersion());
        assertTrue(resource.getAvailable());
        assertFalse(resource.getDeleted());
        assertEquals("LOCATION", resource.getLocation());
    }

    @Test
    void defaultAvailableShouldBeTrue() {
        DataResource resource = new DataResource();

        assertTrue(resource.getAvailable());
    }

    @Test
    void defaultDeletedShouldBeFalse() {
        DataResource resource = new DataResource();

        assertFalse(resource.getDeleted());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        UUID uuid = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        DataResource a = new DataResource()
                .uuid(uuid).name("NAME").author("AUTHOR").type("SCD")
                .changedAt(now).version("1.0.0").available(true).deleted(false)
                .location("LOCATION");

        DataResource b = new DataResource()
                .uuid(uuid).name("NAME").author("AUTHOR").type("SCD")
                .changedAt(now).version("1.0.0").available(true).deleted(false)
                .location("LOCATION");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentValues() {
        UUID uuid = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        DataResource a = new DataResource().uuid(uuid).name("NAME-A").version("1.0.0").changedAt(now);
        DataResource b = new DataResource().uuid(uuid).name("NAME-B").version("1.0.0").changedAt(now);

        assertNotEquals(a, b);
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        DataResource resource = createSample();

        assertNotEquals(null, resource);
        assertNotEquals(new Object(), resource);
    }

    @Test
    void toStringShouldContainFields() {
        DataResource resource = createSample();
        String result = resource.toString();

        assertNotNull(result);
        assertTrue(result.contains("class DataResource"));
        assertTrue(result.contains("uuid"));
        assertTrue(result.contains("name"));
        assertTrue(result.contains("author"));
        assertTrue(result.contains("version"));
        assertTrue(result.contains("location"));
    }
}
