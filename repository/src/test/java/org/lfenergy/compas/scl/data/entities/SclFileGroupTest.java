// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.entities;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SclFileGroupTest {
    private SclFileGroup buildSclFileGroup(UUID id, String code, String name, String description) {
        var fileGroup = new SclFileGroup();
        fileGroup.id = id;
        fileGroup.code = code;
        fileGroup.name = name;
        fileGroup.description = description;
        return fileGroup;
    }

    @Test
    void equals_WhenSameInstance_ThenReturnsTrue() {
        var fileGroup = buildSclFileGroup(UUID.randomUUID(), "K1", "Name1", "desc");
        assertEquals(fileGroup, fileGroup);
    }

    @Test
    void equals_WhenNull_ThenReturnsFalse() {
        var fileGroup = buildSclFileGroup(UUID.randomUUID(), "K1", "Name1", "desc");
        assertNotEquals(null, fileGroup);
    }

    @Test
    void equals_WhenDifferentType_ThenReturnsFalse() {
        var fileGroup = buildSclFileGroup(UUID.randomUUID(), "K1", "Name1", "desc");
        assertNotEquals("string", fileGroup);
    }

    @Test
    void equals_WhenAllFieldsEqual_ThenReturnsTrue() {
        var id = UUID.randomUUID();
        var a = buildSclFileGroup(id, "K1", "Name1", "desc");
        var b = buildSclFileGroup(id, "K1", "Name1", "desc");
        assertEquals(a, b);
    }

    @Test
    void hashCode_WhenEqualObjects_ThenSameHashCode() {
        var id = UUID.randomUUID();
        var a = buildSclFileGroup(id, "K1", "Name1", "desc");
        var b = buildSclFileGroup(id, "K1", "Name1", "desc");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void hashCode_WhenDifferentId_ThenDifferentHashCode() {
        var a = buildSclFileGroup(UUID.randomUUID(), "K1", "Name1", "desc");
        var b = buildSclFileGroup(UUID.randomUUID(), "K1", "Name1", "desc");
        assertNotEquals(a.hashCode(), b.hashCode());
    }
}
