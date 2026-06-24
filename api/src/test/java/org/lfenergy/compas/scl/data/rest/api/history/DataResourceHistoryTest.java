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

class DataResourceHistoryTest {

    private DataResourceVersion createVersion() {
        return new DataResourceVersion()
                .uuid(UUID.randomUUID())
                .name("NAME")
                .author("AUTHOR")
                .type("SCD")
                .changedAt(OffsetDateTime.now())
                .version("1.0.0")
                .available(true)
                .deleted(false)
                .archived(false);
    }

    @Test
    void shouldSetAndGetVersions() {
        DataResourceHistory history = new DataResourceHistory();
        List<DataResourceVersion> list = List.of(createVersion());

        history.setVersions(list);

        assertEquals(list, history.getVersions());
    }

    @Test
    void shouldSupportFluentSetter() {
        DataResourceVersion version = createVersion();

        DataResourceHistory history = new DataResourceHistory()
                .versions(new ArrayList<>())
                .addVersionsItem(version);

        assertEquals(1, history.getVersions().size());
        assertEquals(version, history.getVersions().get(0));
    }

    @Test
    void addVersionsItemShouldInitializeListIfNull() {
        DataResourceHistory history = new DataResourceHistory();
        history.setVersions(null);

        DataResourceVersion version = createVersion();
        history.addVersionsItem(version);

        assertNotNull(history.getVersions());
        assertEquals(1, history.getVersions().size());
        assertEquals(version, history.getVersions().get(0));
    }

    @Test
    void removeVersionsItemShouldRemoveItem() {
        DataResourceVersion version = createVersion();

        DataResourceHistory history = new DataResourceHistory()
                .addVersionsItem(version);

        history.removeVersionsItem(version);

        assertTrue(history.getVersions().isEmpty());
    }

    @Test
    void removeVersionsItemShouldHandleNullsGracefully() {
        DataResourceHistory history = new DataResourceHistory();
        history.setVersions(null);

        assertDoesNotThrow(() -> history.removeVersionsItem(null));
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        DataResourceVersion version = createVersion();

        DataResourceHistory a = new DataResourceHistory().addVersionsItem(version);
        DataResourceHistory b = new DataResourceHistory().addVersionsItem(version);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        DataResourceHistory history = new DataResourceHistory()
                .addVersionsItem(createVersion());

        assertNotEquals(null, history);
        assertNotEquals(new Object(), history);
    }

    @Test
    void toStringShouldContainFields() {
        DataResourceHistory history = new DataResourceHistory()
                .addVersionsItem(createVersion());
        String result = history.toString();

        assertNotNull(result);
        assertTrue(result.contains("class DataResourceHistory"));
        assertTrue(result.contains("versions"));
    }
}
