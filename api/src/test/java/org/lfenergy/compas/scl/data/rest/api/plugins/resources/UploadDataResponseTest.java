// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UploadDataResponseTest {

    private UploadDataResponse createSample() {
        return new UploadDataResponse()
                .id(UUID.randomUUID())
                .type("TYPE")
                .tenant("TENANT")
                .name("NAME")
                .version("1.0.0")
                .uploadedAt(new Date());
    }

    @Test
    void shouldSetAndGetProperties() {
        UploadDataResponse response = new UploadDataResponse();
        UUID id = UUID.randomUUID();
        Date now = new Date();

        response.setId(id);
        response.setType("TYPE");
        response.setTenant("TENANT");
        response.setName("NAME");
        response.setVersion("1.0.0");
        response.setUploadedAt(now);

        assertEquals(id, response.getId());
        assertEquals("TYPE", response.getType());
        assertEquals("TENANT", response.getTenant());
        assertEquals("NAME", response.getName());
        assertEquals("1.0.0", response.getVersion());
        assertEquals(now, response.getUploadedAt());
    }

    @Test
    void shouldSupportFluentSetters() {
        UUID id = UUID.randomUUID();
        Date now = new Date();

        UploadDataResponse response = new UploadDataResponse()
                .id(id)
                .type("TYPE")
                .tenant("TENANT")
                .name("NAME")
                .version("1.0.0")
                .uploadedAt(now);

        assertEquals(id, response.getId());
        assertEquals("TYPE", response.getType());
        assertEquals("TENANT", response.getTenant());
        assertEquals("NAME", response.getName());
        assertEquals("1.0.0", response.getVersion());
        assertEquals(now, response.getUploadedAt());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        UUID id = UUID.randomUUID();
        Date now = new Date();

        UploadDataResponse a = new UploadDataResponse()
                .id(id)
                .type("TYPE")
                .tenant("TENANT")
                .name("NAME")
                .version("1.0.0")
                .uploadedAt(now);

        UploadDataResponse b = new UploadDataResponse()
                .id(id)
                .type("TYPE")
                .tenant("TENANT")
                .name("NAME")
                .version("1.0.0")
                .uploadedAt(now);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        UploadDataResponse response = createSample();

        assertNotEquals(null, response);
        assertNotEquals(new Object(), response);
    }

    @Test
    void toStringShouldContainFields() {
        UploadDataResponse response = createSample();
        String result = response.toString();

        assertNotNull(result);
        assertTrue(result.contains("class UploadDataResponse"));
        assertTrue(result.contains("id"));
        assertTrue(result.contains("type"));
        assertTrue(result.contains("tenant"));
        assertTrue(result.contains("name"));
        assertTrue(result.contains("version"));
        assertTrue(result.contains("uploadedAt"));
    }
}