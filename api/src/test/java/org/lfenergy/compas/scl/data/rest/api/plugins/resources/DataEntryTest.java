// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataEntryTest {

    private DataEntry createSample() {
        return new DataEntry(
                UUID.randomUUID(),
                "TYPE",
                "TENANT",
                "NAME",
                DataEntry.ContentTypeEnum.APPLICATION_JSON,
                "1.0.0",
                "1.0",
                new Date()
        );
    }

    @Test
    void shouldCreateUsingAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Date now = new Date();

        DataEntry entry = new DataEntry(
                id,
                "TYPE",
                "TENANT",
                "NAME",
                DataEntry.ContentTypeEnum.APPLICATION_XML,
                "2.0.0",
                "2.0",
                now
        );

        assertEquals(id, entry.getId());
        assertEquals("TYPE", entry.getType());
        assertEquals("TENANT", entry.getTenant());
        assertEquals("NAME", entry.getName());
        assertEquals(DataEntry.ContentTypeEnum.APPLICATION_XML, entry.getContentType());
        assertEquals("2.0.0", entry.getVersion());
        assertEquals("2.0", entry.getDataCompatibilityVersion());
        assertEquals(now, entry.getUploadedAt());
    }

    @Test
    void shouldSetAndGetProperties() {
        DataEntry entry = new DataEntry();
        UUID id = UUID.randomUUID();
        Date now = new Date();

        entry.setId(id);
        entry.setType("TYPE");
        entry.setTenant("TENANT");
        entry.setName("NAME");
        entry.setDescription("DESC");
        entry.setContentType(DataEntry.ContentTypeEnum.APPLICATION_JSON);
        entry.setVersion("1.0");
        entry.setDataCompatibilityVersion("1.0");
        entry.setUploadedAt(now);

        assertEquals(id, entry.getId());
        assertEquals("TYPE", entry.getType());
        assertEquals("TENANT", entry.getTenant());
        assertEquals("NAME", entry.getName());
        assertEquals("DESC", entry.getDescription());
        assertEquals(DataEntry.ContentTypeEnum.APPLICATION_JSON, entry.getContentType());
        assertEquals("1.0", entry.getVersion());
        assertEquals("1.0", entry.getDataCompatibilityVersion());
        assertEquals(now, entry.getUploadedAt());
    }

    @Test
    void shouldSupportFluentSetters() {
        UUID id = UUID.randomUUID();
        Date now = new Date();

        DataEntry entry = new DataEntry()
                .id(id)
                .type("TYPE")
                .tenant("TENANT")
                .name("NAME")
                .description("DESC")
                .contentType(DataEntry.ContentTypeEnum.APPLICATION_JSON)
                .version("1.0")
                .dataCompatibilityVersion("1.0")
                .uploadedAt(now);

        assertEquals(id, entry.getId());
        assertEquals("TYPE", entry.getType());
        assertEquals("TENANT", entry.getTenant());
        assertEquals("NAME", entry.getName());
        assertEquals("DESC", entry.getDescription());
        assertEquals(DataEntry.ContentTypeEnum.APPLICATION_JSON, entry.getContentType());
        assertEquals("1.0", entry.getVersion());
        assertEquals("1.0", entry.getDataCompatibilityVersion());
        assertEquals(now, entry.getUploadedAt());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        UUID id = UUID.randomUUID();
        Date now = new Date();

        DataEntry a = new DataEntry(
                id, "TYPE", "TENANT", "NAME",
                DataEntry.ContentTypeEnum.APPLICATION_JSON,
                "1.0", "1.0", now
        );

        DataEntry b = new DataEntry(
                id, "TYPE", "TENANT", "NAME",
                DataEntry.ContentTypeEnum.APPLICATION_JSON,
                "1.0", "1.0", now
        );

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        DataEntry entry = createSample();
        assertNotEquals(new Object(), entry);
        assertNotEquals(null, entry);
    }

    @Test
    void toStringShouldContainClassName() {
        DataEntry entry = createSample();
        String result = entry.toString();

        assertNotNull(result);
        assertTrue(result.contains("class DataEntry"));
        assertTrue(result.contains("id"));
    }

    @Test
    void contentTypeEnumFromValueShouldWork() {
        assertEquals(
                DataEntry.ContentTypeEnum.APPLICATION_JSON,
                DataEntry.ContentTypeEnum.fromValue("application/json")
        );
    }

    @Test
    void contentTypeEnumFromStringShouldWork() {
        assertEquals(
                DataEntry.ContentTypeEnum.APPLICATION_XML,
                DataEntry.ContentTypeEnum.fromString("application/xml")
        );
    }

    @Test
    void contentTypeEnumFromValueShouldThrowOnInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> DataEntry.ContentTypeEnum.fromValue("invalid/type")
        );
    }

    @Test
    void contentTypeEnumFromStringShouldThrowOnInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> DataEntry.ContentTypeEnum.fromString("invalid/type")
        );
    }
}