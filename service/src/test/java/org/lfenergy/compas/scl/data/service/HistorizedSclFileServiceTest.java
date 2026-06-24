// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.model.IAbstractItem;
import org.lfenergy.compas.scl.data.model.IHistoryItem;
import org.lfenergy.compas.scl.data.model.IItem;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.repository.HistorizedSclFileRepository;
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistorizedSclFileServiceTest {

    @Mock
    private CompasSclDataRepository repository;

    @Mock
    private HistorizedSclFileRepository historizedSclFileRepository;

    private HistorizedSclFileService service;

    @BeforeEach
    void setUp() {
        service = new HistorizedSclFileService(repository, historizedSclFileRepository);
    }

    // ---- insertSclFileWithHistory ------------------------------------------

    @Test
    void insertSclFileWithHistory_WhenCalled_ThenCallsRepositoryCreateWithAllArgs() {
        var id = UUID.randomUUID();
        var version = new Version(1, 0, 0);
        var labels = List.of("label1", "label2");

        service.insertSclFileWithHistory(SclFileType.SCD, id, "test-file", "<SCL/>",
                version, "alice", labels, "initial", "test-file.scd");

        verify(repository).create(SclFileType.SCD, id, "test-file", "<SCL/>", version, "alice", labels);
    }

    @Test
    void insertSclFileWithHistory_WhenCalled_ThenCallsHistorizedRepositoryCreateEntry() {
        var id = UUID.randomUUID();
        var version = new Version(1, 2, 3);

        service.insertSclFileWithHistory(SclFileType.CID, id, "cid-file", "<SCL/>",
                version, "bob", List.of(), "my comment", "cid-file.cid");

        verify(historizedSclFileRepository).createEntry(id, version, "application/xml", "cid-file.cid", "my comment");
    }

    @Test
    void insertSclFileWithHistory_WhenWhoIsNull_ThenUsesUnknownAsAuthor() {
        var id = UUID.randomUUID();
        var version = new Version(1, 0, 0);

        service.insertSclFileWithHistory(SclFileType.SCD, id, "file", "<SCL/>",
                version, null, List.of(), "comment", "file.scd");

        verify(repository).create(eq(SclFileType.SCD), eq(id), eq("file"), eq("<SCL/>"),
                eq(version), eq("Unknown"), any());
    }

    @Test
    void insertSclFileWithHistory_WhenWhoIsProvided_ThenUsesProvidedAuthor() {
        var id = UUID.randomUUID();
        var version = new Version(1, 0, 0);

        service.insertSclFileWithHistory(SclFileType.SCD, id, "file", "<SCL/>",
                version, "charlie", List.of(), null, "file.scd");

        var captor = ArgumentCaptor.forClass(String.class);
        verify(repository).create(eq(SclFileType.SCD), eq(id), eq("file"), eq("<SCL/>"),
                eq(version), captor.capture(), any());
        assertEquals("charlie", captor.getValue());
    }

    // ---- list --------------------------------------------------------------

    @Test
    void list_WhenCalled_ThenDelegatesToRepository() {
        var item = mock(IItem.class);
        when(repository.list(SclFileType.SCD)).thenReturn(List.of(item));

        var result = service.list(SclFileType.SCD);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(item, result.get(0));
        verify(repository).list(SclFileType.SCD);
    }

    @Test
    void list_WhenRepositoryReturnsEmpty_ThenReturnsEmpty() {
        when(repository.list(SclFileType.CID)).thenReturn(List.of());

        var result = service.list(SclFileType.CID);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ---- listVersionsByUUID ------------------------------------------------

    @Test
    void listVersionsByUUID_WhenCalled_ThenDelegatesToRepository() {
        var id = UUID.randomUUID();
        var histItem = mock(IHistoryItem.class);
        when(repository.listVersionsByUUID(SclFileType.SCD, id)).thenReturn(List.of(histItem));

        var result = service.listVersionsByUUID(SclFileType.SCD, id);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(histItem, result.get(0));
        verify(repository).listVersionsByUUID(SclFileType.SCD, id);
    }

    // ---- findByUUID (latest) -----------------------------------------------

    @Test
    void findByUUID_WhenCalled_ThenDelegatesToRepository() {
        var id = UUID.randomUUID();
        when(repository.findByUUID(SclFileType.SCD, id)).thenReturn("<SCL/>");

        var result = service.findByUUID(SclFileType.SCD, id);

        assertEquals("<SCL/>", result);
        verify(repository).findByUUID(SclFileType.SCD, id);
    }

    // ---- findMetaInfoByUUID ------------------------------------------------

    @Test
    void findMetaInfoByUUID_WhenCalled_ThenDelegatesToRepository() {
        var id = UUID.randomUUID();
        var meta = mock(IAbstractItem.class);
        when(repository.findMetaInfoByUUID(SclFileType.SCD, id)).thenReturn(meta);

        var result = service.findMetaInfoByUUID(SclFileType.SCD, id);

        assertSame(meta, result);
        verify(repository).findMetaInfoByUUID(SclFileType.SCD, id);
    }

    // ---- findByUUID (versioned) --------------------------------------------

    @Test
    void findByUUID_WithVersion_WhenCalled_ThenDelegatesToRepository() {
        var id = UUID.randomUUID();
        var version = new Version(1, 2, 3);
        when(repository.findByUUID(SclFileType.SCD, id, version)).thenReturn("<SCL/>");

        var result = service.findByUUID(SclFileType.SCD, id, version);

        assertEquals("<SCL/>", result);
        verify(repository).findByUUID(SclFileType.SCD, id, version);
    }

    // ---- hasDuplicateSclName -----------------------------------------------

    @Test
    void hasDuplicateSclName_WhenDuplicateExists_ThenReturnsTrue() {
        when(repository.hasDuplicateSclName(SclFileType.SCD, "my-file")).thenReturn(true);

        assertTrue(service.hasDuplicateSclName(SclFileType.SCD, "my-file"));
    }

    @Test
    void hasDuplicateSclName_WhenNoDuplicate_ThenReturnsFalse() {
        when(repository.hasDuplicateSclName(SclFileType.SCD, "unique-file")).thenReturn(false);

        assertFalse(service.hasDuplicateSclName(SclFileType.SCD, "unique-file"));
    }

    // ---- create ------------------------------------------------------------

    @Test
    void create_WhenCalled_ThenDelegatesToRepository() {
        var id = UUID.randomUUID();
        var version = new Version(1, 0, 0);
        var labels = List.of("tag1");

        service.create(SclFileType.SCD, id, "name", "<SCL/>", version, "user", labels);

        verify(repository).create(SclFileType.SCD, id, "name", "<SCL/>", version, "user", labels);
    }

    // ---- delete (by id) ----------------------------------------------------

    @Test
    void delete_WhenCalledWithId_ThenDelegatesToRepository() {
        var id = UUID.randomUUID();

        service.delete(SclFileType.SCD, id);

        verify(repository).delete(SclFileType.SCD, id);
    }

    // ---- delete (by id and version) ----------------------------------------

    @Test
    void delete_WhenCalledWithIdAndVersion_ThenDelegatesToRepository() {
        var id = UUID.randomUUID();
        var version = new Version(1, 0, 0);

        service.delete(SclFileType.SCD, id, version);

        verify(repository).delete(SclFileType.SCD, id, version);
    }
}
