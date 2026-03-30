// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PagedDataEntryResponseTest {

    private DataEntry createDataEntry() {
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
    void shouldCreateAndSetProperties() {
        PagedDataEntryResponse response = new PagedDataEntryResponse();
        List<DataEntry> content = List.of(createDataEntry());

        response.setContent(content);
        response.setTotalElements(10);
        response.setTotalPages(2);
        response.setPage(0);
        response.setSize(5);

        assertEquals(content, response.getContent());
        assertEquals(10, response.getTotalElements());
        assertEquals(2, response.getTotalPages());
        assertEquals(0, response.getPage());
        assertEquals(5, response.getSize());
    }

    @Test
    void shouldSupportFluentSetters() {
        DataEntry entry = createDataEntry();

        PagedDataEntryResponse response = new PagedDataEntryResponse()
                .content(new ArrayList<>())
                .addContentItem(entry)
                .totalElements(1)
                .totalPages(1)
                .page(0)
                .size(10);

        assertEquals(1, response.getContent().size());
        assertEquals(entry, response.getContent().get(0));
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertEquals(0, response.getPage());
        assertEquals(10, response.getSize());
    }

    @Test
    void addContentItemShouldInitializeListIfNull() {
        PagedDataEntryResponse response = new PagedDataEntryResponse();
        response.setContent(null);

        DataEntry entry = createDataEntry();
        response.addContentItem(entry);

        assertNotNull(response.getContent());
        assertEquals(1, response.getContent().size());
        assertEquals(entry, response.getContent().get(0));
    }

    @Test
    void removeContentItemShouldRemoveItem() {
        DataEntry entry = createDataEntry();

        PagedDataEntryResponse response = new PagedDataEntryResponse()
                .addContentItem(entry);

        response.removeContentItem(entry);

        assertTrue(response.getContent().isEmpty());
    }

    @Test
    void removeContentItemShouldHandleNullsGracefully() {
        PagedDataEntryResponse response = new PagedDataEntryResponse();
        response.setContent(null);

        assertDoesNotThrow(() -> response.removeContentItem(null));
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        DataEntry entry = createDataEntry();
        List<DataEntry> content = List.of(entry);

        PagedDataEntryResponse a = new PagedDataEntryResponse()
                .content(content)
                .totalElements(1)
                .totalPages(1)
                .page(0)
                .size(10);

        PagedDataEntryResponse b = new PagedDataEntryResponse()
                .content(new ArrayList<>(content))
                .totalElements(1)
                .totalPages(1)
                .page(0)
                .size(10);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        PagedDataEntryResponse response = new PagedDataEntryResponse();
        assertNotEquals(null, response);
        assertNotEquals(new Object(), response);
    }

    @Test
    void toStringShouldContainFields() {
        PagedDataEntryResponse response = new PagedDataEntryResponse()
                .addContentItem(createDataEntry())
                .totalElements(1)
                .totalPages(1)
                .page(0)
                .size(10);

        String result = response.toString();

        assertNotNull(result);
        assertTrue(result.contains("class PagedDataEntryResponse"));
        assertTrue(result.contains("content"));
        assertTrue(result.contains("totalElements"));
        assertTrue(result.contains("totalPages"));
        assertTrue(result.contains("page"));
        assertTrue(result.contains("size"));
    }
}