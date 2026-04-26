// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.entities.ArchivedResource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ArchivedResourceRepositoryTest {

    @Mock
    private Session session;

    private ArchivedResourceRepository repository;

    @BeforeEach
    void setUp() {
        repository = spy(new ArchivedResourceRepository() {
            @Override
            public Session getSession() {
                return session;
            }
        });
    }

    // ---- findAllByResourceId -----------------------------------------------

    @Test
    void findAllByResourceId_WhenCalled_ThenDelegatesToListWithQuery() {
        var resourceId = UUID.randomUUID();
        var e1 = new ArchivedResource();
        var e2 = new ArchivedResource();
        doReturn(List.of(e1, e2)).when(repository).list(anyString(), eq(resourceId));

        var result = repository.findAllByResourceId(resourceId);

        assertEquals(List.of(e1, e2), result);
    }

    @Test
    void findAllByResourceId_WhenNoEntries_ThenReturnsEmptyList() {
        var resourceId = UUID.randomUUID();
        doReturn(List.of()).when(repository).list(anyString(), eq(resourceId));

        var result = repository.findAllByResourceId(resourceId);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllByResourceId_WhenCalled_ThenQueryOrdersByArchivedAtDesc() {
        var resourceId = UUID.randomUUID();
        doReturn(List.of()).when(repository).list(anyString(), eq(resourceId));

        repository.findAllByResourceId(resourceId);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).list(captor.capture(), eq(resourceId));
        assertTrue(captor.getValue().contains("archivedAt desc"),
                "Query must order by archivedAt desc");
    }

    // ---- findLatestByResourceId --------------------------------------------

    @Test
    void findLatestByResourceId_WhenEntryExists_ThenReturnsOptionalPresent() {
        var resourceId = UUID.randomUUID();
        var entity = new ArchivedResource();

        var panacheQuery = mock(io.quarkus.hibernate.panache.blocking.PanacheBlockingQuery.class);
        doReturn(panacheQuery).when(repository).find(anyString(), eq(resourceId));
        when(panacheQuery.firstResultOptional()).thenReturn(Optional.of(entity));

        var result = repository.findLatestByResourceId(resourceId);

        assertTrue(result.isPresent());
        assertSame(entity, result.get());
    }

    @Test
    void findLatestByResourceId_WhenNoEntry_ThenReturnsEmptyOptional() {
        var resourceId = UUID.randomUUID();

        var panacheQuery = mock(io.quarkus.hibernate.panache.blocking.PanacheBlockingQuery.class);
        doReturn(panacheQuery).when(repository).find(anyString(), eq(resourceId));
        when(panacheQuery.firstResultOptional()).thenReturn(Optional.empty());

        var result = repository.findLatestByResourceId(resourceId);

        assertFalse(result.isPresent());
    }

    // ---- existsByResourceIdAndVersion --------------------------------------

    @Test
    void existsByResourceIdAndVersion_WhenArchived_ThenReturnsTrue() {
        var resourceId = UUID.randomUUID();
        doReturn(1L).when(repository).count(anyString(), eq(resourceId), eq("1.0.0"));

        assertTrue(repository.existsByResourceIdAndVersion(resourceId, "1.0.0"));
    }

    @Test
    void existsByResourceIdAndVersion_WhenNotArchived_ThenReturnsFalse() {
        var resourceId = UUID.randomUUID();
        doReturn(0L).when(repository).count(anyString(), eq(resourceId), eq("1.0.0"));

        assertFalse(repository.existsByResourceIdAndVersion(resourceId, "1.0.0"));
    }

    @Test
    void existsByResourceIdAndVersion_WhenCalled_ThenQueryChecksArchivedFlag() {
        var resourceId = UUID.randomUUID();
        doReturn(0L).when(repository).count(anyString(), eq(resourceId), eq("2.0.0"));

        repository.existsByResourceIdAndVersion(resourceId, "2.0.0");

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).count(captor.capture(), any(), any());
        assertTrue(captor.getValue().contains("archived = true"),
                "Query must filter on archived = true");
    }

    // ---- searchByFilters — no filters → listAll() -------------------------

    @Test
    void searchByFilters_WhenNoFiltersProvided_ThenCallsListAll() {
        var e1 = new ArchivedResource();
        doReturn(List.of(e1)).when(repository).listAll();

        var result = repository.searchByFilters(null, null, null, null, null, null, null, null);

        assertSame(e1, result.get(0));
        verify(repository).listAll();
        verify(repository, never()).list(anyString(), any(Map.class));
    }

    @Test
    void searchByFilters_WhenAllFiltersBlank_ThenCallsListAll() {
        doReturn(List.of()).when(repository).listAll();

        repository.searchByFilters("", " ", "", "", "", "", null, null);

        verify(repository).listAll();
    }

    // ---- searchByFilters — exact filters ----------------------------------

    @Test
    void searchByFilters_WhenLocationProvided_ThenListContainsLocationCondition() {
        doReturn(List.of()).when(repository).list(anyString(), any(Map.class));

        repository.searchByFilters("LOC-01", null, null, null, null, null, null, null);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).list(captor.capture(), any(Map.class));
        assertTrue(captor.getValue().contains("location = :location"));
    }

    @Test
    void searchByFilters_WhenTypeProvided_ThenListContainsTypeCondition() {
        doReturn(List.of()).when(repository).list(anyString(), any(Map.class));

        repository.searchByFilters(null, null, null, null, "SCD", null, null, null);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).list(captor.capture(), any(Map.class));
        assertTrue(captor.getValue().contains("type = :type"));
    }

    @Test
    void searchByFilters_WhenNameProvided_ThenQueryUsesLikeOnLower() {
        doReturn(List.of()).when(repository).list(anyString(), any(Map.class));

        repository.searchByFilters(null, "Test", null, null, null, null, null, null);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).list(captor.capture(), any(Map.class));
        assertTrue(captor.getValue().contains("lower(name) like :name"));
    }

    @Test
    void searchByFilters_WhenApproverProvided_ThenQueryUsesLikeOnLower() {
        doReturn(List.of()).when(repository).list(anyString(), any(Map.class));

        repository.searchByFilters(null, null, "Alice", null, null, null, null, null);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).list(captor.capture(), any(Map.class));
        assertTrue(captor.getValue().contains("lower(approver) like :approver"));
    }

    @Test
    void searchByFilters_WhenFromProvided_ThenQueryContainsFromCondition() {
        var from = OffsetDateTime.now().minusDays(7);
        doReturn(List.of()).when(repository).list(anyString(), any(Map.class));

        repository.searchByFilters(null, null, null, null, null, null, from, null);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).list(captor.capture(), any(Map.class));
        assertTrue(captor.getValue().contains("archivedAt >= :from"));
    }

    @Test
    void searchByFilters_WhenToProvided_ThenQueryContainsToCondition() {
        var to = OffsetDateTime.now();
        doReturn(List.of()).when(repository).list(anyString(), any(Map.class));

        repository.searchByFilters(null, null, null, null, null, null, null, to);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).list(captor.capture(), any(Map.class));
        assertTrue(captor.getValue().contains("archivedAt <= :to"));
    }

    @Test
    void searchByFilters_WhenMultipleFilters_ThenConditionsJoinedWithAnd() {
        doReturn(List.of()).when(repository).list(anyString(), any(Map.class));

        repository.searchByFilters("LOC", null, null, null, "SCD", null, null, null);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).list(captor.capture(), any(Map.class));
        assertTrue(captor.getValue().contains(" and "),
                "Multiple conditions must be joined with 'and'");
    }

    @Test
    void searchByFilters_WhenVoltageProvided_ThenListContainsVoltageCondition() {
        doReturn(List.of()).when(repository).list(anyString(), any(Map.class));

        repository.searchByFilters(null, null, null, null, null, "110kV", null, null);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).list(captor.capture(), any(Map.class));
        assertTrue(captor.getValue().contains("voltage = :voltage"));
    }

    @Test
    void searchByFilters_WhenContentTypeProvided_ThenListContainsContentTypeCondition() {
        doReturn(List.of()).when(repository).list(anyString(), any(Map.class));

        repository.searchByFilters(null, null, null, "application/xml", null, null, null, null);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).list(captor.capture(), any(Map.class));
        assertTrue(captor.getValue().contains("contentType = :contentType"));
    }
}
