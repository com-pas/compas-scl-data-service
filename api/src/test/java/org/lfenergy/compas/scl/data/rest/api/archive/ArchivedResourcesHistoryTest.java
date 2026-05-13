// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.archive;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArchivedResourcesHistoryTest {

    private ArchivedResourceVersion createVersion() {
        return new ArchivedResourceVersion()
                .uuid("uuid-1")
                .name("NAME")
                .author("AUTHOR")
                .contentType("application/xml")
                .version("1.0.0")
                .modifiedAt(OffsetDateTime.now())
                .archivedAt(OffsetDateTime.now())
                .archived(false);
    }

    @Test
    void shouldSetAndGetVersions() {
        ArchivedResourcesHistory history = new ArchivedResourcesHistory();
        List<ArchivedResourceVersion> list = List.of(createVersion());

        history.setVersions(list);

        assertEquals(list, history.getVersions());
    }

    @Test
    void shouldSupportFluentSetter() {
        ArchivedResourceVersion version = createVersion();

        ArchivedResourcesHistory history = new ArchivedResourcesHistory()
                .versions(new ArrayList<>())
                .addVersionsItem(version);

        assertEquals(1, history.getVersions().size());
        assertEquals(version, history.getVersions().get(0));
    }

    @Test
    void addVersionsItemShouldInitializeListIfNull() {
        ArchivedResourcesHistory history = new ArchivedResourcesHistory();
        history.setVersions(null);

        ArchivedResourceVersion version = createVersion();
        history.addVersionsItem(version);

        assertNotNull(history.getVersions());
        assertEquals(1, history.getVersions().size());
        assertEquals(version, history.getVersions().get(0));
    }

    @Test
    void removeVersionsItemShouldRemoveItem() {
        ArchivedResourceVersion version = createVersion();

        ArchivedResourcesHistory history = new ArchivedResourcesHistory()
                .addVersionsItem(version);

        history.removeVersionsItem(version);

        assertTrue(history.getVersions().isEmpty());
    }

    @Test
    void removeVersionsItemShouldHandleNullsGracefully() {
        ArchivedResourcesHistory history = new ArchivedResourcesHistory();
        history.setVersions(null);

        assertDoesNotThrow(() -> history.removeVersionsItem(null));
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        ArchivedResourceVersion version = createVersion();

        ArchivedResourcesHistory a = new ArchivedResourcesHistory().addVersionsItem(version);
        ArchivedResourcesHistory b = new ArchivedResourcesHistory().addVersionsItem(version);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        ArchivedResourcesHistory history = new ArchivedResourcesHistory()
                .addVersionsItem(createVersion());

        assertNotEquals(null, history);
        assertNotEquals(new Object(), history);
    }

    @Test
    void toStringShouldContainFields() {
        ArchivedResourcesHistory history = new ArchivedResourcesHistory()
                .addVersionsItem(createVersion());
        String result = history.toString();

        assertNotNull(result);
        assertTrue(result.contains("class ArchivedResourcesHistory"));
        assertTrue(result.contains("versions"));
    }
}
