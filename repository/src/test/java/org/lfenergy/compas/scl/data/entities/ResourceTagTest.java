// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceTagTest {

    private ResourceTag buildTag(String key, String value) {
        var tag = new ResourceTag();
        tag.key = key;
        tag.value = value;
        return tag;
    }

    // ---- equals() ----------------------------------------------------------

    @Test
    void equals_WhenSameInstance_ThenReturnsTrue() {
        var tag = buildTag("env", "prod");
        assertEquals(tag, tag);
    }

    @Test
    void equals_WhenNull_ThenReturnsFalse() {
        var tag = buildTag("env", "prod");
        assertNotEquals(null, tag);
    }

    @Test
    void equals_WhenDifferentType_ThenReturnsFalse() {
        var tag = buildTag("env", "prod");
        assertNotEquals("string", tag);
    }

    @Test
    void equals_WhenSameKeyAndValue_ThenReturnsTrue() {
        var a = buildTag("env", "prod");
        var b = buildTag("env", "prod");
        assertEquals(a, b);
    }

    @Test
    void equals_WhenDifferentKey_ThenReturnsFalse() {
        var a = buildTag("env", "prod");
        var b = buildTag("region", "prod");
        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentValue_ThenReturnsFalse() {
        var a = buildTag("env", "prod");
        var b = buildTag("env", "dev");
        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenBothFieldsNull_ThenReturnsTrue() {
        var a = new ResourceTag();
        var b = new ResourceTag();
        assertEquals(a, b);
    }

    @Test
    void equals_WhenKeyNullValueDiffers_ThenReturnsFalse() {
        var a = new ResourceTag();
        a.value = "prod";
        var b = new ResourceTag();
        b.value = "dev";
        assertNotEquals(a, b);
    }

    // ---- hashCode() --------------------------------------------------------

    @Test
    void hashCode_WhenEqualObjects_ThenSameHashCode() {
        var a = buildTag("env", "prod");
        var b = buildTag("env", "prod");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void hashCode_WhenDifferentObjects_ThenDifferentHashCode() {
        var a = buildTag("env", "prod");
        var b = buildTag("env", "dev");
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void hashCode_WhenNullFields_ThenDoesNotThrow() {
        var tag = new ResourceTag();
        assertDoesNotThrow(tag::hashCode);
    }
}
