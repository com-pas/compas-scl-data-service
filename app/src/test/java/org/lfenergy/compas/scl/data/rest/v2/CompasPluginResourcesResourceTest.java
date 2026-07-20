// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.entities.v2.PluginResource;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.CreatePluginResourceRequest;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.PluginResourceMeta;
import org.lfenergy.compas.scl.data.model.v2.CreatePluginResourceData;
import org.lfenergy.compas.scl.data.rest.v2.CompasPluginResourcesResource;
import org.lfenergy.compas.scl.data.service.v2.PluginResourcesService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompasPluginResourcesResourceTest {

    private static final String PLUGIN = "engineering_wizard";
    private static final String TYPE = "processes";
    private static final String NAME = "default-process";

    @Mock
    PluginResourcesService service;

    @InjectMocks
    CompasPluginResourcesResource resource;

    // ---- createPluginResource ----------------------------------------------

    @Test
    void createPluginResource_WhenCalled_ThenDelegatesToServiceAndMapsResponse() {
        var entity = createEntity();
        when(service.create(any(CreatePluginResourceData.class))).thenReturn(entity);

        var request = new CreatePluginResourceRequest();
        request.setName(NAME);
        request.setDescription("desc");
        request.setContentType(CreatePluginResourceRequest.ContentTypeEnum.APPLICATION_JSON);
        request.setContent("{}");
        request.setVersion("1.2.3");
        request.setDataCompatibilityVersion("1.0.0");

        var response = resource.createPluginResource(PLUGIN, TYPE, request);

        assertEquals(entity.id, response.getId());
        assertEquals(entity.type, response.getType());
        assertEquals(entity.name, response.getName());
        assertEquals(entity.version, response.getVersion());

        var captor = ArgumentCaptor.forClass(CreatePluginResourceData.class);
        verify(service).create(captor.capture());
        var passed = captor.getValue();
        assertEquals(PLUGIN, passed.plugin());
        assertEquals(TYPE, passed.type());
        assertEquals(NAME, passed.name());
        assertEquals("application/json", passed.contentType());
        assertEquals("1.2.3", passed.version());
        assertNull(passed.nextVersionType());
    }

    @Test
    void createPluginResource_WhenNextVersionType_ThenForwardsToService() {
        var entity = createEntity();
        when(service.create(any(CreatePluginResourceData.class))).thenReturn(entity);

        var request = new CreatePluginResourceRequest();
        request.setName(NAME);
        request.setContentType(CreatePluginResourceRequest.ContentTypeEnum.APPLICATION_XML);
        request.setContent("<root/>");
        request.setNextVersionType(CreatePluginResourceRequest.NextVersionTypeEnum.MINOR);
        request.setDataCompatibilityVersion("1.0.0");

        resource.createPluginResource(PLUGIN, TYPE, request);

        var captor = ArgumentCaptor.forClass(CreatePluginResourceData.class);
        verify(service).create(captor.capture());
        assertEquals("minor", captor.getValue().nextVersionType());
        assertNull(captor.getValue().version());
    }

    // ---- deletePluginResourcesByType --------------------------------------

    @Test
    void deletePluginResourcesByType_WhenCalled_ThenDelegatesToService() {
        resource.deletePluginResourcesByType(PLUGIN, TYPE);

        verify(service).deleteByType(PLUGIN, TYPE);
    }

    @Test
    void deletePluginResourcesByType_WhenServiceThrowsNotFound_ThenPropagates() {
        org.mockito.Mockito.doThrow(new CompasNoDataFoundException("not found"))
                .when(service).deleteByType(PLUGIN, TYPE);

        assertThrows(CompasNoDataFoundException.class,
                () -> resource.deletePluginResourcesByType(PLUGIN, TYPE));
    }

    // ---- getPluginResourceById --------------------------------------------

    @Test
    void getPluginResourceById_WhenExists_ThenReturnsResourceWithContent() {
        var id = UUID.randomUUID();
        var entity = createEntity();
        entity.id = id;
        when(service.findById(PLUGIN, TYPE, id)).thenReturn(entity);

        var result = resource.getPluginResourceById(PLUGIN, TYPE, id);

        assertEquals(id, result.getId());
        assertEquals(entity.type, result.getType());
        assertEquals(entity.name, result.getName());
        assertEquals(entity.content, result.getContent());
    }

    @Test
    void getPluginResourceById_WhenNotFound_ThenPropagates() {
        var id = UUID.randomUUID();
        when(service.findById(eq(PLUGIN), eq(TYPE), eq(id)))
                .thenThrow(new CompasNoDataFoundException("not found"));

        assertThrows(CompasNoDataFoundException.class,
                () -> resource.getPluginResourceById(PLUGIN, TYPE, id));
    }

    // ---- getLatestPluginResourcesByType -----------------------------------

    @Test
    void getLatestPluginResourcesByType_WhenCalled_ThenReturnsList() {
        var entity = createEntity();
        when(service.findLatestByType(PLUGIN, TYPE)).thenReturn(List.of(entity));

        var result = resource.getLatestPluginResourcesByType(PLUGIN, TYPE);

        assertEquals(1, result.size());
        assertEquals(entity.id, result.get(0).getId());
    }

    @Test
    void getLatestPluginResourcesByType_WhenUploadedAtIsNull_ThenResponseUploadedAtIsNull() {
        var entity = createEntity();
        entity.uploadedAt = null;
        when(service.findLatestByType(PLUGIN, TYPE)).thenReturn(List.of(entity));

        var result = resource.getLatestPluginResourcesByType(PLUGIN, TYPE);

        assertEquals(1, result.size());
        assertNull(result.get(0).getUploadedAt());
    }

    // ---- deletePluginResourceByName ---------------------------------------

    @Test
    void deletePluginResourceByName_WhenCalled_ThenDelegatesToService() {
        resource.deletePluginResourceByName(PLUGIN, TYPE, NAME);

        verify(service).deleteByName(PLUGIN, TYPE, NAME);
    }

    // ---- getLatestPluginResourceByName ------------------------------------

    @Test
    void getLatestPluginResourceByName_WhenExists_ThenReturnsResourceWithContent() {
        var entity = createEntity();
        when(service.findLatestByName(PLUGIN, TYPE, NAME)).thenReturn(entity);

        var result = resource.getLatestPluginResourceByName(PLUGIN, TYPE, NAME);

        assertEquals(entity.id, result.getId());
        assertEquals(entity.content, result.getContent());
    }

    // ---- getPluginResourceVersionsByName ----------------------------------

    @Test
    void getPluginResourceVersionsByName_WhenExists_ThenReturnsList() {
        var v1 = createEntity();
        v1.version = "2.0.0";
        var v2 = createEntity();
        v2.version = "1.0.0";
        when(service.findVersionsByName(PLUGIN, TYPE, NAME)).thenReturn(List.of(v1, v2));

        List<PluginResourceMeta> result =
                resource.getPluginResourceVersionsByName(PLUGIN, TYPE, NAME);

        assertEquals(2, result.size());
        assertEquals("2.0.0", result.get(0).getVersion());
        assertEquals("1.0.0", result.get(1).getVersion());
    }

    // ---- helpers ----------------------------------------------------------

    private PluginResource createEntity() {
        var entity = new PluginResource();
        entity.id = UUID.randomUUID();
        entity.plugin = PLUGIN;
        entity.type = TYPE;
        entity.tenant = "default";
        entity.name = NAME;
        entity.description = "desc";
        entity.contentType = "application/json";
        entity.content = "{}";
        entity.version = "1.2.3";
        entity.dataCompatibilityVersion = "1.0.0";
        entity.uploadedAt = java.time.OffsetDateTime.now();
        return entity;
    }
}
