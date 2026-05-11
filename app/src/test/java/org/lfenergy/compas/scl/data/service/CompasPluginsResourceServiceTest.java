// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.lfenergy.compas.scl.data.exception.CompasDuplicateVersionException;
import org.lfenergy.compas.scl.data.exception.CompasInvalidInputException;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.entities.PluginsCustomResource;
import org.lfenergy.compas.scl.data.repository.CustomPluginsResourceRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasPluginsResourceServiceTest {

    @Mock
    CustomPluginsResourceRepository repository;

    @InjectMocks
    CompasPluginsResourceService service;

    // --- list ---

    @Test
    void list_WhenCalledWithTypeOnly_ThenReturnsResults() {
        var resource = createResource();
        when(repository.listFiltered("xml", null, null, null, 0, 20)).thenReturn(List.of(resource));

        var result = service.list("xml", null, null, null, 0, 20);

        assertEquals(1, result.size());
        assertEquals(resource, result.get(0));
    }

    @Test
    void list_WhenCalledWithAllFilters_ThenAppliesAllFilters() {
        Date uploadedAfter = new Date();
        Date uploadedBefore = new Date();
        when(repository.listFiltered("xml", uploadedAfter, uploadedBefore, "test", 1, 10)).thenReturn(List.of());

        var result = service.list("xml", uploadedAfter, uploadedBefore, "test", 1, 10);

        assertEquals(0, result.size());
        verify(repository).listFiltered("xml", uploadedAfter, uploadedBefore, "test", 1, 10);
    }

    // --- count ---

    @Test
    void count_WhenCalledWithTypeOnly_ThenReturnsCount() {
        when(repository.countFiltered("xml", null, null, null)).thenReturn(5L);

        var result = service.count("xml", null, null, null);

        assertEquals(5L, result);
    }

    @Test
    void count_WhenCalledWithFilters_ThenAppliesFilters() {
        Date uploadedAfter = new Date();
        Date uploadedBefore = new Date();
        when(repository.countFiltered("xml", uploadedAfter, uploadedBefore, "search")).thenReturn(3L);

        var result = service.count("xml", uploadedAfter, uploadedBefore, "search");

        assertEquals(3L, result);
        verify(repository).countFiltered("xml", uploadedAfter, uploadedBefore, "search");
    }

    // --- findById ---

    @Test
    void findById_WhenEntityExists_ThenReturnsEntity() {
        var id = UUID.randomUUID();
        var resource = createResource();
        when(repository.findByIdOptional(id)).thenReturn(Optional.of(resource));

        var result = service.findById(id);

        assertEquals(resource, result);
    }

    @Test
    void findById_WhenEntityDoesNotExist_ThenThrowsCompasNoDataFoundException() {
        var id = UUID.randomUUID();
        when(repository.findByIdOptional(id)).thenReturn(Optional.empty());

        var exception = assertThrows(CompasNoDataFoundException.class, () -> service.findById(id));
        assertTrue(exception.getMessage().contains(id.toString()));
    }

    @Test
    void findLatestByType_WhenEntitiesExist_ThenReturnsLatestPerName() {
        var older = createResource();
        older.name = "config";
        older.version = "1.2.3";
        var newer = createResource();
        newer.name = "config";
        newer.version = "1.10.0";
        var another = createResource();
        another.name = "another";
        another.version = "2.0.0";
        when(repository.list("type", "xml")).thenReturn(List.of(older, newer, another));

        var result = service.findLatestByType("xml");

        assertEquals(2, result.size());
        assertEquals(another, result.get(0));
        assertEquals(newer, result.get(1));
    }

    @Test
    void findLatestByType_WhenNoEntityExists_ThenThrowsCompasNoDataFoundException() {
        when(repository.list("type", "xml")).thenReturn(List.of());

        var exception = assertThrows(CompasNoDataFoundException.class, () -> service.findLatestByType("xml"));

        assertTrue(exception.getMessage().contains("xml"));
    }

    @Test
    void findLatestByTypeAndName_WhenEntitiesExist_ThenReturnsHighestVersion() {
        var older = createResource();
        older.name = "config";
        older.version = "1.2.3";
        var newer = createResource();
        newer.name = "config";
        newer.version = "1.10.0";
        when(repository.list("type = ?1 and name = ?2", "xml", "config")).thenReturn(List.of(older, newer));

        var result = service.findLatestByTypeAndName("xml", "config");

        assertEquals(newer, result);
    }

    @Test
    void findLatestByTypeAndName_WhenNoEntityExists_ThenThrowsCompasNoDataFoundException() {
        when(repository.list("type = ?1 and name = ?2", "xml", "config")).thenReturn(List.of());

        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> service.findLatestByTypeAndName("xml", "config"));

        assertTrue(exception.getMessage().contains("xml"));
        assertTrue(exception.getMessage().contains("config"));
    }

    @Test
    void deleteByType_WhenEntriesExist_ThenDeletesEntries() {
        when(repository.delete("type = ?1", "xml")).thenReturn(2L);

        service.deleteByType("xml");

        verify(repository).delete("type = ?1", "xml");
    }

    @Test
    void deleteByType_WhenNoEntriesExist_ThenThrowsCompasNoDataFoundException() {
        when(repository.delete("type = ?1", "xml")).thenReturn(0L);

        var exception = assertThrows(CompasNoDataFoundException.class, () -> service.deleteByType("xml"));

        assertTrue(exception.getMessage().contains("xml"));
    }

    @Test
    void deleteByTypeAndName_WhenEntriesExist_ThenDeletesEntries() {
        when(repository.delete("type = ?1 and name = ?2", "xml", "config")).thenReturn(2L);

        service.deleteByTypeAndName("xml", "config");

        verify(repository).delete("type = ?1 and name = ?2", "xml", "config");
    }

    @Test
    void deleteByTypeAndName_WhenNoEntriesExist_ThenThrowsCompasNoDataFoundException() {
        when(repository.delete("type = ?1 and name = ?2", "xml", "config")).thenReturn(0L);

        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> service.deleteByTypeAndName("xml", "config"));

        assertTrue(exception.getMessage().contains("xml"));
        assertTrue(exception.getMessage().contains("config"));
    }

    // --- upload ---

    @Test
    void upload_WhenCalledWithExplicitVersion_ThenPersistsEntity() {
        when(repository.countDuplicate("xml", "default", "name", "2.0.0")).thenReturn(0L);

        var result = service.upload(new UploadCustomPluginsResourceData("xml", "name", "application/xml", "<root/>",
                "1.0.0", "desc", "2.0.0", null));

        verify(repository).persist(any(PluginsCustomResource.class));
        assertEquals("xml", result.type);
        assertEquals("name", result.name);
        assertEquals("application/xml", result.contentType);
        assertEquals("<root/>", result.content);
        assertEquals("2.0.0", result.version);
        assertEquals("1.0.0", result.dataCompatibilityVersion);
        assertEquals("desc", result.description);
        assertEquals("default", result.tenant);
    }

    @Test
    void upload_WhenDuplicateVersionExists_ThenThrowsCompasDuplicateVersionException() {
        when(repository.countDuplicate("xml", "default", "name", "2.0.0")).thenReturn(1L);

        var request = new UploadCustomPluginsResourceData("xml", "name", "application/xml", "<root/>",
                "1.0.0", "desc", "2.0.0", null);
        assertThrows(CompasDuplicateVersionException.class, () -> service.upload(request));
    }

    @ParameterizedTest
    @CsvSource({"MAJOR, 2.0.0", "minor, 1.3.0", "patch, 1.2.4"})
    void upload_WhenNextVersionType_ThenIncrementsVersion(String nextVersionType, String expectedVersion) {
        var existing = createResource();
        existing.version = "1.2.3";
        when(repository.list("type = ?1 and tenant = ?2 and name = ?3", "xml", "default", "name")).thenReturn(List.of(existing));
        when(repository.countDuplicate("xml", "default", "name", expectedVersion)).thenReturn(0L);

        var result = service.upload(new UploadCustomPluginsResourceData("xml", "name", "application/json", "{}",
                "1.0.0", "desc", null, nextVersionType));

        assertEquals(expectedVersion, result.version);
    }

    @Test
    void upload_WhenNextVersionTypeWithNoExistingVersions_ThenReturns100() {
        when(repository.list("type = ?1 and tenant = ?2 and name = ?3", "xml", "default", "name")).thenReturn(List.of());
        when(repository.countDuplicate("xml", "default", "name", "1.0.0")).thenReturn(0L);

        var result = service.upload(new UploadCustomPluginsResourceData("xml", "name", "application/json", "{}",
                "1.0.0", "desc", null, "MAJOR"));

        assertEquals("1.0.0", result.version);
    }

    @Test
    void upload_WhenInvalidNextVersionType_ThenThrowsCompasInvalidInputException() {
        var request = new UploadCustomPluginsResourceData("xml", "name", "application/xml", "<root/>",
                "1.0.0", "desc", null, "INVALID");
        assertThrows(CompasInvalidInputException.class, () -> service.upload(request));
    }

    @Test
    void upload_WhenNoVersionAndNoNextVersionType_ThenThrowsCompasInvalidInputException() {
        var request = new UploadCustomPluginsResourceData("xml", "name", "application/xml", "<root/>",
                "1.0.0", "desc", null, null);
        assertThrows(CompasInvalidInputException.class, () -> service.upload(request));
    }

    @Test
    void upload_WhenBlankVersionAndBlankNextVersionType_ThenThrowsCompasInvalidInputException() {
        var request = new UploadCustomPluginsResourceData("xml", "name", "application/xml", "<root/>",
                "1.0.0", "desc", "  ", "  ");
        assertThrows(CompasInvalidInputException.class, () -> service.upload(request));
    }

    // --- validation ---

    @Test
    void upload_WhenInvalidContentType_ThenThrowsCompasInvalidInputException() {
        var request = new UploadCustomPluginsResourceData("xml", "name", "text/plain", "<root/>",
                "1.0.0", "desc", "1.0.0", null);
        assertThrows(CompasInvalidInputException.class, () -> service.upload(request));
    }

    @Test
    void upload_WhenNullContentType_ThenThrowsCompasInvalidInputException() {
        var request = new UploadCustomPluginsResourceData("xml", "name", null, "<root/>",
                "1.0.0", "desc", "1.0.0", null);
        assertThrows(CompasInvalidInputException.class, () -> service.upload(request));
    }

    @Test
    void upload_WhenInvalidSemverForDataCompatibilityVersion_ThenThrowsCompasInvalidInputException() {
        var request = new UploadCustomPluginsResourceData("xml", "name", "application/xml", "<root/>",
                "not-a-version", "desc", "1.0.0", null);
        assertThrows(CompasInvalidInputException.class, () -> service.upload(request));
    }

    @Test
    void upload_WhenInvalidSemverForExplicitVersion_ThenThrowsCompasInvalidInputException() {
        var request = new UploadCustomPluginsResourceData("xml", "name", "application/xml", "<root/>",
                "1.0.0", "desc", "bad", null);
        assertThrows(CompasInvalidInputException.class, () -> service.upload(request));
    }

    // --- helpers ---

    private PluginsCustomResource createResource() {
        var resource = new PluginsCustomResource();
        resource.id = UUID.randomUUID();
        resource.type = "xml";
        resource.tenant = "default";
        resource.name = "test-resource";
        resource.contentType = "application/xml";
        resource.content = "<root/>";
        resource.version = "1.0.0";
        resource.dataCompatibilityVersion = "1.0.0";
        return resource;
    }
}
