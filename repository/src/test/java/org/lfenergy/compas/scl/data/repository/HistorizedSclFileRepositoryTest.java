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
        var queryMock = mock(org.hibernate.query.Query.class);
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(queryMock);

        repository.searchLatest(null, null, null, null, null, null, null);

        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(hqlCaptor.capture(), eq(HistorizedSclFile.class));
        var hql = hqlCaptor.getValue();
        assertFalse(hql.contains(":type"), "No type filter expected");
        assertFalse(hql.contains(":name"), "No name filter expected");
        assertFalse(hql.contains(":locationId"), "No locationId filter expected");
        assertFalse(hql.contains(":author"), "No author filter expected");
        assertFalse(hql.contains(":from"), "No from filter expected");
        assertFalse(hql.contains(":to"), "No to filter expected");
    }

    @Test
    void searchLatest_WhenBlankFiltersProvided_ThenNoFiltersApplied() {
        var queryMock = mock(org.hibernate.query.Query.class);
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(queryMock);

        repository.searchLatest("  ", "  ", "  ", "  ", "  ", null, null);

        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(hqlCaptor.capture(), eq(HistorizedSclFile.class));
        var hql = hqlCaptor.getValue();
        assertFalse(hql.contains(":type"));
        assertFalse(hql.contains(":name"));
        assertFalse(hql.contains(":locationId"));
        assertFalse(hql.contains(":author"));
    }

    @Test
    void searchLatest_WhenTypeFilterProvided_ThenTypeConditionIsAdded() {
        var queryMock = mock(org.hibernate.query.Query.class);
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(queryMock);

        repository.searchLatest(null, "SCD", null, null, null, null, null);

        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(hqlCaptor.capture(), eq(HistorizedSclFile.class));
        assertTrue(hqlCaptor.getValue().contains("r.sclFile.type = :type"));
        verify(queryMock).setParameter("type", "SCD");
    }

    @Test
    void searchLatest_WhenNameFilterProvided_ThenNameConditionIsAddedWithLowercase() {
        var queryMock = mock(org.hibernate.query.Query.class);
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(queryMock);

        repository.searchLatest(null, null, "Substation", null, null, null, null);

        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(hqlCaptor.capture(), eq(HistorizedSclFile.class));
        assertTrue(hqlCaptor.getValue().contains("lower(r.sclFile.name) like :name"));
        verify(queryMock).setParameter("name", "%substation%");
    }

    @Test
    void searchLatest_WhenLocationIdFilterProvided_ThenLocationConditionIsAdded() {
        var queryMock = mock(org.hibernate.query.Query.class);
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(queryMock);
        UUID locationId = UUID.randomUUID();

        repository.searchLatest(null, null, null, locationId.toString(), null, null, null);

        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(hqlCaptor.capture(), eq(HistorizedSclFile.class));
        assertTrue(hqlCaptor.getValue().contains("r.location.id = :locationId"));
        verify(queryMock).setParameter("locationId", locationId);
    }

    @Test
    void searchLatest_WhenAuthorFilterProvided_ThenAuthorConditionIsAddedWithLowercase() {
        var queryMock = mock(org.hibernate.query.Query.class);
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(queryMock);

        repository.searchLatest(null, null, null, null, "JohnDoe", null, null);

        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(hqlCaptor.capture(), eq(HistorizedSclFile.class));
        assertTrue(hqlCaptor.getValue().contains("lower(r.sclFile.createdBy) like :author"));
        verify(queryMock).setParameter("author", "%johndoe%");
    }

    @Test
    void searchLatest_WhenFromDateProvided_ThenFromConditionIsAdded() {
        var queryMock = mock(org.hibernate.query.Query.class);
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(queryMock);
        OffsetDateTime from = OffsetDateTime.now().minusDays(1);

        repository.searchLatest(null, null, null, null, null, from, null);

        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(hqlCaptor.capture(), eq(HistorizedSclFile.class));
        assertTrue(hqlCaptor.getValue().contains("r.changedAt >= :from"));
        verify(queryMock).setParameter("from", from);
    }

    @Test
    void searchLatest_WhenToDateProvided_ThenToConditionIsAdded() {
        var queryMock = mock(org.hibernate.query.Query.class);
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(queryMock);
        OffsetDateTime to = OffsetDateTime.now();

        repository.searchLatest(null, null, null, null, null, null, to);

        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(hqlCaptor.capture(), eq(HistorizedSclFile.class));
        assertTrue(hqlCaptor.getValue().contains("r.changedAt <= :to"));
        verify(queryMock).setParameter("to", to);
    }

    @Test
    void searchLatest_WhenAllFiltersProvided_ThenAllConditionsAddedAndParametersSet() {
        var queryMock = mock(org.hibernate.query.Query.class);
        when(session.createQuery(anyString(), eq(HistorizedSclFile.class))).thenReturn(queryMock);

        UUID locationId = UUID.randomUUID();
        OffsetDateTime from = OffsetDateTime.now().minusDays(1);
        OffsetDateTime to = OffsetDateTime.now();

        repository.searchLatest("1234", "SCD", "Substation", locationId.toString(), "JohnDoe", from, to);

        ArgumentCaptor<String> hqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(session).createQuery(hqlCaptor.capture(), eq(HistorizedSclFile.class));
        String capturedHql = hqlCaptor.getValue();

        assertTrue(capturedHql.contains("cast(r.sclFile.id.id as string) like :uuid"));
        assertTrue(capturedHql.contains("r.sclFile.type = :type"));
        assertTrue(capturedHql.contains("lower(r.sclFile.name) like :name"));
        assertTrue(capturedHql.contains("r.location.id = :locationId"));
        assertTrue(capturedHql.contains("lower(r.sclFile.createdBy) like :author"));
        assertTrue(capturedHql.contains("r.changedAt >= :from"));
        assertTrue(capturedHql.contains("r.changedAt <= :to"));

        verify(queryMock).setParameter("uuid", "%1234%");
        verify(queryMock).setParameter("type", "SCD");
        verify(queryMock).setParameter("name", "%substation%");
        verify(queryMock).setParameter("locationId", locationId);
        verify(queryMock).setParameter("author", "%johndoe%");
        verify(queryMock).setParameter("from", from);
        verify(queryMock).setParameter("to", to);
    }
}
