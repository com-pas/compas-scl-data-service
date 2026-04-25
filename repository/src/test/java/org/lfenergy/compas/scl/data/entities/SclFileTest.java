// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.entities;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SclFileTest {

    private SclFileId buildFileId(UUID id, short major, short minor, short patch) {
        var fileId = new SclFileId();
        fileId.id = id;
        fileId.majorVersion = major;
        fileId.minorVersion = minor;
        fileId.patchVersion = patch;
        return fileId;
    }

    private SclFile buildSclFile(SclFileId id, String name, String type,
                                  boolean isDeleted, String createdBy) {
        var f = new SclFile();
        f.id = id;
        f.name = name;
        f.type = type;
        f.isDeleted = isDeleted;
        f.createdBy = createdBy;
        f.creationDate = LocalDateTime.now();
        return f;
    }

    // ---- equals() ----------------------------------------------------------

    @Test
    void equals_WhenSameInstance_ThenReturnsTrue() {
        var f = buildSclFile(buildFileId(UUID.randomUUID(), (short) 1, (short) 0, (short) 0),
                "file", "SCD", false, "user");
        assertEquals(f, f);
    }

    @Test
    void equals_WhenNull_ThenReturnsFalse() {
        var f = buildSclFile(buildFileId(UUID.randomUUID(), (short) 1, (short) 0, (short) 0),
                "file", "SCD", false, "user");
        assertNotEquals(null, f);
    }

    @Test
    void equals_WhenDifferentType_ThenReturnsFalse() {
        var f = buildSclFile(buildFileId(UUID.randomUUID(), (short) 1, (short) 0, (short) 0),
                "file", "SCD", false, "user");
        assertNotEquals("string", f);
    }

    @Test
    void equals_WhenSameId_ThenReturnsTrueRegardlessOfOtherFields() {
        var id = buildFileId(UUID.randomUUID(), (short) 1, (short) 0, (short) 0);
        var a = buildSclFile(id, "file-A", "SCD", false, "userA");
        var b = buildSclFile(id, "file-B", "IED", true, "userB");
        assertEquals(a, b);
    }

    @Test
    void equals_WhenDifferentId_ThenReturnsFalse() {
        var uid = UUID.randomUUID();
        var a = buildSclFile(buildFileId(uid, (short) 1, (short) 0, (short) 0), "file", "SCD", false, "user");
        var b = buildSclFile(buildFileId(uid, (short) 2, (short) 0, (short) 0), "file", "SCD", false, "user");
        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenBothIdsNull_ThenReturnsTrue() {
        var a = new SclFile();
        var b = new SclFile();
        assertEquals(a, b);
    }

    @Test
    void equals_WhenOneIdNull_ThenReturnsFalse() {
        var a = new SclFile();
        var b = buildSclFile(buildFileId(UUID.randomUUID(), (short) 1, (short) 0, (short) 0), "file", "SCD", false, "user");
        assertNotEquals(a, b);
    }

    // ---- hashCode() --------------------------------------------------------

    @Test
    void hashCode_WhenEqualObjects_ThenSameHashCode() {
        var id = buildFileId(UUID.randomUUID(), (short) 1, (short) 0, (short) 0);
        var a = buildSclFile(id, "file-A", "SCD", false, "userA");
        var b = buildSclFile(id, "file-B", "IED", true, "userB");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void hashCode_WhenNullId_ThenDoesNotThrow() {
        var f = new SclFile();
        assertDoesNotThrow(f::hashCode);
    }
}
