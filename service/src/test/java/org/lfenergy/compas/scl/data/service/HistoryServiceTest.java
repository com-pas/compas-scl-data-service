// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.entities.HistorizedSclFile;
import org.lfenergy.compas.scl.data.entities.Location;
import org.lfenergy.compas.scl.data.entities.SclFile;
import org.lfenergy.compas.scl.data.entities.SclFileId;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.repository.HistorizedSclFileRepository;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourceSearch;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @Mock
    private HistorizedSclFileRepository historizedSclFileRepository;

    @Mock
    private CompasSclDataRepository compasSclDataRepository;

    private HistoryService historyService;

    @BeforeEach
    void setUp() {
        historyService = new HistoryService(historizedSclFileRepository, compasSclDataRepository);
    }

    // ---- helper methods ----------------------------------------------------

    private HistorizedSclFile buildEntry(UUID id, String type, String name, String version,
                                          boolean available, boolean isDeleted) {
        var sclFileId = new SclFileId();
        sclFileId.id = id;
        var parts = version.split("\\.");
        sclFileId.majorVersion = Short.parseShort(parts[0]);
        sclFileId.minorVersion = Short.parseShort(parts[1]);
        sclFileId.patchVersion = Short.parseShort(parts[2]);

        var sclFile = new SclFile();
        sclFile.id = sclFileId;
        sclFile.type = type;
        sclFile.name = name;
        sclFile.createdBy = "test-author";
        sclFile.isDeleted = isDeleted;

        var entry = new HistorizedSclFile();
        entry.id = UUID.randomUUID();
        entry.sclFile = sclFile;
        entry.available = available;
        entry.changedAt = OffsetDateTime.now();
        return entry;
    }

    // ---- retrieveDataResourceByVersion -------------------------------------

    @Test
    void retrieveDataResourceByVersion_WhenEntryExists_ThenReturnsTempFile() {
        var id = UUID.randomUUID();
        var version = "1.0.0";
        var entry = buildEntry(id, "SCD", "test-file", version, true, false);
        var sclContent = "<SCL/>";

        when(historizedSclFileRepository.findBySclFileIdAndVersion(id, version))
                .thenReturn(Optional.of(entry));
        when(compasSclDataRepository.findByUUID(any(), eq(id), any()))
                .thenReturn(sclContent);

        var result = historyService.retrieveDataResourceByVersion(id, version);

        assertNotNull(result);
        assertTrue(result.getName().contains("resource_" + id));
        assertTrue(result.getName().endsWith(".xml"));
        verify(historizedSclFileRepository).findBySclFileIdAndVersion(id, version);
        verify(compasSclDataRepository).findByUUID(any(), eq(id), any());
    }

    @Test
    void retrieveDataResourceByVersion_WhenEntryNotFound_ThenThrowsCompasNoDataFoundException() {
        var id = UUID.randomUUID();
        var version = "1.0.0";
        when(historizedSclFileRepository.findBySclFileIdAndVersion(id, version))
                .thenReturn(Optional.empty());

        assertThrows(CompasNoDataFoundException.class,
                () -> historyService.retrieveDataResourceByVersion(id, version));
        verify(compasSclDataRepository, never()).findByUUID(any(), any(), any());
    }

    @Test
    void retrieveDataResourceByVersion_WhenEntryExists_ThenFileContentMatchesSclContent() throws Exception {
        var id = UUID.randomUUID();
        var version = "2.1.0";
        var entry = buildEntry(id, "ICD", "icd-file", version, true, false);
        var sclContent = "<SCL>test content</SCL>";

        when(historizedSclFileRepository.findBySclFileIdAndVersion(id, version))
                .thenReturn(Optional.of(entry));
        when(compasSclDataRepository.findByUUID(any(), eq(id), any()))
                .thenReturn(sclContent);

        var result = historyService.retrieveDataResourceByVersion(id, version);

        assertTrue(result.exists());
        var fileContent = new String(java.nio.file.Files.readAllBytes(result.toPath()),
                java.nio.charset.StandardCharsets.UTF_8);
        assertEquals(sclContent, fileContent);
    }

    // ---- retrieveDataResourceHistory ---------------------------------------

    @Test
    void retrieveDataResourceHistory_WhenEntriesExist_ThenReturnsHistory() {
        var id = UUID.randomUUID();
        var entry1 = buildEntry(id, "SCD", "file", "1.0.0", true, false);
        var entry2 = buildEntry(id, "SCD", "file", "2.0.0", true, false);

        when(historizedSclFileRepository.findAllBySclFileId(id))
                .thenReturn(List.of(entry1, entry2));

        var result = historyService.retrieveDataResourceHistory(id);

        assertNotNull(result);
        assertNotNull(result.getVersions());
        assertEquals(2, result.getVersions().size());
    }

    @Test
    void retrieveDataResourceHistory_WhenNoEntriesFound_ThenThrowsCompasNoDataFoundException() {
        var id = UUID.randomUUID();
        when(historizedSclFileRepository.findAllBySclFileId(id)).thenReturn(List.of());

        assertThrows(CompasNoDataFoundException.class,
                () -> historyService.retrieveDataResourceHistory(id));
    }

    @Test
    void retrieveDataResourceHistory_WhenEntryHasLocation_ThenLocationIdIsIncluded() {
        var id = UUID.randomUUID();
        var locationId = UUID.randomUUID();
        var entry = buildEntry(id, "SCD", "file", "1.0.0", true, false);
        var location = new Location();
        location.id = locationId;
        entry.location = location;

        when(historizedSclFileRepository.findAllBySclFileId(id)).thenReturn(List.of(entry));

        var result = historyService.retrieveDataResourceHistory(id);

        assertEquals(locationId.toString(), result.getVersions().get(0).getLocation());
    }

    @Test
    void retrieveDataResourceHistory_WhenEntryHasNoLocation_ThenLocationIsNull() {
        var id = UUID.randomUUID();
        var entry = buildEntry(id, "SCD", "file", "1.0.0", true, false);
        entry.location = null;

        when(historizedSclFileRepository.findAllBySclFileId(id)).thenReturn(List.of(entry));

        var result = historyService.retrieveDataResourceHistory(id);

        assertNull(result.getVersions().get(0).getLocation());
    }

    @Test
    void retrieveDataResourceHistory_WhenEntryHasCommentAndArchivedFlag_ThenTheyAreMapped() {
        var id = UUID.randomUUID();
        var entry = buildEntry(id, "SCD", "file", "1.0.0", true, false);
        entry.comment = "my comment";
        entry.archived = true;

        when(historizedSclFileRepository.findAllBySclFileId(id)).thenReturn(List.of(entry));

        var result = historyService.retrieveDataResourceHistory(id);

        var version = result.getVersions().get(0);
        assertEquals("my comment", version.getComment());
        assertTrue(version.getArchived());
    }

    // ---- searchForResources ------------------------------------------------

    @Test
    void searchForResources_WhenUuidProvided_ThenSearchesByLatestForThatUuid() {
        var id = UUID.randomUUID();
        var entry = buildEntry(id, "SCD", "file", "1.0.0", true, false);
        var search = new DataResourceSearch().uuid(id.toString());

        when(historizedSclFileRepository.findLatestBySclFileId(id))
                .thenReturn(Optional.of(entry));

        var result = historyService.searchForResources(search);

        assertNotNull(result);
        assertEquals(1, result.getResults().size());
        verify(historizedSclFileRepository).findLatestBySclFileId(id);
        verify(historizedSclFileRepository, never()).searchLatest(any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchForResources_WhenUuidProvidedButNotFound_ThenReturnsEmptyResult() {
        var id = UUID.randomUUID();
        var search = new DataResourceSearch().uuid(id.toString());

        when(historizedSclFileRepository.findLatestBySclFileId(id))
                .thenReturn(Optional.empty());

        var result = historyService.searchForResources(search);

        assertNotNull(result);
        assertEquals(0, result.getResults().size());
    }

    @Test
    void searchForResources_WhenNoUuidProvided_ThenDelegatesToSearchLatest() {
        var id = UUID.randomUUID();
        var entry = buildEntry(id, "SCD", "file", "1.0.0", true, false);
        var search = new DataResourceSearch().type("SCD").name("file");

        when(historizedSclFileRepository.searchLatest(
                eq("SCD"), eq("file"), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(entry));

        var result = historyService.searchForResources(search);

        assertNotNull(result);
        assertEquals(1, result.getResults().size());
        verify(historizedSclFileRepository).searchLatest("SCD", "file", null, null, null, null);
        verify(historizedSclFileRepository, never()).findLatestBySclFileId(any());
    }

    @Test
    void searchForResources_WhenBlankUuidProvided_ThenDelegatesToSearchLatest() {
        var search = new DataResourceSearch().uuid("  ");

        when(historizedSclFileRepository.searchLatest(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        var result = historyService.searchForResources(search);

        assertNotNull(result);
        verify(historizedSclFileRepository).searchLatest(any(), any(), any(), any(), any(), any());
        verify(historizedSclFileRepository, never()).findLatestBySclFileId(any());
    }

    @Test
    void searchForResources_WhenEntryHasLocation_ThenLocationIsMappedInResult() {
        var id = UUID.randomUUID();
        var locationId = UUID.randomUUID();
        var entry = buildEntry(id, "SCD", "file", "1.0.0", true, false);
        var location = new Location();
        location.id = locationId;
        entry.location = location;
        var search = new DataResourceSearch();

        when(historizedSclFileRepository.searchLatest(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(entry));

        var result = historyService.searchForResources(search);

        assertEquals(locationId.toString(), result.getResults().get(0).getLocation());
    }

    @Test
    void searchForResources_WhenEntryHasNoLocation_ThenLocationIsNullInResult() {
        var id = UUID.randomUUID();
        var entry = buildEntry(id, "SCD", "file", "1.0.0", true, false);
        entry.location = null;
        var search = new DataResourceSearch();

        when(historizedSclFileRepository.searchLatest(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(entry));

        var result = historyService.searchForResources(search);

        assertNull(result.getResults().get(0).getLocation());
    }

    @Test
    void searchForResources_WhenDeletedEntryFound_ThenDeletedFlagIsMapped() {
        var id = UUID.randomUUID();
        var entry = buildEntry(id, "SCD", "file", "1.0.0", false, true);
        var search = new DataResourceSearch();

        when(historizedSclFileRepository.searchLatest(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(entry));

        var result = historyService.searchForResources(search);

        assertTrue(result.getResults().get(0).getDeleted());
        assertFalse(result.getResults().get(0).getAvailable());
    }
}
