// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.entities.HistorizedSclFile;
import org.lfenergy.compas.scl.data.entities.Location;
import org.lfenergy.compas.scl.data.entities.ResourceTag;
import org.lfenergy.compas.scl.data.entities.SclFile;
import org.lfenergy.compas.scl.data.entities.SclFileId;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.repository.ArchivedResourceRepository;
import org.lfenergy.compas.scl.data.repository.HistorizedSclFileRepository;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResourcesSearch;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArchivingServiceTest {

    @Mock
    private ArchivedResourceRepository archiveRepository;

    @Mock
    private HistorizedSclFileRepository historizedSclFileRepository;

    private ArchivingService service;

    @BeforeEach
    void setUp() {
        service = new ArchivingService(archiveRepository, historizedSclFileRepository);
    }

    // ---- helpers -----------------------------------------------------------

    private HistorizedSclFile buildHistorizedSclFile(UUID resourceId, Location location) {
        var sclFileId = new SclFileId();
        sclFileId.id = resourceId;
        sclFileId.majorVersion = 1;
        sclFileId.minorVersion = 0;
        sclFileId.patchVersion = 0;

        var sclFile = new SclFile();
        sclFile.id = sclFileId;
        sclFile.name = "test-resource.scd";
        sclFile.type = "SCD";
        sclFile.createdBy = "alice";

        var hSclFile = new HistorizedSclFile();
        hSclFile.id = UUID.randomUUID();
        hSclFile.sclFile = sclFile;
        hSclFile.location = location;
        hSclFile.changedAt = OffsetDateTime.now();
        return hSclFile;
    }

    private Location buildLocation() {
        var loc = new Location();
        loc.id = UUID.randomUUID();
        loc.key = "LOC-01";
        loc.name = "Primary Substation";
        return loc;
    }

    private org.lfenergy.compas.scl.data.entities.ArchivedResource buildArchivedEntity(UUID resourceId) {
        var e = new org.lfenergy.compas.scl.data.entities.ArchivedResource();
        e.id = UUID.randomUUID();
        e.resourceId = resourceId;
        e.name = "test-resource.scd";
        e.version = "1.0.0";
        e.location = "LOC-01";
        e.locationId = UUID.randomUUID().toString();
        e.author = "alice";
        e.type = "SCD";
        e.contentType = "application/xml";
        e.modifiedAt = OffsetDateTime.now().minusDays(1);
        e.archivedAt = OffsetDateTime.now();
        return e;
    }

    // ---- archiveResource ---------------------------------------------------

    @Test
    void archiveResource_WhenSclFileNotFound_ThenThrowsCompasNoDataFoundException() {
        var id = UUID.randomUUID();
        when(historizedSclFileRepository.findBySclFileIdAndVersion(id, "1.0.0"))
                .thenReturn(Optional.empty());

        assertThrows(CompasNoDataFoundException.class,
                () -> service.archiveResource(id, "1.0.0", "alice", "bob",
                        "application/xml", "file.scd", null));
    }

    @Test
    void archiveResource_WhenLocationIsNull_ThenThrowsCompasSclDataServiceException() {
        var id = UUID.randomUUID();
        var hSclFile = buildHistorizedSclFile(id, null);
        when(historizedSclFileRepository.findBySclFileIdAndVersion(id, "1.0.0"))
                .thenReturn(Optional.of(hSclFile));

        assertThrows(CompasSclDataServiceException.class,
                () -> service.archiveResource(id, "1.0.0", "alice", "bob",
                        "application/xml", "file.scd", null));
    }

    @Test
    void archiveResource_WhenValidInput_ThenPersistsEntityWithCorrectFields() {
        var id = UUID.randomUUID();
        var location = buildLocation();
        var hSclFile = buildHistorizedSclFile(id, location);
        when(historizedSclFileRepository.findBySclFileIdAndVersion(id, "1.0.0"))
                .thenReturn(Optional.of(hSclFile));

        service.archiveResource(id, "1.0.0", "alice", "bob",
                "application/xml", "my-file.scd", mock(File.class));

        var captor = ArgumentCaptor.forClass(org.lfenergy.compas.scl.data.entities.ArchivedResource.class);
        verify(archiveRepository).persist(captor.capture());
        var persisted = captor.getValue();
        assertEquals(id, persisted.resourceId);
        assertEquals("my-file.scd", persisted.name);
        assertEquals("1.0.0", persisted.version);
        assertEquals("alice", persisted.author);
        assertEquals("bob", persisted.approver);
        assertEquals("SCD", persisted.type);
        assertEquals("application/xml", persisted.contentType);
        assertEquals(location.key, persisted.location);
        assertEquals(location.id.toString(), persisted.locationId);
    }

    @Test
    void archiveResource_WhenFilenameIsNull_ThenUsesResourceName() {
        var id = UUID.randomUUID();
        var location = buildLocation();
        var hSclFile = buildHistorizedSclFile(id, location);
        when(historizedSclFileRepository.findBySclFileIdAndVersion(id, "1.0.0"))
                .thenReturn(Optional.of(hSclFile));

        service.archiveResource(id, "1.0.0", "alice", null, "application/xml", null, null);

        var captor = ArgumentCaptor.forClass(org.lfenergy.compas.scl.data.entities.ArchivedResource.class);
        verify(archiveRepository).persist(captor.capture());
        assertEquals("test-resource.scd", captor.getValue().name);
    }

    @Test
    void archiveResource_WhenFilenameIsBlank_ThenUsesResourceName() {
        var id = UUID.randomUUID();
        var location = buildLocation();
        var hSclFile = buildHistorizedSclFile(id, location);
        when(historizedSclFileRepository.findBySclFileIdAndVersion(id, "1.0.0"))
                .thenReturn(Optional.of(hSclFile));

        service.archiveResource(id, "1.0.0", "alice", null, "application/xml", "   ", null);

        var captor = ArgumentCaptor.forClass(org.lfenergy.compas.scl.data.entities.ArchivedResource.class);
        verify(archiveRepository).persist(captor.capture());
        assertEquals("test-resource.scd", captor.getValue().name);
    }

    @Test
    void archiveResource_WhenValid_ThenReturnsDtoWithResourceId() {
        var id = UUID.randomUUID();
        var location = buildLocation();
        var hSclFile = buildHistorizedSclFile(id, location);
        when(historizedSclFileRepository.findBySclFileIdAndVersion(id, "1.0.0"))
                .thenReturn(Optional.of(hSclFile));

        var dto = service.archiveResource(id, "1.0.0", "alice", "bob",
                "application/xml", "file.scd", null);

        assertNotNull(dto);
        assertEquals(id.toString(), dto.getUuid());
    }

    @Test
    void archiveResource_WhenEntityHasNullTags_ThenDtoFieldsIsEmpty() {
        var id = UUID.randomUUID();
        var location = buildLocation();
        var hSclFile = buildHistorizedSclFile(id, location);
        when(historizedSclFileRepository.findBySclFileIdAndVersion(id, "1.0.0"))
                .thenReturn(Optional.of(hSclFile));

        var dto = service.archiveResource(id, "1.0.0", "alice", null,
                "application/xml", "file.scd", null);

        assertNotNull(dto.getFields());
        assertTrue(dto.getFields().isEmpty());
    }

    // ---- archiveSclResource ------------------------------------------------

    @Test
    void archiveSclResource_WhenSclFileNotFound_ThenThrowsCompasNoDataFoundException() {
        var id = UUID.randomUUID();
        when(historizedSclFileRepository.findLatestBySclFileId(id)).thenReturn(Optional.empty());

        assertThrows(CompasNoDataFoundException.class,
                () -> service.archiveSclResource(id, "1.0.0"));
    }

    @Test
    void archiveSclResource_WhenLocationIsNull_ThenThrowsCompasSclDataServiceException() {
        var id = UUID.randomUUID();
        var hSclFile = buildHistorizedSclFile(id, null);
        when(historizedSclFileRepository.findLatestBySclFileId(id))
                .thenReturn(Optional.of(hSclFile));

        assertThrows(CompasSclDataServiceException.class,
                () -> service.archiveSclResource(id, "1.0.0"));
    }

    @Test
    void archiveSclResource_WhenAlreadyArchived_ThenThrowsCompasSclDataServiceException() {
        var id = UUID.randomUUID();
        var hSclFile = buildHistorizedSclFile(id, buildLocation());
        when(historizedSclFileRepository.findLatestBySclFileId(id))
                .thenReturn(Optional.of(hSclFile));
        when(archiveRepository.existsByResourceIdAndVersion(id, "1.0.0")).thenReturn(true);

        assertThrows(CompasSclDataServiceException.class,
                () -> service.archiveSclResource(id, "1.0.0"));
    }

    @Test
    void archiveSclResource_WhenValid_ThenUpdatesHistorizedFileAsArchived() {
        var id = UUID.randomUUID();
        var hSclFile = buildHistorizedSclFile(id, buildLocation());
        when(historizedSclFileRepository.findLatestBySclFileId(id))
                .thenReturn(Optional.of(hSclFile));
        when(archiveRepository.existsByResourceIdAndVersion(id, "1.0.0")).thenReturn(false);
        when(historizedSclFileRepository.update(anyString(), any(), any(), any(), any()))
                .thenReturn(1L);

        service.archiveSclResource(id, "1.0.0");

        verify(historizedSclFileRepository).update(
                contains("archived = true"),
                eq(id),
                eq((short) 1),
                eq((short) 0),
                eq((short) 0));
    }

    @Test
    void archiveSclResource_WhenValid_ThenPersistsArchivedResource() {
        var id = UUID.randomUUID();
        var location = buildLocation();
        var hSclFile = buildHistorizedSclFile(id, location);
        when(historizedSclFileRepository.findLatestBySclFileId(id))
                .thenReturn(Optional.of(hSclFile));
        when(archiveRepository.existsByResourceIdAndVersion(id, "1.0.0")).thenReturn(false);
        when(historizedSclFileRepository.update(anyString(), any(), any(), any(), any()))
                .thenReturn(1L);

        service.archiveSclResource(id, "1.0.0");

        var captor = ArgumentCaptor.forClass(org.lfenergy.compas.scl.data.entities.ArchivedResource.class);
        verify(archiveRepository).persist(captor.capture());
        var persisted = captor.getValue();
        assertEquals(id, persisted.resourceId);
        assertEquals("1.0.0", persisted.version);
        assertEquals("alice", persisted.author);
        assertEquals(location.key, persisted.location);
    }

    @Test
    void archiveSclResource_WhenValid_ThenReturnsDtoWithResourceId() {
        var id = UUID.randomUUID();
        var hSclFile = buildHistorizedSclFile(id, buildLocation());
        when(historizedSclFileRepository.findLatestBySclFileId(id))
                .thenReturn(Optional.of(hSclFile));
        when(archiveRepository.existsByResourceIdAndVersion(id, "1.0.0")).thenReturn(false);
        when(historizedSclFileRepository.update(anyString(), any(), any(), any(), any()))
                .thenReturn(1L);

        var dto = service.archiveSclResource(id, "1.0.0");

        assertNotNull(dto);
        assertEquals(id.toString(), dto.getUuid());
    }

    // ---- retrieveArchivedResourceHistory -----------------------------------

    @Test
    void retrieveArchivedResourceHistory_WhenEntriesExist_ThenReturnsHistoryWithVersions() {
        var id = UUID.randomUUID();
        var e1 = buildArchivedEntity(id);
        var e2 = buildArchivedEntity(id);
        e2.version = "1.1.0";
        when(archiveRepository.findAllByResourceId(id)).thenReturn(List.of(e1, e2));

        var result = service.retrieveArchivedResourceHistory(id);

        assertNotNull(result);
        assertNotNull(result.getVersions());
        assertEquals(2, result.getVersions().size());
        assertEquals(id.toString(), result.getVersions().get(0).getUuid());
    }

    @Test
    void retrieveArchivedResourceHistory_WhenEmpty_ThenReturnsEmptyVersions() {
        var id = UUID.randomUUID();
        when(archiveRepository.findAllByResourceId(id)).thenReturn(List.of());

        var result = service.retrieveArchivedResourceHistory(id);

        assertNotNull(result);
        assertNotNull(result.getVersions());
        assertTrue(result.getVersions().isEmpty());
    }

    @Test
    void retrieveArchivedResourceHistory_WhenEntryHasTags_ThenVersionDtoContainsTags() {
        var id = UUID.randomUUID();
        var entity = buildArchivedEntity(id);
        var tag = new ResourceTag();
        tag.key = "env";
        tag.value = "prod";
        entity.fields = new ArrayList<>(List.of(tag));
        when(archiveRepository.findAllByResourceId(id)).thenReturn(List.of(entity));

        var result = service.retrieveArchivedResourceHistory(id);

        var versionDto = result.getVersions().get(0);
        assertNotNull(versionDto.getFields());
        assertEquals(1, versionDto.getFields().size());
        assertEquals("env", versionDto.getFields().get(0).getKey());
        assertEquals("prod", versionDto.getFields().get(0).getValue());
    }

    // ---- searchArchivedResources -------------------------------------------

    @Test
    void searchArchivedResources_WhenUuidProvided_ThenSearchesByResourceId() {
        var resourceId = UUID.randomUUID();
        var entity = buildArchivedEntity(resourceId);
        when(archiveRepository.findAllByResourceId(resourceId)).thenReturn(List.of(entity));

        var search = new ArchivedResourcesSearch().uuid(resourceId.toString());
        var result = service.searchArchivedResources(search);

        assertNotNull(result);
        assertEquals(1, result.getResources().size());
        verify(archiveRepository).findAllByResourceId(resourceId);
        verify(archiveRepository, never()).searchByFilters(
                any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchArchivedResources_WhenNoUuid_ThenSearchesByFilters() {
        var resourceId = UUID.randomUUID();
        var entity = buildArchivedEntity(resourceId);
        when(archiveRepository.searchByFilters(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(entity));

        var search = new ArchivedResourcesSearch()
                .location("LOC-01")
                .type("SCD");
        var result = service.searchArchivedResources(search);

        assertNotNull(result);
        assertEquals(1, result.getResources().size());
        verify(archiveRepository).searchByFilters(
                eq("LOC-01"), isNull(), isNull(), isNull(), eq("SCD"), isNull(), isNull(), isNull());
        verify(archiveRepository, never()).findAllByResourceId(any());
    }

    @Test
    void searchArchivedResources_WhenUuidBlank_ThenSearchesByFilters() {
        when(archiveRepository.searchByFilters(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        var search = new ArchivedResourcesSearch().uuid("   ");
        var result = service.searchArchivedResources(search);

        assertNotNull(result);
        verify(archiveRepository).searchByFilters(any(), any(), any(), any(), any(), any(), any(), any());
        verify(archiveRepository, never()).findAllByResourceId(any());
    }

    @Test
    void searchArchivedResources_WhenEntityHasNullTags_ThenDtoFieldsIsEmpty() {
        var resourceId = UUID.randomUUID();
        var entity = buildArchivedEntity(resourceId);
        entity.fields = null;
        when(archiveRepository.searchByFilters(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(entity));

        var search = new ArchivedResourcesSearch();
        var result = service.searchArchivedResources(search);

        assertNotNull(result.getResources().get(0).getFields());
        assertTrue(result.getResources().get(0).getFields().isEmpty());
    }
}
