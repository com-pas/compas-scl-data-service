// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreatePluginResourceRequestTest {

    private CreatePluginResourceRequest createSample() {
        return new CreatePluginResourceRequest()
                .name("default-process")
                .description("DESCRIPTION")
                .contentType(ContentType.APPLICATION_JSON)
                .version("1.2.3")
                .dataCompatibilityVersion("1.0.0")
                .content("{\"a\":1}");
    }

    @Test
    void shouldSetAndGetProperties() {
        CreatePluginResourceRequest request = new CreatePluginResourceRequest();

        request.setName("default-process");
        request.setDescription("DESCRIPTION");
        request.setContentType(ContentType.APPLICATION_XML);
        request.setVersion("1.2.3");
        request.setNextVersionType(CreatePluginResourceRequest.NextVersionTypeEnum.MINOR);
        request.setDataCompatibilityVersion("1.0.0");
        request.setContent("<root/>");

        assertEquals("default-process", request.getName());
        assertEquals("DESCRIPTION", request.getDescription());
        assertEquals(ContentType.APPLICATION_XML, request.getContentType());
        assertEquals("1.2.3", request.getVersion());
        assertEquals(CreatePluginResourceRequest.NextVersionTypeEnum.MINOR, request.getNextVersionType());
        assertEquals("1.0.0", request.getDataCompatibilityVersion());
        assertEquals("<root/>", request.getContent());
    }

    @Test
    void shouldSupportFluentSetters() {
        CreatePluginResourceRequest request = new CreatePluginResourceRequest()
                .name("default-process")
                .description("DESCRIPTION")
                .contentType(ContentType.APPLICATION_JSON)
                .version("1.2.3")
                .nextVersionType(CreatePluginResourceRequest.NextVersionTypeEnum.MAJOR)
                .dataCompatibilityVersion("1.0.0")
                .content("{\"a\":1}");

        assertEquals("default-process", request.getName());
        assertEquals("DESCRIPTION", request.getDescription());
        assertEquals(ContentType.APPLICATION_JSON, request.getContentType());
        assertEquals("1.2.3", request.getVersion());
        assertEquals(CreatePluginResourceRequest.NextVersionTypeEnum.MAJOR, request.getNextVersionType());
        assertEquals("1.0.0", request.getDataCompatibilityVersion());
        assertEquals("{\"a\":1}", request.getContent());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        CreatePluginResourceRequest a = new CreatePluginResourceRequest()
                .name("N").description("D")
                .contentType(ContentType.APPLICATION_JSON)
                .version("1.0.0").dataCompatibilityVersion("1.0.0").content("{}");

        CreatePluginResourceRequest b = new CreatePluginResourceRequest()
                .name("N").description("D")
                .contentType(ContentType.APPLICATION_JSON)
                .version("1.0.0").dataCompatibilityVersion("1.0.0").content("{}");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentValues() {
        CreatePluginResourceRequest a = new CreatePluginResourceRequest().name("A").version("1.0.0");
        CreatePluginResourceRequest b = new CreatePluginResourceRequest().name("B").version("1.0.0");

        assertNotEquals(a, b);
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        CreatePluginResourceRequest request = createSample();

        assertNotEquals(null, request);
        assertNotEquals(new Object(), request);
    }

    @Test
    void toStringShouldContainFields() {
        CreatePluginResourceRequest request = createSample();
        String result = request.toString();

        assertNotNull(result);
        assertTrue(result.contains("class CreatePluginResourceRequest"));
        assertTrue(result.contains("name"));
        assertTrue(result.contains("description"));
        assertTrue(result.contains("contentType"));
        assertTrue(result.contains("version"));
        assertTrue(result.contains("dataCompatibilityVersion"));
        assertTrue(result.contains("content"));
    }

    @Test
    void nextVersionTypeEnumShouldRoundTripValues() {
        assertEquals("major",
                CreatePluginResourceRequest.NextVersionTypeEnum.MAJOR.value());
        assertEquals("minor",
                CreatePluginResourceRequest.NextVersionTypeEnum.MINOR.value());
        assertEquals("patch",
                CreatePluginResourceRequest.NextVersionTypeEnum.PATCH.value());
        assertEquals(CreatePluginResourceRequest.NextVersionTypeEnum.MAJOR,
                CreatePluginResourceRequest.NextVersionTypeEnum.fromValue("major"));
        assertEquals(CreatePluginResourceRequest.NextVersionTypeEnum.MINOR,
                CreatePluginResourceRequest.NextVersionTypeEnum.fromString("minor"));
    }

    @Test
    void nextVersionTypeEnumFromValueShouldThrowForUnknown() {
        assertThrows(IllegalArgumentException.class,
                () -> CreatePluginResourceRequest.NextVersionTypeEnum.fromValue("build"));
    }
}
