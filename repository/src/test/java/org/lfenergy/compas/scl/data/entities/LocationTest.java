// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.entities;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    private Location buildLocation(UUID id, String key, String name, String description) {
        var loc = new Location();
        loc.id = id;
        loc.key = key;
        loc.name = name;
        loc.description = description;
        return loc;
    }

    // ---- defaults ----------------------------------------------------------

    @Test
    void newLocation_ThenAssignedResourcesDefaultsToZero() {
        var loc = new Location();
        assertEquals(0, loc.assignedResources);
    }

    // ---- equals() ----------------------------------------------------------

    @Test
    void equals_WhenSameInstance_ThenReturnsTrue() {
        var loc = buildLocation(UUID.randomUUID(), "K1", "Name1", "desc");
        assertEquals(loc, loc);
    }

    @Test
    void equals_WhenNull_ThenReturnsFalse() {
        var loc = buildLocation(UUID.randomUUID(), "K1", "Name1", "desc");
        assertNotEquals(null, loc);
    }

    @Test
    void equals_WhenDifferentType_ThenReturnsFalse() {
        var loc = buildLocation(UUID.randomUUID(), "K1", "Name1", "desc");
        assertNotEquals("string", loc);
    }

    @Test
    void equals_WhenAllFieldsEqual_ThenReturnsTrue() {
        var id = UUID.randomUUID();
        var a = buildLocation(id, "K1", "Name1", "desc");
        var b = buildLocation(id, "K1", "Name1", "desc");
        assertEquals(a, b);
    }

    @Test
    void equals_WhenDifferentId_ThenReturnsFalse() {
        var a = buildLocation(UUID.randomUUID(), "K1", "Name1", "desc");
        var b = buildLocation(UUID.randomUUID(), "K1", "Name1", "desc");
        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentKey_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var a = buildLocation(id, "K1", "Name1", "desc");
        var b = buildLocation(id, "K2", "Name1", "desc");
        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentName_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var a = buildLocation(id, "K1", "Name1", "desc");
        var b = buildLocation(id, "K1", "Name2", "desc");
        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentDescription_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var a = buildLocation(id, "K1", "Name1", "desc-A");
        var b = buildLocation(id, "K1", "Name1", "desc-B");
        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDescriptionNull_ThenComparesCorrectly() {
        var id = UUID.randomUUID();
        var a = buildLocation(id, "K1", "Name1", null);
        var b = buildLocation(id, "K1", "Name1", null);
        assertEquals(a, b);
    }

    @Test
    void equals_WhenAssignedResourcesDiffers_ThenStillEqual() {
        // assignedResources is intentionally NOT part of equals
        var id = UUID.randomUUID();
        var a = buildLocation(id, "K1", "Name1", "desc");
        var b = buildLocation(id, "K1", "Name1", "desc");
        b.assignedResources = 5;
        assertEquals(a, b);
    }

    // ---- hashCode() --------------------------------------------------------

    @Test
    void hashCode_WhenEqualObjects_ThenSameHashCode() {
        var id = UUID.randomUUID();
        var a = buildLocation(id, "K1", "Name1", "desc");
        var b = buildLocation(id, "K1", "Name1", "desc");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void hashCode_WhenDifferentId_ThenDifferentHashCode() {
        var a = buildLocation(UUID.randomUUID(), "K1", "Name1", "desc");
        var b = buildLocation(UUID.randomUUID(), "K1", "Name1", "desc");
        assertNotEquals(a.hashCode(), b.hashCode());
    }
}
