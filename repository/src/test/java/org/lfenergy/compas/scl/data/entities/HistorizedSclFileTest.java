// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.entities;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HistorizedSclFileTest {

    private HistorizedSclFile buildEntry(UUID id, SclFile sclFile, String contentType, String filename,
                                         OffsetDateTime changedAt, boolean archived, boolean available) {
        var entry = new HistorizedSclFile();
        entry.id = id;
        entry.sclFile = sclFile;
        entry.contentType = contentType;
        entry.filename = filename;
        entry.changedAt = changedAt;
        entry.archived = archived;
        entry.available = available;
        return entry;
    }

    private SclFile buildSclFile(UUID id, String version) {
        var fileId = new SclFileId();
        fileId.id = id;
        fileId.majorVersion = 1;
        fileId.minorVersion = 0;
        fileId.patchVersion = 0;

        var sclFile = new SclFile();
        sclFile.id = fileId;
        sclFile.name = "test-file";
        sclFile.type = "SCD";
        return sclFile;
    }

    @Test
    void equals_WhenSameInstance_ThenReturnsTrue() {
        var entry = new HistorizedSclFile();
        assertEquals(entry, entry);
    }

    @Test
    void equals_WhenNull_ThenReturnsFalse() {
        var entry = new HistorizedSclFile();
        assertNotEquals(null, entry);
    }

    @Test
    void equals_WhenDifferentType_ThenReturnsFalse() {
        var entry = new HistorizedSclFile();
        assertNotEquals("string", entry);
    }

    @Test
    void equals_WhenAllFieldsEqual_ThenReturnsTrue() {
        var id = UUID.randomUUID();
        var sclFileId = UUID.randomUUID();
        var sclFile = buildSclFile(sclFileId, "1.0.0");
        var now = OffsetDateTime.now();

        var entry1 = buildEntry(id, sclFile, "application/xml", "test.scd", now, false, true);
        var entry2 = buildEntry(id, sclFile, "application/xml", "test.scd", now, false, true);

        assertEquals(entry1, entry2);
    }

    @Test
    void equals_WhenDifferentId_ThenReturnsFalse() {
        var sclFile = buildSclFile(UUID.randomUUID(), "1.0.0");
        var now = OffsetDateTime.now();

        var entry1 = buildEntry(UUID.randomUUID(), sclFile, "application/xml", "test.scd", now, false, true);
        var entry2 = buildEntry(UUID.randomUUID(), sclFile, "application/xml", "test.scd", now, false, true);

        assertNotEquals(entry1, entry2);
    }

    @Test
    void equals_WhenDifferentContentType_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var sclFile = buildSclFile(UUID.randomUUID(), "1.0.0");
        var now = OffsetDateTime.now();

        var entry1 = buildEntry(id, sclFile, "application/xml", "test.scd", now, false, true);
        var entry2 = buildEntry(id, sclFile, "text/plain", "test.scd", now, false, true);

        assertNotEquals(entry1, entry2);
    }

    @Test
    void equals_WhenDifferentFilename_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var sclFile = buildSclFile(UUID.randomUUID(), "1.0.0");
        var now = OffsetDateTime.now();

        var entry1 = buildEntry(id, sclFile, "application/xml", "test.scd", now, false, true);
        var entry2 = buildEntry(id, sclFile, "application/xml", "other.scd", now, false, true);

        assertNotEquals(entry1, entry2);
    }

    @Test
    void equals_WhenDifferentArchivedFlag_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var sclFile = buildSclFile(UUID.randomUUID(), "1.0.0");
        var now = OffsetDateTime.now();

        var entry1 = buildEntry(id, sclFile, "application/xml", "test.scd", now, false, true);
        var entry2 = buildEntry(id, sclFile, "application/xml", "test.scd", now, true, true);

        assertNotEquals(entry1, entry2);
    }

    @Test
    void equals_WhenDifferentAvailableFlag_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var sclFile = buildSclFile(UUID.randomUUID(), "1.0.0");
        var now = OffsetDateTime.now();

        var entry1 = buildEntry(id, sclFile, "application/xml", "test.scd", now, false, true);
        var entry2 = buildEntry(id, sclFile, "application/xml", "test.scd", now, false, false);

        assertNotEquals(entry1, entry2);
    }

    @Test
    void hashCode_WhenCalledOnEqualObjects_ThenSameHashCode() {
        var id = UUID.randomUUID();
        var sclFile = buildSclFile(UUID.randomUUID(), "1.0.0");
        var now = OffsetDateTime.now();

        var entry1 = buildEntry(id, sclFile, "application/xml", "test.scd", now, false, true);
        var entry2 = buildEntry(id, sclFile, "application/xml", "test.scd", now, false, true);

        assertEquals(entry1.hashCode(), entry2.hashCode());
    }

    @Test
    void hashCode_WhenCalledOnDifferentObjects_ThenDifferentHashCode() {
        var sclFile = buildSclFile(UUID.randomUUID(), "1.0.0");
        var now = OffsetDateTime.now();

        var entry1 = buildEntry(UUID.randomUUID(), sclFile, "application/xml", "test.scd", now, false, true);
        var entry2 = buildEntry(UUID.randomUUID(), sclFile, "application/xml", "test.scd", now, false, true);

        assertNotEquals(entry1.hashCode(), entry2.hashCode());
    }

    @Test
    void defaults_WhenNewInstanceCreated_ThenDefaultValuesAreSet() {
        var entry = new HistorizedSclFile();

        assertFalse(entry.archived, "archived should default to false");
        assertTrue(entry.available, "available should default to true");
        assertNull(entry.id);
        assertNull(entry.sclFile);
        assertNull(entry.contentType);
        assertNull(entry.filename);
        assertNull(entry.comment);
        assertNull(entry.changedAt);
        assertNull(entry.location);
    }

    @Test
    void comment_CanBeNullWithoutAffectingEquality() {
        var id = UUID.randomUUID();
        var sclFile = buildSclFile(UUID.randomUUID(), "1.0.0");
        var now = OffsetDateTime.now();

        var entry1 = buildEntry(id, sclFile, "application/xml", "test.scd", now, false, true);
        entry1.comment = null;

        var entry2 = buildEntry(id, sclFile, "application/xml", "test.scd", now, false, true);
        entry2.comment = "some comment";

        // comment is not part of equals contract
        assertEquals(entry1, entry2);
    }
}
