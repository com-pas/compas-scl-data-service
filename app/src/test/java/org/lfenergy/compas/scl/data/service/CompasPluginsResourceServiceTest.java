// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    // --- upload ---

    @Test
    void upload_WhenCalledWithExplicitVersion_ThenPersistsEntity() {
        var duplicateQuery = mockTypedQuery(Long.class);
        when(duplicateQuery.getSingleResult()).thenReturn(0L);

        var result = service.upload(new UploadRequest("xml", "name", "application/xml", "<root/>",
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

        assertThrows(CompasDuplicateVersionException.class, () ->
                service.upload(new UploadRequest("xml", "name", "application/xml", "<root/>",
                        "1.0.0", "desc", "2.0.0", null)));
    }

    @Test
    void upload_WhenNextVersionTypeMajor_ThenIncrementsVersion() {
        // duplicate check query
        var duplicateQuery = mockTypedQuery(Long.class);
        when(duplicateQuery.getSingleResult()).thenReturn(0L);

        // existing versions query
        var existingQuery = mockTypedQuery(PluginsCustomResource.class);
        var existing = createResource();
        existing.version = "1.2.3";
        when(existingQuery.getResultList()).thenReturn(List.of(existing));

        var result = service.upload(new UploadRequest("xml", "name", "application/json", "{}",
                "1.0.0", "desc", null, "MAJOR"));

        assertEquals("2.0.0", result.version);
    }

    @Test
    void upload_WhenNextVersionTypeMinor_ThenIncrementsMinorVersion() {
        var duplicateQuery = mockTypedQuery(Long.class);
        when(duplicateQuery.getSingleResult()).thenReturn(0L);

        var existingQuery = mockTypedQuery(PluginsCustomResource.class);
        var existing = createResource();
        existing.version = "1.2.3";
        when(existingQuery.getResultList()).thenReturn(List.of(existing));

        var result = service.upload(new UploadRequest("xml", "name", "application/json", "{}",
                "1.0.0", "desc", null, "minor"));

        assertEquals("1.3.0", result.version);
    }

    @Test
    void upload_WhenNextVersionTypePatch_ThenIncrementsPatchVersion() {
        var duplicateQuery = mockTypedQuery(Long.class);
        when(duplicateQuery.getSingleResult()).thenReturn(0L);

        var existingQuery = mockTypedQuery(PluginsCustomResource.class);
        var existing = createResource();
        existing.version = "1.2.3";
        when(existingQuery.getResultList()).thenReturn(List.of(existing));

        var result = service.upload(new UploadRequest("xml", "name", "application/json", "{}",
                "1.0.0", "desc", null, "patch"));

        assertEquals("1.2.4", result.version);
    }

    @Test
    void upload_WhenNextVersionTypeWithNoExistingVersions_ThenReturns100() {
        var duplicateQuery = mockTypedQuery(Long.class);
        when(duplicateQuery.getSingleResult()).thenReturn(0L);

        var existingQuery = mockTypedQuery(PluginsCustomResource.class);
        when(existingQuery.getResultList()).thenReturn(List.of());

        var result = service.upload(new UploadRequest("xml", "name", "application/json", "{}",
                "1.0.0", "desc", null, "MAJOR"));

        assertEquals("1.0.0", result.version);
    }

    @Test
    void upload_WhenInvalidNextVersionType_ThenThrowsCompasInvalidInputException() {
        assertThrows(CompasInvalidInputException.class, () ->
                service.upload(new UploadRequest("xml", "name", "application/xml", "<root/>",
                        "1.0.0", "desc", null, "INVALID")));
    }

    @Test
    void upload_WhenNoVersionAndNoNextVersionType_ThenThrowsCompasInvalidInputException() {
        assertThrows(CompasInvalidInputException.class, () ->
                service.upload(new UploadRequest("xml", "name", "application/xml", "<root/>",
                        "1.0.0", "desc", null, null)));
    }

    @Test
    void upload_WhenBlankVersionAndBlankNextVersionType_ThenThrowsCompasInvalidInputException() {
        assertThrows(CompasInvalidInputException.class, () ->
                service.upload(new UploadRequest("xml", "name", "application/xml", "<root/>",
                        "1.0.0", "desc", "  ", "  ")));
    }

    // --- validation ---

    @Test
    void upload_WhenInvalidContentType_ThenThrowsCompasInvalidInputException() {
        assertThrows(CompasInvalidInputException.class, () ->
                service.upload(new UploadRequest("xml", "name", "text/plain", "<root/>",
                        "1.0.0", "desc", "1.0.0", null)));
    }

    @Test
    void upload_WhenNullContentType_ThenThrowsCompasInvalidInputException() {
        assertThrows(CompasInvalidInputException.class, () ->
                service.upload(new UploadRequest("xml", "name", null, "<root/>",
                        "1.0.0", "desc", "1.0.0", null)));
    }

    @Test
    void upload_WhenInvalidSemverForDataCompatibilityVersion_ThenThrowsCompasInvalidInputException() {
        assertThrows(CompasInvalidInputException.class, () ->
                service.upload(new UploadRequest("xml", "name", "application/xml", "<root/>",
                        "not-a-version", "desc", "1.0.0", null)));
    }

    @Test
    void upload_WhenInvalidSemverForExplicitVersion_ThenThrowsCompasInvalidInputException() {
        assertThrows(CompasInvalidInputException.class, () ->
                service.upload(new UploadRequest("xml", "name", "application/xml", "<root/>",
                        "1.0.0", "desc", "bad", null)));
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
