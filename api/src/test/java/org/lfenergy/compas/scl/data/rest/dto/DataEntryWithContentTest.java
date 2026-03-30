// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.dto;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataEntryWithContentTest {

    private DataEntryWithContent createSample() {
        return new DataEntryWithContent(
                UUID.randomUUID(),
                "TYPE",
                "TENANT",
                "NAME",
                DataEntryWithContent.ContentTypeEnum.APPLICATION_JSON,
                "1.0.0",
                "1.0",
                new Date(),
                "{\"key\":\"value\"}"
        );
    }

    @Test
    void shouldCreateUsingAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        Date now = new Date();
        String content = "<xml/>";

        DataEntryWithContent entry = new DataEntryWithContent(
                id,
                "TYPE",
                "TENANT",
                "NAME",
                DataEntryWithContent.ContentTypeEnum.APPLICATION_XML,
                "2.0.0",
                "2.0",
                now,
                content
        );

        assertEquals(id, entry.getId());
        assertEquals("TYPE", entry.getType());
        assertEquals("TENANT", entry.getTenant());
        assertEquals("NAME", entry.getName());
        assertEquals(DataEntryWithContent.ContentTypeEnum.APPLICATION_XML, entry.getContentType());
        assertEquals("2.0.0", entry.getVersion());
        assertEquals("2.0", entry.getDataCompatibilityVersion());
        assertEquals(now, entry.getUploadedAt());
        assertEquals(content, entry.getContent());
    }

    @Test
    void shouldSetAndGetProperties() {
        DataEntryWithContent entry = new DataEntryWithContent();
        UUID id = UUID.randomUUID();
        Date now = new Date();

        entry.setId(id);
        entry.setType("TYPE");
        entry.setTenant("TENANT");
        entry.setName("NAME");
        entry.setDescription("DESC");
        entry.setContentType(DataEntryWithContent.ContentTypeEnum.APPLICATION_JSON);
        entry.setVersion("1.0");
        entry.setDataCompatibilityVersion("1.0");
        entry.setUploadedAt(now);
        entry.setContent("CONTENT");

        assertEquals(id, entry.getId());
        assertEquals("TYPE", entry.getType());
        assertEquals("TENANT", entry.getTenant());
        assertEquals("NAME", entry.getName());
        assertEquals("DESC", entry.getDescription());
        assertEquals(DataEntryWithContent.ContentTypeEnum.APPLICATION_JSON, entry.getContentType());
        assertEquals("1.0", entry.getVersion());
        assertEquals("1.0", entry.getDataCompatibilityVersion());
        assertEquals(now, entry.getUploadedAt());
        assertEquals("CONTENT", entry.getContent());
    }

    @Test
    void shouldSupportFluentSetters() {
        UUID id = UUID.randomUUID();
        Date now = new Date();

        DataEntryWithContent entry = new DataEntryWithContent()
                .id(id)
                .type("TYPE")
                .tenant("TENANT")
                .name("NAME")
                .description("DESC")
                .contentType(DataEntryWithContent.ContentTypeEnum.APPLICATION_JSON)
                .version("1.0")
                .dataCompatibilityVersion("1.0")
                .uploadedAt(now)
                .content("CONTENT");

        assertEquals(id, entry.getId());
        assertEquals("TYPE", entry.getType());
        assertEquals("TENANT", entry.getTenant());
        assertEquals("NAME", entry.getName());
        assertEquals("DESC", entry.getDescription());
        assertEquals(DataEntryWithContent.ContentTypeEnum.APPLICATION_JSON, entry.getContentType());
        assertEquals("1.0", entry.getVersion());
        assertEquals("1.0", entry.getDataCompatibilityVersion());
        assertEquals(now, entry.getUploadedAt());
        assertEquals("CONTENT", entry.getContent());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        UUID id = UUID.randomUUID();
        Date now = new Date();

        DataEntryWithContent a = new DataEntryWithContent(
                id, "TYPE", "TENANT", "NAME",
                DataEntryWithContent.ContentTypeEnum.APPLICATION_JSON,
                "1.0", "1.0", now, "CONTENT"
        );

        DataEntryWithContent b = new DataEntryWithContent(
                id, "TYPE", "TENANT", "NAME",
                DataEntryWithContent.ContentTypeEnum.APPLICATION_JSON,
                "1.0", "1.0", now, "CONTENT"
        );

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        DataEntryWithContent entry = createSample();
        assertNotEquals(null, entry);
        assertNotEquals(new Object(), entry);
    }

    @Test
    void toStringShouldContainClassNameAndContent() {
        DataEntryWithContent entry = createSample();
        String result = entry.toString();

        assertNotNull(result);
        assertTrue(result.contains("class DataEntryWithContent"));
        assertTrue(result.contains("content"));
    }

    @Test
    void contentTypeEnumFromValueShouldWork() {
        assertEquals(
                DataEntryWithContent.ContentTypeEnum.APPLICATION_JSON,
                DataEntryWithContent.ContentTypeEnum.fromValue("application/json")
        );
    }

    @Test
    void contentTypeEnumFromStringShouldWork() {
        assertEquals(
                DataEntryWithContent.ContentTypeEnum.APPLICATION_XML,
                DataEntryWithContent.ContentTypeEnum.fromString("application/xml")
        );
    }

    @Test
    void contentTypeEnumFromValueShouldThrowOnInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> DataEntryWithContent.ContentTypeEnum.fromValue("invalid/type")
        );
    }

    @Test
    void contentTypeEnumFromStringShouldThrowOnInvalid() {
        assertThrows(
                IllegalArgumentException.class,
                () -> DataEntryWithContent.ContentTypeEnum.fromString("invalid/type")
        );
    }
}
