// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.lfenergy.compas.scl.data.exception.CompasDuplicateVersionException;
import org.lfenergy.compas.scl.data.exception.CompasInvalidInputException;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.model.PluginsCustomResource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasPluginsResourceServiceTest {

    @Mock
    EntityManager entityManager;

    @InjectMocks
    CompasPluginsResourceService service;

    // --- list ---

    @Test
    void list_WhenCalledWithTypeOnly_ThenReturnsResults() {
        var query = mockTypedQuery(PluginsCustomResource.class);
        var resource = createResource();
        when(query.getResultList()).thenReturn(List.of(resource));

        var result = service.list("xml", null, null, null, 0, 20);

        assertEquals(1, result.size());
        assertEquals(resource, result.get(0));
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(20);
    }

    @Test
    void list_WhenCalledWithAllFilters_ThenAppliesAllFilters() {
        var query = mockTypedQuery(PluginsCustomResource.class);
        when(query.getResultList()).thenReturn(List.of());

        service.list("xml", new Date(), new Date(), "test", 1, 10);

        var jpqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpqlCaptor.capture(), eq(PluginsCustomResource.class));
        var jpql = jpqlCaptor.getValue();
        assertTrue(jpql.contains("e.uploadedAt >= :uploadedAfter"));
        assertTrue(jpql.contains("e.uploadedAt <= :uploadedBefore"));
        assertTrue(jpql.contains("LOWER(e.name) LIKE :name"));
        verify(query).setParameter(eq("name"), contains("test"));
        verify(query).setFirstResult(10);
        verify(query).setMaxResults(10);
    }

    // --- count ---

    @Test
    void count_WhenCalledWithTypeOnly_ThenReturnsCount() {
        var query = mockTypedQuery(Long.class);
        when(query.getSingleResult()).thenReturn(5L);

        var result = service.count("xml", null, null, null);

        assertEquals(5L, result);
    }

    @Test
    void count_WhenCalledWithFilters_ThenAppliesFilters() {
        var query = mockTypedQuery(Long.class);
        when(query.getSingleResult()).thenReturn(3L);

        var result = service.count("xml", new Date(), new Date(), "search");

        assertEquals(3L, result);
        var jpqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(entityManager).createQuery(jpqlCaptor.capture(), eq(Long.class));
        var jpql = jpqlCaptor.getValue();
        assertTrue(jpql.contains("uploadedAfter"));
        assertTrue(jpql.contains("uploadedBefore"));
        assertTrue(jpql.contains("LOWER(e.name) LIKE :name"));
    }

    // --- findById ---

    @Test
    void findById_WhenEntityExists_ThenReturnsEntity() {
        var id = UUID.randomUUID();
        var resource = createResource();
        when(entityManager.find(PluginsCustomResource.class, id)).thenReturn(resource);

        var result = service.findById(id);

        assertEquals(resource, result);
    }

    @Test
    void findById_WhenEntityDoesNotExist_ThenThrowsCompasNoDataFoundException() {
        var id = UUID.randomUUID();
        when(entityManager.find(PluginsCustomResource.class, id)).thenReturn(null);

        var exception = assertThrows(CompasNoDataFoundException.class, () -> service.findById(id));
        assertTrue(exception.getMessage().contains(id.toString()));
    }

    // --- listLatestPerType ---

    @Test
    void listLatestPerType_WhenMultipleVersionsPerName_ThenReturnsLatestPerName() {
        var query = mockTypedQuery(PluginsCustomResource.class);
        var a1 = createResource(); a1.name = "a"; a1.version = "1.0.0";
        var a2 = createResource(); a2.name = "a"; a2.version = "1.2.0";
        var a3 = createResource(); a3.name = "a"; a3.version = "1.1.0";
        var b1 = createResource(); b1.name = "b"; b1.version = "2.0.0";
        when(query.getResultList()).thenReturn(List.of(a1, a2, a3, b1));

        var result = service.listLatestPerType("xml");

        assertEquals(2, result.size());
        var byName = result.stream().collect(java.util.stream.Collectors.toMap(r -> r.name, r -> r.version));
        assertEquals("1.2.0", byName.get("a"));
        assertEquals("2.0.0", byName.get("b"));
    }

    @Test
    void listLatestPerType_WhenNoResources_ThenReturnsEmptyList() {
        var query = mockTypedQuery(PluginsCustomResource.class);
        when(query.getResultList()).thenReturn(List.of());

        assertTrue(service.listLatestPerType("xml").isEmpty());
    }

    // --- listByName ---

    @Test
    void listByName_WhenVersionsExist_ThenReturnsAll() {
        var query = mockTypedQuery(PluginsCustomResource.class);
        var v1 = createResource(); v1.version = "1.0.0";
        var v2 = createResource(); v2.version = "1.1.0";
        when(query.getResultList()).thenReturn(List.of(v1, v2));

        var result = service.listByName("xml", "name");

        assertEquals(2, result.size());
    }

    @Test
    void listByName_WhenNoVersions_ThenThrowsCompasNoDataFoundException() {
        var query = mockTypedQuery(PluginsCustomResource.class);
        when(query.getResultList()).thenReturn(List.of());

        assertThrows(CompasNoDataFoundException.class, () -> service.listByName("xml", "missing"));
    }

    // --- findByNameAndVersion ---

    @Test
    void findByNameAndVersion_WhenExists_ThenReturnsEntity() {
        var query = mockTypedQuery(PluginsCustomResource.class);
        var resource = createResource();
        when(query.getResultList()).thenReturn(List.of(resource));

        var result = service.findByNameAndVersion("xml", "name", "1.0.0");

        assertEquals(resource, result);
    }

    @Test
    void findByNameAndVersion_WhenMissing_ThenThrowsCompasNoDataFoundException() {
        var query = mockTypedQuery(PluginsCustomResource.class);
        when(query.getResultList()).thenReturn(List.of());

        assertThrows(CompasNoDataFoundException.class,
                () -> service.findByNameAndVersion("xml", "name", "9.9.9"));
    }

    // --- findLatestByName ---

    @Test
    void findLatestByName_WhenMultipleVersions_ThenReturnsHighest() {
        var query = mockTypedQuery(PluginsCustomResource.class);
        var v1 = createResource(); v1.version = "1.0.0";
        var v2 = createResource(); v2.version = "2.5.1";
        var v3 = createResource(); v3.version = "1.9.9";
        when(query.getResultList()).thenReturn(List.of(v1, v2, v3));

        var result = service.findLatestByName("xml", "name");

        assertEquals("2.5.1", result.version);
    }

    @Test
    void findLatestByName_WhenNoVersions_ThenThrowsCompasNoDataFoundException() {
        var query = mockTypedQuery(PluginsCustomResource.class);
        when(query.getResultList()).thenReturn(List.of());

        assertThrows(CompasNoDataFoundException.class,
                () -> service.findLatestByName("xml", "missing"));
    }

    // --- deleteByName ---

    @Test
    void deleteByName_WhenVersionsExist_ThenRemovesAll() {
        var query = mockTypedQuery(PluginsCustomResource.class);
        var v1 = createResource();
        var v2 = createResource();
        when(query.getResultList()).thenReturn(List.of(v1, v2));

        service.deleteByName("xml", "name");

        verify(entityManager).remove(v1);
        verify(entityManager).remove(v2);
    }

    @Test
    void deleteByName_WhenNoVersions_ThenThrowsCompasNoDataFoundException() {
        var query = mockTypedQuery(PluginsCustomResource.class);
        when(query.getResultList()).thenReturn(List.of());

        assertThrows(CompasNoDataFoundException.class, () -> service.deleteByName("xml", "missing"));
        verify(entityManager, never()).remove(any());
    }

    // --- upload ---

    @Test
    void upload_WhenCalledWithExplicitVersion_ThenPersistsEntity() {
        var duplicateQuery = mockTypedQuery(Long.class);
        when(duplicateQuery.getSingleResult()).thenReturn(0L);

        var result = service.upload(new UploadCustomPluginsResourceData("xml", "name", "application/xml", "<root/>",
                "1.0.0", "desc", "2.0.0", null));

        verify(entityManager).persist(any(PluginsCustomResource.class));
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
        var duplicateQuery = mockTypedQuery(Long.class);
        when(duplicateQuery.getSingleResult()).thenReturn(1L);

        var request = new UploadCustomPluginsResourceData("xml", "name", "application/xml", "<root/>",
                "1.0.0", "desc", "2.0.0", null);
        assertThrows(CompasDuplicateVersionException.class, () -> service.upload(request));
    }

    @ParameterizedTest
    @CsvSource({"MAJOR, 2.0.0", "minor, 1.3.0", "patch, 1.2.4"})
    void upload_WhenNextVersionType_ThenIncrementsVersion(String nextVersionType, String expectedVersion) {
        var duplicateQuery = mockTypedQuery(Long.class);
        when(duplicateQuery.getSingleResult()).thenReturn(0L);

        var existingQuery = mockTypedQuery(PluginsCustomResource.class);
        var existing = createResource();
        existing.version = "1.2.3";
        when(existingQuery.getResultList()).thenReturn(List.of(existing));

        var result = service.upload(new UploadCustomPluginsResourceData("xml", "name", "application/json", "{}",
                "1.0.0", "desc", null, nextVersionType));

        assertEquals(expectedVersion, result.version);
    }

    @Test
    void upload_WhenNextVersionTypeWithNoExistingVersions_ThenReturns100() {
        var duplicateQuery = mockTypedQuery(Long.class);
        when(duplicateQuery.getSingleResult()).thenReturn(0L);

        var existingQuery = mockTypedQuery(PluginsCustomResource.class);
        when(existingQuery.getResultList()).thenReturn(List.of());

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

    @SuppressWarnings("unchecked")
    private <T> TypedQuery<T> mockTypedQuery(Class<T> resultClass) {
        TypedQuery<T> query = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(resultClass))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        return query;
    }

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
