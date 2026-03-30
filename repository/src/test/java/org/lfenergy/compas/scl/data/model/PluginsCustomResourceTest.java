// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PluginsCustomResourceTest {

    @Test
    void defaultTenant_ShouldBeDefault() {
        var resource = new PluginsCustomResource();
        assertEquals("default", resource.tenant);
    }

    @Test
    void equals_SameObject_ShouldReturnTrue() {
        var resource = createResource();
        assertEquals(resource, resource);
    }

    @Test
    void equals_Null_ShouldReturnFalse() {
        var resource = createResource();
        assertNotEquals(null, resource);
    }

    @Test
    void equals_DifferentClass_ShouldReturnFalse() {
        var resource = createResource();
        assertNotEquals("string", resource);
    }

    @Test
    void equals_EqualObjects_ShouldReturnTrue() {
        var id = UUID.randomUUID();
        var uploadedAt = OffsetDateTime.now();

        var resource1 = createResource(id, uploadedAt);
        var resource2 = createResource(id, uploadedAt);

        assertEquals(resource1, resource2);
    }

    @Test
    void equals_DifferentId_ShouldReturnFalse() {
        var uploadedAt = OffsetDateTime.now();
        var resource1 = createResource(UUID.randomUUID(), uploadedAt);
        var resource2 = createResource(UUID.randomUUID(), uploadedAt);

        assertNotEquals(resource1, resource2);
    }

    @Test
    void hashCode_EqualObjects_ShouldBeEqual() {
        var id = UUID.randomUUID();
        var uploadedAt = OffsetDateTime.now();

        var resource1 = createResource(id, uploadedAt);
        var resource2 = createResource(id, uploadedAt);

        assertEquals(resource1.hashCode(), resource2.hashCode());
    }

    @Test
    void hashCode_DifferentObjects_ShouldDiffer() {
        var resource1 = createResource(UUID.randomUUID(), OffsetDateTime.now());
        var resource2 = createResource(UUID.randomUUID(), OffsetDateTime.now());

        assertNotEquals(resource1.hashCode(), resource2.hashCode());
    }

    private PluginsCustomResource createResource() {
        return createResource(UUID.randomUUID(), OffsetDateTime.now());
    }

    private PluginsCustomResource createResource(UUID id, OffsetDateTime uploadedAt) {
        var resource = new PluginsCustomResource();
        resource.id = id;
        resource.type = "nsd";
        resource.tenant = "default";
        resource.name = "TestResource";
        resource.description = "A test resource";
        resource.contentType = "application/xml";
        resource.content = "<nsd></nsd>";
        resource.version = "1.0.0";
        resource.dataCompatibilityVersion = "1.0.0";
        resource.uploadedAt = uploadedAt;
        return resource;
    }
}
