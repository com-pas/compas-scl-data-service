// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.exception.CompasInvalidInputException;
import org.lfenergy.compas.scl.data.entities.PluginsCustomResource;
import org.lfenergy.compas.scl.data.service.CompasPluginsResourceService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompasPluginsResourceTest {

    @Mock
    CompasPluginsResourceService service;

    @InjectMocks
    CompasPluginsResource resource;

    @Test
    void getPluginsWithTypes_WhenCalled_ThenMapsServiceResponse() {
        Map<String, List<String>> pluginsWithTypes = new LinkedHashMap<>();
        pluginsWithTypes.put("engineering-wizard", List.of("config", "process"));
        pluginsWithTypes.put("switching", List.of("report"));
        when(service.listPluginsWithTypes()).thenReturn(pluginsWithTypes);

        var response = resource.getPluginsWithTypes();

        assertEquals(2, response.size());
        assertEquals("engineering-wizard", response.get(0).getPlugin());
        assertEquals(List.of("config", "process"), response.get(0).getTypes());
        assertEquals("switching", response.get(1).getPlugin());
        assertEquals(List.of("report"), response.get(1).getTypes());
    }

    @Test
    void uploadData_WhenInputStreamThrowsIOException_ThenThrowsCompasInvalidInputException() throws IOException {
        var inputStream = org.mockito.Mockito.mock(InputStream.class);
        when(inputStream.readAllBytes()).thenThrow(new IOException("read failed"));

        assertThrows(CompasInvalidInputException.class, () ->
                resource.createPluginResource("test-plugin", "xml", "name", "application/xml", inputStream,
                        "1.0.0", "desc", "1.0.0", "MAJOR"));
    }

    @Test
    void getPluginResourceById_WhenUploadedAtIsNull_ThenResponseUploadedAtIsNull() {
        var entity = new PluginsCustomResource();
        entity.id = UUID.randomUUID();
        entity.type = "test-plugin_xml";
        entity.tenant = "default";
        entity.name = "test";
        entity.contentType = "application/xml";
        entity.content = "<root/>";
        entity.version = "1.0.0";
        entity.dataCompatibilityVersion = "1.0.0";
        entity.uploadedAt = null;

        when(service.findByIdAndType(eq(entity.id), eq(entity.type))).thenReturn(entity);

        var response = resource.getPluginResourceById("test-plugin", "xml", entity.id);

        assertNull(response.getUploadedAt());
        assertEquals("xml", response.getType());
    }

    @Test
    void getLatestPluginResourcesByType_WhenUploadedAtIsNull_ThenResponseUploadedAtIsNull() {
        var entity = new PluginsCustomResource();
        entity.id = UUID.randomUUID();
        entity.type = "test-plugin_xml";
        entity.tenant = "default";
        entity.name = "test";
        entity.contentType = "application/xml";
        entity.content = "<root/>";
        entity.version = "1.0.0";
        entity.dataCompatibilityVersion = "1.0.0";
        entity.uploadedAt = null;

        when(service.findLatestByType(eq(entity.type))).thenReturn(List.of(entity));

        var response = resource.getLatestPluginResourcesByType("test-plugin", "xml");

        assertEquals(1, response.size());
        assertNull(response.get(0).getUploadedAt());
        assertEquals("xml", response.get(0).getType());
    }

    @Test
    void deletePluginResourcesByType_WhenCalled_ThenDelegatesToService() {
        resource.deletePluginResourcesByType("test-plugin", "xml");

        org.mockito.Mockito.verify(service).deleteByType("test-plugin_xml");
    }

    @Test
    void getLatestPluginResourceByName_WhenUploadedAtIsNull_ThenResponseUploadedAtIsNull() {
        var entity = new PluginsCustomResource();
        entity.id = UUID.randomUUID();
        entity.type = "test-plugin_xml";
        entity.tenant = "default";
        entity.name = "test";
        entity.contentType = "application/xml";
        entity.content = "<root/>";
        entity.version = "1.0.0";
        entity.dataCompatibilityVersion = "1.0.0";
        entity.uploadedAt = null;

        when(service.findLatestByTypeAndName(eq(entity.type), eq(entity.name))).thenReturn(entity);

        var response = resource.getLatestPluginResourceByName("test-plugin", "xml", entity.name);

        assertNull(response.getUploadedAt());
        assertEquals("xml", response.getType());
    }

    @Test
    void getPluginResourceVersionsByName_WhenCalled_ThenDelegatesAndMapsType() {
        var entity = new PluginsCustomResource();
        entity.id = UUID.randomUUID();
        entity.type = "test-plugin_xml";
        entity.tenant = "default";
        entity.name = "test";
        entity.contentType = "application/xml";
        entity.content = "<root/>";
        entity.version = "1.0.0";
        entity.dataCompatibilityVersion = "1.0.0";

        when(service.findVersionsByTypeAndName(eq(entity.type), eq(entity.name))).thenReturn(List.of(entity));

        var response = resource.getPluginResourceVersionsByName("test-plugin", "xml", entity.name);

        assertEquals(1, response.size());
        assertEquals("xml", response.get(0).getType());
    }

    @Test
    void deletePluginResourceByName_WhenCalled_ThenDelegatesToService() {
        resource.deletePluginResourceByName("test-plugin", "xml", "test");

        org.mockito.Mockito.verify(service).deleteByTypeAndName("test-plugin_xml", "test");
    }

    @Test
    void deletePluginResourcesByType_WhenPluginOrTypeContainsUnderscore_ThenThrowsCompasInvalidInputException() {
        assertThrows(CompasInvalidInputException.class,
                () -> resource.deletePluginResourcesByType("test_plugin", "xml"));
        assertThrows(CompasInvalidInputException.class,
                () -> resource.deletePluginResourcesByType("test-plugin", "xml_type"));
    }
}
