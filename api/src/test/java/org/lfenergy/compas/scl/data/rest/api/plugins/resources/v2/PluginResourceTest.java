// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PluginResourceTest {

    private PluginResource createSample() {
        return new PluginResource()
                .id(UUID.randomUUID())
                .type("engineering_wizard_processes")
                .name("default-process")
                .description("DESCRIPTION")
                .contentType(PluginResource.ContentTypeEnum.APPLICATION_JSON)
                .version("1.2.3")
                .dataCompatibilityVersion("1.0.0")
                .uploadedAt(new Date())
                .content("{\"a\":1}");
    }

    @Test
    void shouldSetAndGetProperties() {
        PluginResource resource = new PluginResource();
        UUID id = UUID.randomUUID();
        Date uploadedAt = new Date();

        resource.setId(id);
        resource.setType("engineering_wizard_processes");
        resource.setName("default-process");
        resource.setDescription("DESCRIPTION");
        resource.setContentType(PluginResource.ContentTypeEnum.APPLICATION_XML);
        resource.setVersion("1.2.3");
        resource.setDataCompatibilityVersion("1.0.0");
        resource.setUploadedAt(uploadedAt);
        resource.setContent("<root/>");

        assertEquals(id, resource.getId());
        assertEquals("engineering_wizard_processes", resource.getType());
        assertEquals("default-process", resource.getName());
        assertEquals("DESCRIPTION", resource.getDescription());
        assertEquals(PluginResource.ContentTypeEnum.APPLICATION_XML, resource.getContentType());
        assertEquals("1.2.3", resource.getVersion());
        assertEquals("1.0.0", resource.getDataCompatibilityVersion());
        assertEquals(uploadedAt, resource.getUploadedAt());
        assertEquals("<root/>", resource.getContent());
    }

    @Test
    void shouldSupportFluentSetters() {
        UUID id = UUID.randomUUID();
        Date uploadedAt = new Date();

        PluginResource resource = new PluginResource()
                .id(id)
                .type("engineering_wizard_processes")
                .name("default-process")
                .description("DESCRIPTION")
                .contentType(PluginResource.ContentTypeEnum.APPLICATION_JSON)
                .version("1.2.3")
                .dataCompatibilityVersion("1.0.0")
                .uploadedAt(uploadedAt)
                .content("{\"a\":1}");

        assertEquals(id, resource.getId());
        assertEquals("engineering_wizard_processes", resource.getType());
        assertEquals("default-process", resource.getName());
        assertEquals("DESCRIPTION", resource.getDescription());
        assertEquals(PluginResource.ContentTypeEnum.APPLICATION_JSON, resource.getContentType());
        assertEquals("1.2.3", resource.getVersion());
        assertEquals("1.0.0", resource.getDataCompatibilityVersion());
        assertEquals(uploadedAt, resource.getUploadedAt());
        assertEquals("{\"a\":1}", resource.getContent());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        UUID id = UUID.randomUUID();
        Date now = new Date();

        PluginResource a = new PluginResource()
                .id(id).type("T").name("N").description("D")
                .contentType(PluginResource.ContentTypeEnum.APPLICATION_JSON)
                .version("1.0.0").dataCompatibilityVersion("1.0.0")
                .uploadedAt(now).content("{}");

        PluginResource b = new PluginResource()
                .id(id).type("T").name("N").description("D")
                .contentType(PluginResource.ContentTypeEnum.APPLICATION_JSON)
                .version("1.0.0").dataCompatibilityVersion("1.0.0")
                .uploadedAt(now).content("{}");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentValues() {
        PluginResource a = new PluginResource().name("A").content("X");
        PluginResource b = new PluginResource().name("A").content("Y");

        assertNotEquals(a, b);
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        PluginResource resource = createSample();

        assertNotEquals(null, resource);
        assertNotEquals(new Object(), resource);
    }

    @Test
    void toStringShouldContainFields() {
        PluginResource resource = createSample();
        String result = resource.toString();

        assertNotNull(result);
        assertTrue(result.contains("class PluginResource"));
        assertTrue(result.contains("id"));
        assertTrue(result.contains("type"));
        assertTrue(result.contains("name"));
        assertTrue(result.contains("version"));
        assertTrue(result.contains("content"));
    }

    @Test
    void contentTypeEnumShouldRoundTripValues() {
        assertEquals("application/json",
                PluginResource.ContentTypeEnum.APPLICATION_JSON.value());
        assertEquals("application/xml",
                PluginResource.ContentTypeEnum.APPLICATION_XML.value());
        assertEquals(PluginResource.ContentTypeEnum.APPLICATION_JSON,
                PluginResource.ContentTypeEnum.fromValue("application/json"));
        assertEquals(PluginResource.ContentTypeEnum.APPLICATION_XML,
                PluginResource.ContentTypeEnum.fromString("application/xml"));
    }

    @Test
    void contentTypeEnumFromValueShouldThrowForUnknown() {
        assertThrows(IllegalArgumentException.class,
                () -> PluginResource.ContentTypeEnum.fromValue("text/plain"));
        assertThrows(IllegalArgumentException.class,
                () -> PluginResource.ContentTypeEnum.fromString("text/plain"));
    }
}
