// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResource;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResources;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResourcesHistory;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResourcesSearch;
import org.lfenergy.compas.scl.data.service.ArchivingService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasArchivingResourceTest {

    @Mock
    private ArchivingService archivingService;

    @InjectMocks
    private CompasArchivingResource resource;

    @Test
    void archiveResource_WhenCalled_ThenDelegatesToServiceAndReturnsUni() throws IOException {
        var id = UUID.randomUUID();
        var version = "1.0.0";
        var body = File.createTempFile("test", ".xml");
        body.deleteOnExit();
        var expected = new ArchivedResource();
        when(archivingService.archiveResource(id, version, "author", "approver",
                "application/xml", "file.xml", body)).thenReturn(expected);

        var uni = resource.archiveResource(id, version, "author", "approver",
                "application/xml", "file.xml", body);

        assertNotNull(uni);
        assertSame(expected, uni.await().indefinitely());
        verify(archivingService).archiveResource(id, version, "author", "approver",
                "application/xml", "file.xml", body);
    }

    @Test
    void archiveSclResource_WhenCalled_ThenDelegatesToServiceAndReturnsUni() {
        var id = UUID.randomUUID();
        var version = "2.1.0";
        var expected = new ArchivedResource();
        when(archivingService.archiveSclResource(id, version)).thenReturn(expected);

        var uni = resource.archiveSclResource(id, version);

        assertNotNull(uni);
        assertSame(expected, uni.await().indefinitely());
        verify(archivingService).archiveSclResource(id, version);
    }

    @Test
    void retrieveArchivedResourceHistory_WhenCalled_ThenDelegatesToServiceAndReturnsUni() {
        var id = UUID.randomUUID();
        var expected = new ArchivedResourcesHistory();
        when(archivingService.retrieveArchivedResourceHistory(id)).thenReturn(expected);

        var uni = resource.retrieveArchivedResourceHistory(id);

        assertNotNull(uni);
        assertSame(expected, uni.await().indefinitely());
        verify(archivingService).retrieveArchivedResourceHistory(id);
    }

    @Test
    void searchArchivedResources_WhenCalled_ThenDelegatesToServiceAndReturnsUni() {
        var search = new ArchivedResourcesSearch();
        var expected = new ArchivedResources();
        when(archivingService.searchArchivedResources(search)).thenReturn(expected);

        var uni = resource.searchArchivedResources(search);

        assertNotNull(uni);
        assertSame(expected, uni.await().indefinitely());
        verify(archivingService).searchArchivedResources(search);
    }

    @Test
    void archiveResource_WhenServiceThrowsException_ThenUniPropagatesException() throws IOException {
        var id = UUID.randomUUID();
        var body = File.createTempFile("test", ".xml");
        body.deleteOnExit();
        when(archivingService.archiveResource(any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("archive failed"));

        var uni = resource.archiveResource(id, "1.0.0", "author", "approver",
                "application/xml", "file.xml", body);

        assertThrows(RuntimeException.class, () -> uni.await().indefinitely());
    }
}
