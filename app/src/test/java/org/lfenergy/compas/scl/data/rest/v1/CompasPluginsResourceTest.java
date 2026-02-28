// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.exception.CompasInvalidInputException;
import org.lfenergy.compas.scl.data.model.PluginsCustomResource;
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
}
