// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.history;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataResourceVersionTest {

    private DataResourceVersion createSample() {
        return new DataResourceVersion()
                .uuid(UUID.randomUUID())
                .name("NAME")
                .author("AUTHOR")
                .type("SCD")
                .changedAt(OffsetDateTime.now())
                .version("1.0.0")
                .available(true)
                .deleted(false)
                .location("LOCATION")
                .comment("COMMENT")
                .archived(false);
    }

    @Test
    void shouldSetAndGetAllProperties() {
        DataResourceVersion version = new DataResourceVersion();
        UUID uuid = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        version.setUuid(uuid);
        version.setName("NAME");
        version.setAuthor("AUTHOR");
        version.setType("SCD");
        version.setChangedAt(now);
        version.setVersion("1.0.0");
        version.setAvailable(true);
        version.setDeleted(false);
        version.setLocation("LOCATION");
        version.setComment("COMMENT");
        version.setArchived(true);

        assertEquals(uuid, version.getUuid());
        assertEquals("NAME", version.getName());
        assertEquals("AUTHOR", version.getAuthor());
        assertEquals("SCD", version.getType());
        assertEquals(now, version.getChangedAt());
        assertEquals("1.0.0", version.getVersion());
        assertTrue(version.getAvailable());
        assertFalse(version.getDeleted());
        assertEquals("LOCATION", version.getLocation());
        assertEquals("COMMENT", version.getComment());
        assertTrue(version.getArchived());
    }

    @Test
    void shouldSupportFluentSetters() {
        UUID uuid = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        DataResourceVersion version = new DataResourceVersion()
                .uuid(uuid)
                .name("NAME")
                .author("AUTHOR")
                .type("SCD")
                .changedAt(now)
                .version("1.0.0")
                .available(true)
                .deleted(false)
                .location("LOCATION")
                .comment("COMMENT")
                .archived(true);

        assertEquals(uuid, version.getUuid());
        assertEquals("NAME", version.getName());
        assertEquals("COMMENT", version.getComment());
        assertTrue(version.getArchived());
    }

    @Test
    void defaultAvailableShouldBeTrue() {
        DataResourceVersion version = new DataResourceVersion();

        assertTrue(version.getAvailable());
    }

    @Test
    void defaultDeletedShouldBeFalse() {
        DataResourceVersion version = new DataResourceVersion();

        assertFalse(version.getDeleted());
    }

    @Test
    void defaultArchivedShouldBeFalse() {
        DataResourceVersion version = new DataResourceVersion();

        assertFalse(version.getArchived());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        UUID uuid = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        DataResourceVersion a = new DataResourceVersion()
                .uuid(uuid).name("NAME").author("AUTHOR").type("SCD")
                .changedAt(now).version("1.0.0").available(true).deleted(false)
                .location("LOCATION").comment("COMMENT").archived(false);

        DataResourceVersion b = new DataResourceVersion()
                .uuid(uuid).name("NAME").author("AUTHOR").type("SCD")
                .changedAt(now).version("1.0.0").available(true).deleted(false)
                .location("LOCATION").comment("COMMENT").archived(false);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentValues() {
        UUID uuid = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        DataResourceVersion a = new DataResourceVersion().uuid(uuid).comment("C1").changedAt(now);
        DataResourceVersion b = new DataResourceVersion().uuid(uuid).comment("C2").changedAt(now);

        assertNotEquals(a, b);
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        DataResourceVersion version = createSample();

        assertNotEquals(null, version);
        assertNotEquals(new Object(), version);
    }

    @Test
    void toStringShouldContainFields() {
        DataResourceVersion version = createSample();
        String result = version.toString();

        assertNotNull(result);
        assertTrue(result.contains("class DataResourceVersion"));
        assertTrue(result.contains("uuid"));
        assertTrue(result.contains("comment"));
        assertTrue(result.contains("archived"));
    }
}
