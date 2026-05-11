// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import io.quarkus.hibernate.panache.blocking.PanacheBlockingQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.entities.PluginsCustomResource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class CustomPluginsResourceRepositoryTest {

    @Mock
    private PanacheBlockingQuery<PluginsCustomResource> panacheQuery;

    private CustomPluginsResourceRepository repository;

    @BeforeEach
    void setUp() {
        repository = spy(new CustomPluginsResourceRepository());
    }

    @Test
    void findAllVersionsByTypeAndName_WhenCalled_ThenReturnsList() {
        var resource = new PluginsCustomResource();
        doReturn(List.of(resource)).when(repository).list(anyString(), anyString(), anyString());

        var result = repository.findAllVersionsByTypeAndName("type1", "name1");

        assertEquals(1, result.size());
        assertSame(resource, result.get(0));
        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).list(captor.capture(), eq("type1"), eq("name1"));
        assertTrue(captor.getValue().contains("order by uploadedAt desc"));
    }

    @Test
    void findSpecificVersionByTypeAndName_WhenCalled_ThenReturnsOptional() {
        var resource = new PluginsCustomResource();
        doReturn(panacheQuery).when(repository).find(anyString(), anyString(), anyString(), anyString());
        when(panacheQuery.firstResultOptional()).thenReturn(Optional.of(resource));

        var result = repository.findSpecificVersionByTypeAndName("type1", "name1", "v1");

        assertTrue(result.isPresent());
        assertSame(resource, result.get());
        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).find(captor.capture(), eq("type1"), eq("name1"), eq("v1"));
        assertEquals("type = ?1 and name = ?2 and version = ?3", captor.getValue());
    }

    @Test
    void listFiltered_WhenNoFilters_ThenAppliesPagination() {
        var resource = new PluginsCustomResource();
        doReturn(panacheQuery).when(repository).find(anyString(), any(Map.class));
        when(panacheQuery.page(0, 10)).thenReturn(panacheQuery);
        when(panacheQuery.list()).thenReturn(List.of(resource));

        var result = repository.listFiltered("type1", null, null, null, 0, 10);

        assertEquals(1, result.size());
        assertSame(resource, result.get(0));
        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).find(captor.capture(), any(Map.class));
        assertTrue(captor.getValue().startsWith("type = :type order by uploadedAt desc"));
    }

    @Test
    void listFiltered_WithAllFilters_ThenIncludesFiltersInQuery() {
        var dateAfter = Date.from(Instant.now().minusSeconds(100));
        var dateBefore = Date.from(Instant.now());
        doReturn(panacheQuery).when(repository).find(anyString(), any(Map.class));
        when(panacheQuery.page(1, 20)).thenReturn(panacheQuery);
        when(panacheQuery.list()).thenReturn(List.of());

        repository.listFiltered("type1", dateAfter, dateBefore, "NaMe", 1, 20);

        var queryCaptor = org.mockito.ArgumentCaptor.forClass(String.class);
        var paramsCaptor = org.mockito.ArgumentCaptor.forClass(Map.class);
        verify(repository).find(queryCaptor.capture(), paramsCaptor.capture());

        var query = queryCaptor.getValue();
        assertTrue(query.contains("uploadedAt >= :uploadedAfter"));
        assertTrue(query.contains("uploadedAt <= :uploadedBefore"));
        assertTrue(query.contains("lower(name) like :name"));

        var params = paramsCaptor.getValue();
        assertEquals("type1", params.get("type"));
        assertEquals("%name%", params.get("name"));
        assertNotNull(params.get("uploadedAfter"));
        assertNotNull(params.get("uploadedBefore"));
    }

    @Test
    void countFiltered_WhenCalled_ThenReturnsCount() {
        doReturn(5L).when(repository).count(anyString(), any(Map.class));

        var result = repository.countFiltered("type1", null, null, null);

        assertEquals(5L, result);
        verify(repository).count(argThat((String s) -> s.equals("type = :type")), any(Map.class));
    }

    @Test
    void countDuplicate_WhenCalled_ThenReturnsCount() {
        doReturn(1L).when(repository).count(anyString(), anyString(), anyString(), anyString(), anyString());

        var result = repository.countDuplicate("type1", "tenant1", "name1", "v1");

        assertEquals(1L, result);
        verify(repository).count(argThat((String s) -> s.equals("type = ?1 and tenant = ?2 and name = ?3 and version = ?4")),
            eq("type1"), eq("tenant1"), eq("name1"), eq("v1"));
    }
}

