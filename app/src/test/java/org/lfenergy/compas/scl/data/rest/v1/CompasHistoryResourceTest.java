// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourceHistory;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourceSearch;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourcesResult;
import org.lfenergy.compas.scl.data.service.HistoryService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasHistoryResourceTest {

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private CompasHistoryResource resource;

    @Test
    void retrieveDataResourceByVersion_WhenCalled_ThenDelegatesToServiceAndReturnsUni() throws Exception {
        var id = UUID.randomUUID();
        var version = "1.0.0";
        var tempFile = File.createTempFile("resource_" + id, ".xml");
        tempFile.deleteOnExit();
        when(historyService.retrieveDataResourceByVersion(id, version)).thenReturn(tempFile);

        var uni = resource.retrieveDataResourceByVersion(id, version);

        assertNotNull(uni);
        assertSame(tempFile, uni.await().indefinitely());
        verify(historyService).retrieveDataResourceByVersion(id, version);
    }

    @Test
    void retrieveDataResourceHistory_WhenCalled_ThenDelegatesToServiceAndReturnsUni() {
        var id = UUID.randomUUID();
        var expected = new DataResourceHistory();
        when(historyService.retrieveDataResourceHistory(id)).thenReturn(expected);

        var uni = resource.retrieveDataResourceHistory(id);

        assertNotNull(uni);
        assertSame(expected, uni.await().indefinitely());
        verify(historyService).retrieveDataResourceHistory(id);
    }

    @Test
    void searchForResources_WhenCalled_ThenDelegatesToServiceAndReturnsUni() {
        var search = new DataResourceSearch().type("SCD").name("test");
        var expected = new DataResourcesResult();
        when(historyService.searchForResources(search)).thenReturn(expected);

        var uni = resource.searchForResources(search);

        assertNotNull(uni);
        assertSame(expected, uni.await().indefinitely());
        verify(historyService).searchForResources(search);
    }

    @Test
    void retrieveDataResourceByVersion_WhenServiceThrowsException_ThenUniPropagatesException() {
        var id = UUID.randomUUID();
        when(historyService.retrieveDataResourceByVersion(id, "1.0.0"))
                .thenThrow(new RuntimeException("not found"));

        var uni = resource.retrieveDataResourceByVersion(id, "1.0.0");

        assertThrows(RuntimeException.class, () -> uni.await().indefinitely());
    }

    @Test
    void retrieveDataResourceHistory_WhenServiceThrowsException_ThenUniPropagatesException() {
        var id = UUID.randomUUID();
        when(historyService.retrieveDataResourceHistory(id))
                .thenThrow(new RuntimeException("history error"));

        var uni = resource.retrieveDataResourceHistory(id);

        assertThrows(RuntimeException.class, () -> uni.await().indefinitely());
    }

    @Test
    void searchForResources_WhenSearchHasNoResults_ThenEmptyResultIsReturned() {
        var search = new DataResourceSearch();
        when(historyService.searchForResources(search))
                .thenReturn(new DataResourcesResult().results(List.of()));

        var uni = resource.searchForResources(search);

        var result = uni.await().indefinitely();
        assertNotNull(result);
        assertTrue(result.getResults().isEmpty());
    }
}
