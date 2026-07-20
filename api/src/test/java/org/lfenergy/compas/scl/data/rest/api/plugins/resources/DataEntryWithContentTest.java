// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataEntryWithContentTest {

    private DataEntryWithContent createSample() {
        return new DataEntryWithContent(
                "CONTENT",
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
    void shouldCreateUsingAllArgsConstructorAndCallSuper() {
        UUID id = UUID.randomUUID();
        Date uploadedAt = new Date();

        DataEntryWithContent entry = new DataEntryWithContent(
                "CONTENT",
                id,
                "TYPE",
                "TENANT",
                "NAME",
                DataEntry.ContentTypeEnum.APPLICATION_XML,
                "2.0.0",
                "2.0",
                uploadedAt
        );

        // subclass field
        assertEquals("CONTENT", entry.getContent());

        // inherited fields
        assertEquals(id, entry.getId());
        assertEquals("TYPE", entry.getType());
        assertEquals("TENANT", entry.getTenant());
        assertEquals("NAME", entry.getName());
        assertEquals(DataEntry.ContentTypeEnum.APPLICATION_XML, entry.getContentType());
        assertEquals("2.0.0", entry.getVersion());
        assertEquals("2.0", entry.getDataCompatibilityVersion());
        assertEquals(uploadedAt, entry.getUploadedAt());
    }

    @Test
    void shouldSetAndGetContent() {
        DataEntryWithContent entry = new DataEntryWithContent();
        entry.setContent("DATA");

        assertEquals("DATA", entry.getContent());
    }

    @Test
    void shouldSupportFluentContentSetter() {
        DataEntryWithContent entry = new DataEntryWithContent()
                .content("DATA");

        assertEquals("DATA", entry.getContent());
    }

    @Test
    void equalsAndHashCodeShouldMatchIncludingSuperClass() {
        UUID id = UUID.randomUUID();
        Date uploadedAt = new Date();

        DataEntryWithContent a = new DataEntryWithContent(
                "CONTENT",
                id,
                "TYPE",
                "TENANT",
                "NAME",
                DataEntry.ContentTypeEnum.APPLICATION_JSON,
                "1.0",
                "1.0",
                uploadedAt
        );

        DataEntryWithContent b = new DataEntryWithContent(
                "CONTENT",
                id,
                "TYPE",
                "TENANT",
                "NAME",
                DataEntry.ContentTypeEnum.APPLICATION_JSON,
                "1.0",
                "1.0",
                uploadedAt
        );

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseIfContentDiffers() {
        DataEntryWithContent a = createSample();
        DataEntryWithContent b = createSample();

        b.setContent("DIFFERENT");

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenSameInstance_ThenReturnsTrue() {
        DataEntryWithContent entry = createSample();
        assertTrue(entry.equals(entry));
    }

    @Test
    void equalsShouldReturnFalseForDifferentTypeOrNull() {
        DataEntryWithContent entry = createSample();

        assertFalse(entry.equals(null));
        assertFalse(entry.equals(new Object()));
    }

    @Test
    void toStringShouldContainSuperAndSubclassFields() {
        DataEntryWithContent entry = createSample();
        String result = entry.toString();

        assertNotNull(result);
        assertTrue(result.contains("class DataEntryWithContent"));
        assertTrue(result.contains("content"));
        assertTrue(result.contains("DataEntry"));
    }

    @Test
    void toString_WhenContentIsNull_ThenContainsNullLiteral() {
        DataEntryWithContent entry = new DataEntryWithContent();
        String result = entry.toString();

        assertNotNull(result);
        assertTrue(result.contains("null"));
    }
}