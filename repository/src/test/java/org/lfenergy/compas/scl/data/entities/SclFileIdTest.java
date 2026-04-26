// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.entities;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SclFileIdTest {

    private SclFileId buildId(UUID id, short major, short minor, short patch) {
        var fileId = new SclFileId();
        fileId.id = id;
        fileId.majorVersion = major;
        fileId.minorVersion = minor;
        fileId.patchVersion = patch;
        return fileId;
    }

    // ---- version() ---------------------------------------------------------

    @Test
    void version_WhenCalled_ThenReturnsMajorMinorPatchString() {
        var fileId = buildId(UUID.randomUUID(), (short) 1, (short) 2, (short) 3);
        assertEquals("1.2.3", fileId.version());
    }

    @Test
    void version_WhenZeroValues_ThenReturnsZeroString() {
        var fileId = buildId(UUID.randomUUID(), (short) 0, (short) 0, (short) 0);
        assertEquals("0.0.0", fileId.version());
    }

    @Test
    void version_WhenLargeValues_ThenFormatsCorrectly() {
        var fileId = buildId(UUID.randomUUID(), (short) 100, (short) 200, (short) 300);
        assertEquals("100.200.300", fileId.version());
    }

    // ---- equals() ----------------------------------------------------------

    @Test
    void equals_WhenSameInstance_ThenReturnsTrue() {
        var fileId = buildId(UUID.randomUUID(), (short) 1, (short) 0, (short) 0);
        assertEquals(fileId, fileId);
    }

    @Test
    void equals_WhenNull_ThenReturnsFalse() {
        var fileId = buildId(UUID.randomUUID(), (short) 1, (short) 0, (short) 0);
        assertNotEquals(null, fileId);
    }

    @Test
    void equals_WhenDifferentType_ThenReturnsFalse() {
        var fileId = buildId(UUID.randomUUID(), (short) 1, (short) 0, (short) 0);
        assertNotEquals("string", fileId);
    }

    @Test
    void equals_WhenAllFieldsEqual_ThenReturnsTrue() {
        var id = UUID.randomUUID();
        var a = buildId(id, (short) 1, (short) 2, (short) 3);
        var b = buildId(id, (short) 1, (short) 2, (short) 3);
        assertEquals(a, b);
    }

    @Test
    void equals_WhenDifferentUUID_ThenReturnsFalse() {
        var a = buildId(UUID.randomUUID(), (short) 1, (short) 0, (short) 0);
        var b = buildId(UUID.randomUUID(), (short) 1, (short) 0, (short) 0);
        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentMajorVersion_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var a = buildId(id, (short) 1, (short) 0, (short) 0);
        var b = buildId(id, (short) 2, (short) 0, (short) 0);
        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentMinorVersion_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var a = buildId(id, (short) 1, (short) 0, (short) 0);
        var b = buildId(id, (short) 1, (short) 1, (short) 0);
        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentPatchVersion_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var a = buildId(id, (short) 1, (short) 0, (short) 0);
        var b = buildId(id, (short) 1, (short) 0, (short) 1);
        assertNotEquals(a, b);
    }

    // ---- hashCode() --------------------------------------------------------

    @Test
    void hashCode_WhenEqualObjects_ThenSameHashCode() {
        var id = UUID.randomUUID();
        var a = buildId(id, (short) 1, (short) 2, (short) 3);
        var b = buildId(id, (short) 1, (short) 2, (short) 3);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void hashCode_WhenDifferentVersions_ThenDifferentHashCode() {
        var id = UUID.randomUUID();
        var a = buildId(id, (short) 1, (short) 0, (short) 0);
        var b = buildId(id, (short) 2, (short) 0, (short) 0);
        assertNotEquals(a.hashCode(), b.hashCode());
    }
}
