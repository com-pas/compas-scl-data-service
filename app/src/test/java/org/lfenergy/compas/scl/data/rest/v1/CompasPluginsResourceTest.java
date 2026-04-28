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
import java.util.List;
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
    void getAllData_WhenSizeIsZero_ThenTotalPagesIsZero() {
        when(service.list(eq("xml"), isNull(), isNull(), isNull(), eq(0), eq(0)))
                .thenReturn(List.of());
        when(service.count(eq("xml"), isNull(), isNull(), isNull()))
                .thenReturn(5L);

        var response = resource.getAllData("xml", null, null, null, 0, 0);

        assertEquals(0, response.getTotalPages());
        assertEquals(5, response.getTotalElements());
    }

    @Test
    void uploadData_WhenInputStreamThrowsIOException_ThenThrowsCompasInvalidInputException() throws IOException {
        var inputStream = org.mockito.Mockito.mock(InputStream.class);
        when(inputStream.readAllBytes()).thenThrow(new IOException("read failed"));

        assertThrows(CompasInvalidInputException.class, () ->
                resource.uploadData("xml", "name", "application/xml", inputStream,
                        "1.0.0", "desc", "1.0.0", "MAJOR"));
    }

    @Test
    void getDataById_WhenUploadedAtIsNull_ThenResponseUploadedAtIsNull() {
        var entity = new PluginsCustomResource();
        entity.id = UUID.randomUUID();
        entity.type = "xml";
        entity.tenant = "default";
        entity.name = "test";
        entity.contentType = "application/xml";
        entity.content = "<root/>";
        entity.version = "1.0.0";
        entity.dataCompatibilityVersion = "1.0.0";
        entity.uploadedAt = null;

        when(service.findById(eq(entity.id))).thenReturn(entity);

        var response = resource.getDataById(entity.id);

        assertNull(response.getUploadedAt());
    }

    @Test
    void getLatestDataByType_WhenUploadedAtIsNull_ThenResponseUploadedAtIsNull() {
        var entity = new PluginsCustomResource();
        entity.id = UUID.randomUUID();
        entity.type = "xml";
        entity.tenant = "default";
        entity.name = "test";
        entity.contentType = "application/xml";
        entity.content = "<root/>";
        entity.version = "1.0.0";
        entity.dataCompatibilityVersion = "1.0.0";
        entity.uploadedAt = null;

        when(service.findLatestByType(eq(entity.type))).thenReturn(List.of(entity));

        var response = resource.getLatestDataByType(entity.type);

        assertEquals(1, response.size());
        assertNull(response.get(0).getUploadedAt());
    }

    @Test
    void deleteDataByType_WhenCalled_ThenDelegatesToService() {
        resource.deleteDataByType("xml");

        org.mockito.Mockito.verify(service).deleteByType("xml");
    }

    @Test
    void getLatestDataByTypeAndName_WhenUploadedAtIsNull_ThenResponseUploadedAtIsNull() {
        var entity = new PluginsCustomResource();
        entity.id = UUID.randomUUID();
        entity.type = "xml";
        entity.tenant = "default";
        entity.name = "test";
        entity.contentType = "application/xml";
        entity.content = "<root/>";
        entity.version = "1.0.0";
        entity.dataCompatibilityVersion = "1.0.0";
        entity.uploadedAt = null;

        when(service.findLatestByTypeAndName(eq(entity.type), eq(entity.name))).thenReturn(entity);

        var response = resource.getLatestDataByTypeAndName(entity.type, entity.name);

        assertNull(response.getUploadedAt());
    }

    @Test
    void deleteDataByTypeAndName_WhenCalled_ThenDelegatesToService() {
        resource.deleteDataByTypeAndName("xml", "test");

        org.mockito.Mockito.verify(service).deleteByTypeAndName("xml", "test");
    }
}
