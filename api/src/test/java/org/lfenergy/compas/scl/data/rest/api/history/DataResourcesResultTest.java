// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.history;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DataResourcesResultTest {

    private DataResource createResource() {
        return new DataResource()
                .uuid(UUID.randomUUID())
                .name("NAME")
                .author("AUTHOR")
                .type("SCD")
                .changedAt(OffsetDateTime.now())
                .version("1.0.0")
                .available(true)
                .deleted(false);
    }

    @Test
    void shouldSetAndGetResults() {
        DataResourcesResult result = new DataResourcesResult();
        List<DataResource> list = List.of(createResource());

        result.setResults(list);

        assertEquals(list, result.getResults());
    }

    @Test
    void shouldSupportFluentSetter() {
        DataResource resource = createResource();

        DataResourcesResult result = new DataResourcesResult()
                .results(new ArrayList<>())
                .addResultsItem(resource);

        assertEquals(1, result.getResults().size());
        assertEquals(resource, result.getResults().get(0));
    }

    @Test
    void addResultsItemShouldInitializeListIfNull() {
        DataResourcesResult result = new DataResourcesResult();
        result.setResults(null);

        DataResource resource = createResource();
        result.addResultsItem(resource);

        assertNotNull(result.getResults());
        assertEquals(1, result.getResults().size());
        assertEquals(resource, result.getResults().get(0));
    }

    @Test
    void removeResultsItemShouldRemoveItem() {
        DataResource resource = createResource();

        DataResourcesResult result = new DataResourcesResult()
                .addResultsItem(resource);

        result.removeResultsItem(resource);

        assertTrue(result.getResults().isEmpty());
    }

    @Test
    void removeResultsItemShouldHandleNullsGracefully() {
        DataResourcesResult result = new DataResourcesResult();
        result.setResults(null);

        assertDoesNotThrow(() -> result.removeResultsItem(null));
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        DataResource resource = createResource();

        DataResourcesResult a = new DataResourcesResult().addResultsItem(resource);
        DataResourcesResult b = new DataResourcesResult().addResultsItem(resource);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        DataResourcesResult result = new DataResourcesResult()
                .addResultsItem(createResource());

        assertNotEquals(null, result);
        assertNotEquals(new Object(), result);
    }

    @Test
    void toStringShouldContainFields() {
        DataResourcesResult result = new DataResourcesResult()
                .addResultsItem(createResource());
        String str = result.toString();

        assertNotNull(str);
        assertTrue(str.contains("class DataResourcesResult"));
        assertTrue(str.contains("results"));
    }
}
