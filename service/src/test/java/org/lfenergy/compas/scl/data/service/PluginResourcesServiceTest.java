// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.lfenergy.compas.scl.data.entities.PluginResource;
import org.lfenergy.compas.scl.data.exception.CompasDuplicateVersionException;
import org.lfenergy.compas.scl.data.exception.CompasInvalidInputException;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.repository.PluginResourceRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PluginResourcesServiceTest {

    private static final String PLUGIN = "engineering_wizard";
    private static final String TYPE = "processes";
    private static final String NAME = "default-process";
    private static final String TENANT = "default";

    @Mock
    private PluginResourceRepository pluginResourceRepository;

    @Captor
    private ArgumentCaptor<PluginResource> entityCaptor;

    private PluginResourcesService service;

    @BeforeEach
    void setUp() {
        service = new PluginResourcesService(pluginResourceRepository);
    }

    // ---- findById ----------------------------------------------------------

    @Test
    void findById_WhenEntityFound_ThenReturnsEntity() {
        var id = UUID.randomUUID();
        var entity = buildEntity();
        entity.id = id;
        when(pluginResourceRepository.findByIdForPluginAndType(PLUGIN, TYPE, id))
                .thenReturn(Optional.of(entity));

        var result = service.findById(PLUGIN, TYPE, id);

        assertEquals(entity, result);
    }

    @Test
    void findById_WhenEntityNotFound_ThenThrowsCompasNoDataFoundException() {
        var id = UUID.randomUUID();
        when(pluginResourceRepository.findByIdForPluginAndType(PLUGIN, TYPE, id))
                .thenReturn(Optional.empty());

        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> service.findById(PLUGIN, TYPE, id));

        assertTrue(exception.getMessage().contains(id.toString()));
    }

    // ---- findLatestByType --------------------------------------------------

    @Test
    void findLatestByType_WhenEntitiesExist_ThenReturnsLatestPerName() {
        var older = buildEntity();
        older.name = "config";
        older.version = "1.2.3";
        var newer = buildEntity();
        newer.name = "config";
        newer.version = "1.10.0";
        var another = buildEntity();
        another.name = "another";
        another.version = "2.0.0";
        when(pluginResourceRepository.findAllByPluginAndType(PLUGIN, TYPE))
                .thenReturn(List.of(older, newer, another));

        var result = service.findLatestByType(PLUGIN, TYPE);

        assertEquals(2, result.size());
        assertEquals(another, result.get(0));
        assertEquals(newer, result.get(1));
    }

    @Test
    void findLatestByType_WhenNoEntities_ThenThrowsCompasNoDataFoundException() {
        when(pluginResourceRepository.findAllByPluginAndType(PLUGIN, TYPE))
                .thenReturn(List.of());

        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> service.findLatestByType(PLUGIN, TYPE));

        assertTrue(exception.getMessage().contains(PLUGIN));
        assertTrue(exception.getMessage().contains(TYPE));
    }

    // ---- findLatestByName --------------------------------------------------

    @Test
    void findLatestByName_WhenEntitiesExist_ThenReturnsHighestVersion() {
        var older = buildEntity();
        older.version = "1.2.3";
        var newer = buildEntity();
        newer.version = "1.10.0";
        when(pluginResourceRepository.findAllByPluginTypeAndName(PLUGIN, TYPE, NAME))
                .thenReturn(List.of(older, newer));

        var result = service.findLatestByName(PLUGIN, TYPE, NAME);

        assertEquals(newer, result);
    }

    @Test
    void findLatestByName_WhenNoEntities_ThenThrowsCompasNoDataFoundException() {
        when(pluginResourceRepository.findAllByPluginTypeAndName(PLUGIN, TYPE, NAME))
                .thenReturn(List.of());

        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> service.findLatestByName(PLUGIN, TYPE, NAME));

        assertTrue(exception.getMessage().contains(NAME));
    }

    // ---- findVersionsByName ------------------------------------------------

    @Test
    void findVersionsByName_WhenEntitiesExist_ThenReturnsSortedDescending() {
        var v1 = buildEntity();
        v1.version = "1.0.0";
        var v2 = buildEntity();
        v2.version = "2.0.0";
        var v3 = buildEntity();
        v3.version = "1.5.0";
        when(pluginResourceRepository.findAllByPluginTypeAndName(PLUGIN, TYPE, NAME))
                .thenReturn(List.of(v1, v2, v3));

        var result = service.findVersionsByName(PLUGIN, TYPE, NAME);

        assertEquals(3, result.size());
        assertEquals("2.0.0", result.get(0).version);
        assertEquals("1.5.0", result.get(1).version);
        assertEquals("1.0.0", result.get(2).version);
    }

    @Test
    void findVersionsByName_WhenNoEntities_ThenThrowsCompasNoDataFoundException() {
        when(pluginResourceRepository.findAllByPluginTypeAndName(PLUGIN, TYPE, NAME))
                .thenReturn(List.of());

        assertThrows(CompasNoDataFoundException.class,
                () -> service.findVersionsByName(PLUGIN, TYPE, NAME));
    }

    // ---- deleteByType ------------------------------------------------------

    @Test
    void deleteByType_WhenEntriesExist_ThenDeletes() {
        when(pluginResourceRepository.deleteAllByPluginAndType(PLUGIN, TYPE)).thenReturn(3L);

        service.deleteByType(PLUGIN, TYPE);

        verify(pluginResourceRepository).deleteAllByPluginAndType(PLUGIN, TYPE);
    }

    @Test
    void deleteByType_WhenNoEntries_ThenThrowsCompasNoDataFoundException() {
        when(pluginResourceRepository.deleteAllByPluginAndType(PLUGIN, TYPE)).thenReturn(0L);

        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> service.deleteByType(PLUGIN, TYPE));

        assertTrue(exception.getMessage().contains(PLUGIN));
        assertTrue(exception.getMessage().contains(TYPE));
    }

    // ---- deleteByName ------------------------------------------------------

    @Test
    void deleteByName_WhenEntriesExist_ThenDeletes() {
        when(pluginResourceRepository.deleteAllByPluginTypeAndName(PLUGIN, TYPE, NAME)).thenReturn(2L);

        service.deleteByName(PLUGIN, TYPE, NAME);

        verify(pluginResourceRepository).deleteAllByPluginTypeAndName(PLUGIN, TYPE, NAME);
    }

    @Test
    void deleteByName_WhenNoEntries_ThenThrowsCompasNoDataFoundException() {
        when(pluginResourceRepository.deleteAllByPluginTypeAndName(PLUGIN, TYPE, NAME)).thenReturn(0L);

        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> service.deleteByName(PLUGIN, TYPE, NAME));

        assertTrue(exception.getMessage().contains(NAME));
    }

    // ---- create ------------------------------------------------------------

    @Test
    void create_WhenExplicitVersionAndNoDuplicate_ThenPersistsEntity() {
        when(pluginResourceRepository.existsByPluginTypeTenantNameAndVersion(
                PLUGIN, TYPE, TENANT, NAME, "2.0.0")).thenReturn(false);

        var result = service.create(new CreatePluginResourceData(
                PLUGIN, TYPE, NAME, "application/xml", "<root/>",
                "1.0.0", "desc", "2.0.0", null));

        verify(pluginResourceRepository).persist(entityCaptor.capture());
        var persisted = entityCaptor.getValue();
        assertEquals(PLUGIN, persisted.plugin);
        assertEquals(TYPE, persisted.type);
        assertEquals(NAME, persisted.name);
        assertEquals("application/xml", persisted.contentType);
        assertEquals("<root/>", persisted.content);
        assertEquals("2.0.0", persisted.version);
        assertEquals("1.0.0", persisted.dataCompatibilityVersion);
        assertEquals("desc", persisted.description);
        assertEquals(TENANT, persisted.tenant);
        assertEquals(persisted, result);
    }

    @Test
    void create_WhenDuplicateVersion_ThenThrowsCompasDuplicateVersionException() {
        when(pluginResourceRepository.existsByPluginTypeTenantNameAndVersion(
                PLUGIN, TYPE, TENANT, NAME, "2.0.0")).thenReturn(true);

        var request = new CreatePluginResourceData(PLUGIN, TYPE, NAME, "application/xml", "<root/>",
                "1.0.0", "desc", "2.0.0", null);
        assertThrows(CompasDuplicateVersionException.class, () -> service.create(request));
    }

    @ParameterizedTest
    @CsvSource({"MAJOR, 2.0.0", "minor, 1.3.0", "patch, 1.2.4"})
    void create_WhenNextVersionType_ThenIncrementsVersion(String nextVersionType, String expectedVersion) {
        var existing = buildEntity();
        existing.version = "1.2.3";
        when(pluginResourceRepository.findAllByPluginTypeAndName(PLUGIN, TYPE, NAME))
                .thenReturn(List.of(existing));
        when(pluginResourceRepository.existsByPluginTypeTenantNameAndVersion(
                PLUGIN, TYPE, TENANT, NAME, expectedVersion)).thenReturn(false);

        var result = service.create(new CreatePluginResourceData(
                PLUGIN, TYPE, NAME, "application/json", "{}",
                "1.0.0", "desc", null, nextVersionType));

        assertEquals(expectedVersion, result.version);
    }

    @Test
    void create_WhenNextVersionTypeAndNoExisting_ThenReturns100() {
        when(pluginResourceRepository.findAllByPluginTypeAndName(PLUGIN, TYPE, NAME))
                .thenReturn(List.of());
        when(pluginResourceRepository.existsByPluginTypeTenantNameAndVersion(
                PLUGIN, TYPE, TENANT, NAME, "1.0.0")).thenReturn(false);

        var result = service.create(new CreatePluginResourceData(
                PLUGIN, TYPE, NAME, "application/json", "{}",
                "1.0.0", "desc", null, "MAJOR"));

        assertEquals("1.0.0", result.version);
    }

    @Test
    void create_WhenBothVersionAndNextVersionType_ThenThrowsCompasInvalidInputException() {
        var request = new CreatePluginResourceData(PLUGIN, TYPE, NAME, "application/xml", "<root/>",
                "1.0.0", "desc", "2.0.0", "MAJOR");
        assertThrows(CompasInvalidInputException.class, () -> service.create(request));
    }

    @Test
    void create_WhenInvalidNextVersionType_ThenThrowsCompasInvalidInputException() {
        var request = new CreatePluginResourceData(PLUGIN, TYPE, NAME, "application/xml", "<root/>",
                "1.0.0", "desc", null, "INVALID");
        assertThrows(CompasInvalidInputException.class, () -> service.create(request));
    }

    @Test
    void create_WhenNoVersionAndNoNextVersionType_ThenThrowsCompasInvalidInputException() {
        var request = new CreatePluginResourceData(PLUGIN, TYPE, NAME, "application/xml", "<root/>",
                "1.0.0", "desc", null, null);
        assertThrows(CompasInvalidInputException.class, () -> service.create(request));
    }

    @Test
    void create_WhenInvalidContentType_ThenThrowsCompasInvalidInputException() {
        var request = new CreatePluginResourceData(PLUGIN, TYPE, NAME, "text/plain", "<root/>",
                "1.0.0", "desc", "1.0.0", null);
        assertThrows(CompasInvalidInputException.class, () -> service.create(request));
    }

    @Test
    void create_WhenNullContentType_ThenThrowsCompasInvalidInputException() {
        var request = new CreatePluginResourceData(PLUGIN, TYPE, NAME, null, "<root/>",
                "1.0.0", "desc", "1.0.0", null);
        assertThrows(CompasInvalidInputException.class, () -> service.create(request));
    }

    @Test
    void create_WhenInvalidSemverForDataCompatibilityVersion_ThenThrowsCompasInvalidInputException() {
        var request = new CreatePluginResourceData(PLUGIN, TYPE, NAME, "application/xml", "<root/>",
                "not-a-version", "desc", "1.0.0", null);
        assertThrows(CompasInvalidInputException.class, () -> service.create(request));
    }

    @Test
    void create_WhenInvalidSemverForExplicitVersion_ThenThrowsCompasInvalidInputException() {
        var request = new CreatePluginResourceData(PLUGIN, TYPE, NAME, "application/xml", "<root/>",
                "1.0.0", "desc", "bad", null);
        assertThrows(CompasInvalidInputException.class, () -> service.create(request));
    }

    // ---- helpers -----------------------------------------------------------

    private PluginResource buildEntity() {
        var entity = new PluginResource();
        entity.id = UUID.randomUUID();
        entity.plugin = PLUGIN;
        entity.type = TYPE;
        entity.tenant = TENANT;
        entity.name = NAME;
        entity.contentType = "application/xml";
        entity.content = "<root/>";
        entity.version = "1.0.0";
        entity.dataCompatibilityVersion = "1.0.0";
        return entity;
    }
}
