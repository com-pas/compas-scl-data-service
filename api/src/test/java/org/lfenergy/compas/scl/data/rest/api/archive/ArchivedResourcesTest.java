// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.archive;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArchivedResourcesTest {

    private ArchivedResource createResource() {
        return new ArchivedResource()
                .uuid("uuid-1")
                .name("NAME")
                .author("AUTHOR")
                .contentType("application/xml")
                .version("1.0.0")
                .modifiedAt(OffsetDateTime.now())
                .archivedAt(OffsetDateTime.now());
    }

    @Test
    void shouldSetAndGetResources() {
        ArchivedResources archivedResources = new ArchivedResources();
        List<ArchivedResource> list = List.of(createResource());

        archivedResources.setResources(list);

        assertEquals(list, archivedResources.getResources());
    }

    @Test
    void shouldSupportFluentSetter() {
        ArchivedResource resource = createResource();

        ArchivedResources archivedResources = new ArchivedResources()
                .resources(new ArrayList<>())
                .addResourcesItem(resource);

        assertEquals(1, archivedResources.getResources().size());
        assertEquals(resource, archivedResources.getResources().get(0));
    }

    @Test
    void addResourcesItemShouldInitializeListIfNull() {
        ArchivedResources archivedResources = new ArchivedResources();
        archivedResources.setResources(null);

        ArchivedResource resource = createResource();
        archivedResources.addResourcesItem(resource);

        assertNotNull(archivedResources.getResources());
        assertEquals(1, archivedResources.getResources().size());
        assertEquals(resource, archivedResources.getResources().get(0));
    }

    @Test
    void removeResourcesItemShouldRemoveItem() {
        ArchivedResource resource = createResource();

        ArchivedResources archivedResources = new ArchivedResources()
                .addResourcesItem(resource);

        archivedResources.removeResourcesItem(resource);

        assertTrue(archivedResources.getResources().isEmpty());
    }

    @Test
    void removeResourcesItemShouldHandleNullsGracefully() {
        ArchivedResources archivedResources = new ArchivedResources();
        archivedResources.setResources(null);

        assertDoesNotThrow(() -> archivedResources.removeResourcesItem(null));
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        ArchivedResource resource = createResource();

        ArchivedResources a = new ArchivedResources().addResourcesItem(resource);
        ArchivedResources b = new ArchivedResources().addResourcesItem(resource);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        ArchivedResources archivedResources = new ArchivedResources()
                .addResourcesItem(createResource());

        assertNotEquals(null, archivedResources);
        assertNotEquals(new Object(), archivedResources);
    }

    @Test
    void toStringShouldContainFields() {
        ArchivedResources archivedResources = new ArchivedResources()
                .addResourcesItem(createResource());
        String result = archivedResources.toString();

        assertNotNull(result);
        assertTrue(result.contains("class ArchivedResources"));
        assertTrue(result.contains("resources"));
    }
}
