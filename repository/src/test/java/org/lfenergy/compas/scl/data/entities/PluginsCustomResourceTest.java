// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.entities;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PluginsCustomResourceTest {

    private PluginsCustomResource buildEntity(UUID id) {
        var entity = new PluginsCustomResource();
        entity.id = id;
        entity.type = "SCD";
        entity.tenant = "default";
        entity.name = "test-plugin";
        entity.description = "A test plugin resource";
        entity.contentType = "application/xml";
        entity.content = "<plugin/>";
        entity.version = "1.0.0";
        entity.dataCompatibilityVersion = "1.0";
        entity.uploadedAt = OffsetDateTime.now();
        return entity;
    }

    // ---- defaults ----------------------------------------------------------

    @Test
    void newEntity_ThenTenantDefaultsToDefault() {
        var entity = new PluginsCustomResource();
        assertEquals("default", entity.tenant);
    }

    // ---- equals() ----------------------------------------------------------

    @Test
    void equals_WhenSameInstance_ThenReturnsTrue() {
        var entity = buildEntity(UUID.randomUUID());
        assertEquals(entity, entity);
    }

    @Test
    void equals_WhenNull_ThenReturnsFalse() {
        var entity = buildEntity(UUID.randomUUID());
        assertNotEquals(null, entity);
    }

    @Test
    void equals_WhenDifferentType_ThenReturnsFalse() {
        var entity = buildEntity(UUID.randomUUID());
        assertNotEquals("string", entity);
    }

    @Test
    void equals_WhenAllFieldsEqual_ThenReturnsTrue() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.uploadedAt = now;

        var b = buildEntity(id);
        b.uploadedAt = now;

        assertEquals(a, b);
    }

    @Test
    void equals_WhenDifferentId_ThenReturnsFalse() {
        var now = OffsetDateTime.now();

        var a = buildEntity(UUID.randomUUID());
        a.uploadedAt = now;

        var b = buildEntity(UUID.randomUUID());
        b.uploadedAt = now;

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentType_FieldValue_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.uploadedAt = now;

        var b = buildEntity(id);
        b.type = "ICD";
        b.uploadedAt = now;

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentTenant_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.uploadedAt = now;

        var b = buildEntity(id);
        b.tenant = "other-tenant";
        b.uploadedAt = now;

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentName_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.uploadedAt = now;

        var b = buildEntity(id);
        b.name = "other-plugin";
        b.uploadedAt = now;

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentContent_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.uploadedAt = now;

        var b = buildEntity(id);
        b.content = "<other/>";
        b.uploadedAt = now;

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentVersion_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.uploadedAt = now;

        var b = buildEntity(id);
        b.version = "2.0.0";
        b.uploadedAt = now;

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentDataCompatibilityVersion_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.uploadedAt = now;

        var b = buildEntity(id);
        b.dataCompatibilityVersion = "2.0";
        b.uploadedAt = now;

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentDescription_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.uploadedAt = now;

        var b = buildEntity(id);
        b.description = "different description";
        b.uploadedAt = now;

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentContentType_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.uploadedAt = now;

        var b = buildEntity(id);
        b.contentType = "application/json";
        b.uploadedAt = now;

        assertNotEquals(a, b);
    }

    // ---- hashCode() --------------------------------------------------------

    @Test
    void hashCode_WhenEqualObjects_ThenSameHashCode() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.uploadedAt = now;

        var b = buildEntity(id);
        b.uploadedAt = now;

        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void hashCode_WhenDifferentId_ThenDifferentHashCode() {
        var now = OffsetDateTime.now();

        var a = buildEntity(UUID.randomUUID());
        a.uploadedAt = now;

        var b = buildEntity(UUID.randomUUID());
        b.uploadedAt = now;

        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void hashCode_WhenNullFields_ThenDoesNotThrow() {
        var entity = new PluginsCustomResource();
        assertDoesNotThrow(entity::hashCode);
    }
}
