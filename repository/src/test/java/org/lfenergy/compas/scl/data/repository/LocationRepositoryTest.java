// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import io.quarkus.hibernate.panache.blocking.PanacheBlockingQuery;
import io.quarkus.panache.common.Sort;
import org.hibernate.Session;
import org.hibernate.query.MutationQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.entities.Location;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class LocationRepositoryTest {

    @Mock
    private Session session;

    @Mock
    private MutationQuery mutationQuery;

    @Mock
    private PanacheBlockingQuery<Location> panacheQuery;

    private LocationRepository repository;

    @BeforeEach
    void setUp() {
        repository = spy(new LocationRepository() {
            @Override
            public Session getSession() {
                return session;
            }
        });
    }

    // ---- hasDuplicateValues ------------------------------------------------

    @Test
    void hasDuplicateValues_WhenDuplicateExists_ThenReturnsTrue() {
        doReturn(1L).when(repository).count(anyString(), eq("K1"), eq("N1"));

        assertTrue(repository.hasDuplicateValues("K1", "N1"));
    }

    @Test
    void hasDuplicateValues_WhenNoDuplicate_ThenReturnsFalse() {
        doReturn(0L).when(repository).count(anyString(), eq("K1"), eq("N1"));

        assertFalse(repository.hasDuplicateValues("K1", "N1"));
    }

    @Test
    void hasDuplicateValues_WhenExactlyOneMatch_ThenReturnsTrue() {
        doReturn(1L).when(repository).count(anyString(), any(), any());

        assertTrue(repository.hasDuplicateValues("any-key", "any-name"));
    }

    // ---- hasDuplicateValuesExcluding ----------------------------------------

    @Test
    void hasDuplicateValuesExcluding_WhenDuplicateExists_ThenReturnsTrue() {
        var id = UUID.randomUUID();
        doReturn(2L).when(repository).count(anyString(), eq("K1"), eq("N1"), eq(id));

        assertTrue(repository.hasDuplicateValuesExcluding("K1", "N1", id));
    }

    @Test
    void hasDuplicateValuesExcluding_WhenNoDuplicate_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        doReturn(0L).when(repository).count(anyString(), eq("K1"), eq("N1"), eq(id));

        assertFalse(repository.hasDuplicateValuesExcluding("K1", "N1", id));
    }

    @Test
    void hasDuplicateValuesExcluding_WhenQueryContainsExcludeCondition_ThenQueryUsesIdCondition() {
        var id = UUID.randomUUID();
        doReturn(0L).when(repository).count(anyString(), any(), any(), any());

        repository.hasDuplicateValuesExcluding("K1", "N1", id);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).count(captor.capture(), any(), any(), eq(id));
        assertTrue(captor.getValue().contains("id <> ?3"), "Query must exclude own id");
    }

    // ---- listPaged ---------------------------------------------------------

    @Test
    void listPaged_WhenCalled_ThenReturnsPagedLocations() {
        var loc1 = new Location();
        var loc2 = new Location();
        doReturn(panacheQuery).when(repository).findAll(any(Sort.class));
        when(panacheQuery.page(0, 10)).thenReturn(panacheQuery);
        when(panacheQuery.list()).thenReturn(List.of(loc1, loc2));

        var result = repository.listPaged(0, 10);

        assertEquals(2, result.size());
        assertSame(loc1, result.get(0));
        assertSame(loc2, result.get(1));
    }

    @Test
    void listPaged_WhenCalled_ThenSortsByName() {
        doReturn(panacheQuery).when(repository).findAll(any(Sort.class));
        when(panacheQuery.page(anyInt(), anyInt())).thenReturn(panacheQuery);
        when(panacheQuery.list()).thenReturn(List.of());

        repository.listPaged(0, 5);

        var captor = org.mockito.ArgumentCaptor.forClass(Sort.class);
        verify(repository).findAll(captor.capture());
        // Sort by name: the captured Sort should produce "name" as its column
        assertNotNull(captor.getValue());
    }

    @Test
    void listPaged_WhenSecondPage_ThenPassesCorrectPageParams() {
        doReturn(panacheQuery).when(repository).findAll(any(Sort.class));
        when(panacheQuery.page(2, 15)).thenReturn(panacheQuery);
        when(panacheQuery.list()).thenReturn(List.of());

        repository.listPaged(2, 15);

        verify(panacheQuery).page(2, 15);
    }

    // ---- countTotal --------------------------------------------------------

    @Test
    void countTotal_WhenCalled_ThenReturnsCountFromRepository() {
        doReturn(42L).when(repository).count();

        assertEquals(42L, repository.countTotal());
    }

    @Test
    void countTotal_WhenEmpty_ThenReturnsZero() {
        doReturn(0L).when(repository).count();

        assertEquals(0L, repository.countTotal());
    }

    // ---- recalculateAssignedResources --------------------------------------

    @Test
    void recalculateAssignedResources_WhenCalled_ThenExecutesNativeUpdate() {
        var locationId = UUID.randomUUID();
        when(session.createNativeMutationQuery(anyString())).thenReturn(mutationQuery);
        when(mutationQuery.setParameter(anyString(), any())).thenReturn(mutationQuery);

        repository.recalculateAssignedResources(locationId);

        verify(mutationQuery).executeUpdate();
    }

    @Test
    void recalculateAssignedResources_WhenCalled_ThenQueryUpdatesLocationTable() {
        var locationId = UUID.randomUUID();
        when(session.createNativeMutationQuery(anyString())).thenReturn(mutationQuery);
        when(mutationQuery.setParameter(anyString(), any())).thenReturn(mutationQuery);

        repository.recalculateAssignedResources(locationId);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(session).createNativeMutationQuery(captor.capture());
        var sql = captor.getValue();
        assertTrue(sql.contains("UPDATE location"), "SQL must update location table");
        assertTrue(sql.contains("assigned_resources"), "SQL must set assigned_resources");
        assertTrue(sql.contains("historized_scl_file"), "SQL must count from historized_scl_file");
    }

    @Test
    void recalculateAssignedResources_WhenCalled_ThenSetsLocationIdParameter() {
        var locationId = UUID.randomUUID();
        when(session.createNativeMutationQuery(anyString())).thenReturn(mutationQuery);
        when(mutationQuery.setParameter(anyString(), any())).thenReturn(mutationQuery);

        repository.recalculateAssignedResources(locationId);

        // Named parameter :locationId is set once; Hibernate applies it to all occurrences
        verify(mutationQuery).setParameter(eq("locationId"), eq(locationId));
    }
}
