// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.entities.HistorizedSclFile;
import org.lfenergy.compas.scl.data.entities.Location;
import org.lfenergy.compas.scl.data.entities.SclFile;
import org.lfenergy.compas.scl.data.entities.SclFileId;
import org.lfenergy.compas.scl.data.model.Version;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class HistorizedSclFileRepositoryTest {

    @Mock
    private Session session;

    @Mock
    private Query<HistorizedSclFile> query;

    private HistorizedSclFileRepository repository;

    @BeforeEach
    void setUp() {
        // Create a testable subclass that overrides getSession() since Quarkus/Panache
        // is not available in plain unit tests.
        repository = spy(new HistorizedSclFileRepository() {
            @Override
            public Session getSession() {
                return session;
            }
        });
    }

    // ---- createEntry -------------------------------------------------------

    @Test
    void createEntry_WhenCalled_ThenPersistsHistorizedSclFileWithCorrectFields() {
        var sclFileId = UUID.randomUUID();
        var version = new Version(1, 2, 3);
        var sclFileProxy = new SclFile();
        when(session.getReference(eq(SclFile.class), any(SclFileId.class))).thenReturn(sclFileProxy);
        doNothing().when(repository).persist(any(HistorizedSclFile.class));

        repository.createEntry(sclFileId, version, "application/xml", "file.scd", "initial comment");

        var captor = ArgumentCaptor.forClass(HistorizedSclFile.class);
        verify(repository).persist(captor.capture());
        var persisted = captor.getValue();
        assertSame(sclFileProxy, persisted.sclFile);
        assertEquals("application/xml", persisted.contentType);
        assertEquals("file.scd", persisted.filename);
        assertEquals("initial comment", persisted.comment);
    }

    @Test
    void createEntry_WhenCalled_ThenSclFileIdIsBuiltFromVersionComponents() {
        var sclFileId = UUID.randomUUID();
        var version = new Version(2, 5, 7);
        when(session.getReference(eq(SclFile.class), any(SclFileId.class))).thenReturn(new SclFile());
        doNothing().when(repository).persist(any(HistorizedSclFile.class));

        repository.createEntry(sclFileId, version, "application/xml", "file.scd", null);

        var keyCaptor = ArgumentCaptor.forClass(SclFileId.class);
        verify(session).getReference(eq(SclFile.class), keyCaptor.capture());
        var key = keyCaptor.getValue();
        assertEquals(sclFileId, key.id);
        assertEquals((short) 2, key.majorVersion);
        assertEquals((short) 5, key.minorVersion);
        assertEquals((short) 7, key.patchVersion);
    }

    // ---- findAllLatest -----------------------------------------------------

    @Test
    void findAllLatest_WhenCalled_ThenReturnsQueryResults() {
        var entry = new HistorizedSclFile();
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(entry));

        var result = repository.findAllLatest();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(entry, result.get(0));
    }

    @Test
    void findAllLatest_WhenCalled_ThenQueryContainsMaxChangedAtSubquery() {
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        repository.findAllLatest();

        var captor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(captor.capture(), eq(HistorizedSclFile.class));
        var hql = captor.getValue();
        assertTrue(hql.contains("from HistorizedSclFile r"), "Query should select HistorizedSclFile");
        assertTrue(hql.contains("max(r2.changedAt)"), "Query should use max(changedAt) for latest");
    }

    // ---- searchLatest - query building logic --------------------------------

    @Test
    void searchLatest_WhenNoFiltersProvided_ThenOnlyBaseQueryIsBuilt() {
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        repository.searchLatest(null, null, null, null, null, null);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(captor.capture(), eq(HistorizedSclFile.class));
        var hql = captor.getValue();
        assertFalse(hql.contains(":type"), "No type filter expected");
        assertFalse(hql.contains(":name"), "No name filter expected");
        assertFalse(hql.contains(":locationId"), "No locationId filter expected");
        assertFalse(hql.contains(":author"), "No author filter expected");
        assertFalse(hql.contains(":from"), "No from filter expected");
        assertFalse(hql.contains(":to"), "No to filter expected");
    }

    @Test
    void searchLatest_WhenBlankFiltersProvided_ThenNoFiltersApplied() {
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        repository.searchLatest("  ", "  ", "  ", "  ", null, null);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(captor.capture(), eq(HistorizedSclFile.class));
        var hql = captor.getValue();
        assertFalse(hql.contains(":type"));
        assertFalse(hql.contains(":name"));
        assertFalse(hql.contains(":locationId"));
        assertFalse(hql.contains(":author"));
    }

    @Test
    void searchLatest_WhenTypeFilterProvided_ThenTypeConditionIsAdded() {
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        repository.searchLatest("SCD", null, null, null, null, null);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(captor.capture(), eq(HistorizedSclFile.class));
        assertTrue(captor.getValue().contains("r.sclFile.type = :type"));
        verify(query).setParameter("type", "SCD");
    }

    @Test
    void searchLatest_WhenNameFilterProvided_ThenNameConditionIsAddedWithLowercase() {
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        repository.searchLatest(null, "Substation", null, null, null, null);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(captor.capture(), eq(HistorizedSclFile.class));
        assertTrue(captor.getValue().contains("lower(r.sclFile.name) like :name"));
        verify(query).setParameter("name", "%substation%");
    }

    @Test
    void searchLatest_WhenLocationIdFilterProvided_ThenLocationConditionIsAdded() {
        var locationId = UUID.randomUUID();
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        repository.searchLatest(null, null, locationId.toString(), null, null, null);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(captor.capture(), eq(HistorizedSclFile.class));
        assertTrue(captor.getValue().contains("r.location.id = :locationId"));
        verify(query).setParameter("locationId", locationId);
    }

    @Test
    void searchLatest_WhenAuthorFilterProvided_ThenAuthorConditionIsAddedWithLowercase() {
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        repository.searchLatest(null, null, null, "Alice", null, null);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(captor.capture(), eq(HistorizedSclFile.class));
        assertTrue(captor.getValue().contains("lower(r.author) like :author"));
        verify(query).setParameter("author", "%alice%");
    }

    @Test
    void searchLatest_WhenFromFilterProvided_ThenFromConditionIsAdded() {
        var from = OffsetDateTime.now().minusDays(7);
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        repository.searchLatest(null, null, null, null, from, null);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(captor.capture(), eq(HistorizedSclFile.class));
        assertTrue(captor.getValue().contains("r.changedAt >= :from"));
        verify(query).setParameter("from", from);
    }

    @Test
    void searchLatest_WhenToFilterProvided_ThenToConditionIsAdded() {
        var to = OffsetDateTime.now();
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        repository.searchLatest(null, null, null, null, null, to);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(captor.capture(), eq(HistorizedSclFile.class));
        assertTrue(captor.getValue().contains("r.changedAt <= :to"));
        verify(query).setParameter("to", to);
    }

    @Test
    void searchLatest_WhenAllFiltersProvided_ThenAllConditionsAreAdded() {
        var locationId = UUID.randomUUID();
        var from = OffsetDateTime.now().minusDays(7);
        var to = OffsetDateTime.now();
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        repository.searchLatest("SCD", "sub", locationId.toString(), "alice", from, to);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(captor.capture(), eq(HistorizedSclFile.class));
        var hql = captor.getValue();
        assertTrue(hql.contains(":type"));
        assertTrue(hql.contains(":name"));
        assertTrue(hql.contains(":locationId"));
        assertTrue(hql.contains(":author"));
        assertTrue(hql.contains(":from"));
        assertTrue(hql.contains(":to"));
    }

    @Test
    void searchLatest_WhenCalled_ThenQueryEndsWithOrderByName() {
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        repository.searchLatest(null, null, null, null, null, null);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(captor.capture(), eq(HistorizedSclFile.class));
        assertTrue(captor.getValue().trim().endsWith("order by r.sclFile.name"));
    }

    // ---- delegation methods ------------------------------------------------

    @Test
    void findAllBySclFileId_WhenCalled_ThenDelegatesToPanacheListWithCorrectQuery() {
        var sclFileId = UUID.randomUUID();
        var entry = new HistorizedSclFile();
        doReturn(List.of(entry)).when(repository).list(anyString(), eq(sclFileId));

        var result = repository.findAllBySclFileId(sclFileId);

        assertEquals(1, result.size());
        assertSame(entry, result.get(0));
        verify(repository).list(contains("sclFile.id.id = ?1"), eq(sclFileId));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findLatestBySclFileId_WhenCalled_ThenDelegatesToPanacheFindWithCorrectQuery() {
        var sclFileId = UUID.randomUUID();
        var panacheQuery = mock(io.quarkus.hibernate.panache.blocking.PanacheBlockingQuery.class);
        var entry = new HistorizedSclFile();
        doReturn(panacheQuery).when(repository).find(anyString(), eq(sclFileId));
        when(panacheQuery.firstResultOptional()).thenReturn(Optional.of(entry));

        var result = repository.findLatestBySclFileId(sclFileId);

        assertTrue(result.isPresent());
        assertSame(entry, result.get());
    }

    @Test
    void countBySclFileId_WhenCalled_ThenDelegatesToPanacheCount() {
        var sclFileId = UUID.randomUUID();
        doReturn(3L).when(repository).count(anyString(), eq(sclFileId));

        var result = repository.countBySclFileId(sclFileId);

        assertEquals(3L, result);
    }

    @Test
    void assignToLocation_WhenCalled_ThenDelegatesToPanacheUpdate() {
        var sclFileId = UUID.randomUUID();
        var location = new Location();
        location.id = UUID.randomUUID();
        doReturn(1L).when(repository).update(anyString(), eq(location), eq(sclFileId));

        repository.assignToLocation(sclFileId, location);

        verify(repository).update(contains("location = ?1"), eq(location), eq(sclFileId));
    }

    @Test
    void unassignFromLocation_WhenCalled_ThenDelegatesToPanacheUpdate() {
        var sclFileId = UUID.randomUUID();
        var locationId = UUID.randomUUID();
        doReturn(1L).when(repository).update(anyString(), eq(sclFileId), eq(locationId));

        repository.unassignFromLocation(sclFileId, locationId);

        verify(repository).update(contains("location = null"), eq(sclFileId), eq(locationId));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findBySclFileIdAndVersion_WhenCalled_ThenDelegatesToPanacheFindWithVersionComponents() {
        var sclFileId = UUID.randomUUID();
        var version = "2.3.4";
        var panacheQuery = mock(io.quarkus.hibernate.panache.blocking.PanacheBlockingQuery.class);
        when(panacheQuery.firstResultOptional()).thenReturn(Optional.empty());
        doReturn(panacheQuery).when(repository).find(anyString(),
                eq(sclFileId), eq((short) 2), eq((short) 3), eq((short) 4));

        var result = repository.findBySclFileIdAndVersion(sclFileId, version);

        assertFalse(result.isPresent());
    }
}
