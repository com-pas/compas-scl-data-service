// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PluginResourceMetaTest {

    private PluginResourceMeta createSample() {
        return new PluginResourceMeta()
                .id(UUID.randomUUID())
                .type("engineering_wizard_processes")
                .name("default-process")
                .description("DESCRIPTION")
                .contentType(ContentType.APPLICATION_JSON)
                .version("1.2.3")
                .dataCompatibilityVersion("1.0.0")
                .uploadedAt(new Date());
    }

    @Test
    void shouldSetAndGetProperties() {
        PluginResourceMeta meta = new PluginResourceMeta();
        UUID id = UUID.randomUUID();
        Date uploadedAt = new Date();

        meta.setId(id);
        meta.setType("engineering_wizard_processes");
        meta.setName("default-process");
        meta.setDescription("DESCRIPTION");
        meta.setContentType(ContentType.APPLICATION_XML);
        meta.setVersion("1.2.3");
        meta.setDataCompatibilityVersion("1.0.0");
        meta.setUploadedAt(uploadedAt);

        assertEquals(id, meta.getId());
        assertEquals("engineering_wizard_processes", meta.getType());
        assertEquals("default-process", meta.getName());
        assertEquals("DESCRIPTION", meta.getDescription());
        assertEquals(ContentType.APPLICATION_XML, meta.getContentType());
        assertEquals("1.2.3", meta.getVersion());
        assertEquals("1.0.0", meta.getDataCompatibilityVersion());
        assertEquals(uploadedAt, meta.getUploadedAt());
    }

    @Test
    void shouldSupportFluentSetters() {
        UUID id = UUID.randomUUID();
        Date uploadedAt = new Date();

        PluginResourceMeta meta = new PluginResourceMeta()
                .id(id)
                .type("engineering_wizard_processes")
                .name("default-process")
                .description("DESCRIPTION")
                .contentType(ContentType.APPLICATION_JSON)
                .version("1.2.3")
                .dataCompatibilityVersion("1.0.0")
                .uploadedAt(uploadedAt);

        assertEquals(id, meta.getId());
        assertEquals("engineering_wizard_processes", meta.getType());
        assertEquals("default-process", meta.getName());
        assertEquals("DESCRIPTION", meta.getDescription());
        assertEquals(ContentType.APPLICATION_JSON, meta.getContentType());
        assertEquals("1.2.3", meta.getVersion());
        assertEquals("1.0.0", meta.getDataCompatibilityVersion());
        assertEquals(uploadedAt, meta.getUploadedAt());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        UUID id = UUID.randomUUID();
        Date now = new Date();

        PluginResourceMeta a = new PluginResourceMeta()
                .id(id).type("T").name("N").description("D")
                .contentType(ContentType.APPLICATION_JSON)
                .version("1.0.0").dataCompatibilityVersion("1.0.0").uploadedAt(now);

        PluginResourceMeta b = new PluginResourceMeta()
                .id(id).type("T").name("N").description("D")
                .contentType(ContentType.APPLICATION_JSON)
                .version("1.0.0").dataCompatibilityVersion("1.0.0").uploadedAt(now);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentValues() {
        PluginResourceMeta a = new PluginResourceMeta().name("A").version("1.0.0");
        PluginResourceMeta b = new PluginResourceMeta().name("B").version("1.0.0");

        assertNotEquals(a, b);
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        PluginResourceMeta meta = createSample();

        assertNotEquals(null, meta);
        assertNotEquals(new Object(), meta);
    }

    @Test
    void toStringShouldContainFields() {
        PluginResourceMeta meta = createSample();
        String result = meta.toString();

        assertNotNull(result);
        assertTrue(result.contains("class PluginResourceMeta"));
        assertTrue(result.contains("id"));
        assertTrue(result.contains("type"));
        assertTrue(result.contains("name"));
        assertTrue(result.contains("version"));
        assertTrue(result.contains("dataCompatibilityVersion"));
        assertTrue(result.contains("uploadedAt"));
    }
}
