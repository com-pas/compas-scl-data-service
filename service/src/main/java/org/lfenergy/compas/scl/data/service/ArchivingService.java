// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.ArchivedResourceRepository;
import org.lfenergy.compas.scl.data.repository.HistorizedSclFileRepository;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResource;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResourceVersion;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResources;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResourcesHistory;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResourcesSearch;
import org.lfenergy.compas.scl.data.rest.api.archive.ResourceTag;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.INVALID_INPUT_ERROR_CODE;

@ApplicationScoped
public class ArchivingService {

    private final ArchivedResourceRepository archiveRepository;
    private final HistorizedSclFileRepository historizedSclFileRepository;

    @Inject
    public ArchivingService(ArchivedResourceRepository archiveRepository,
                            HistorizedSclFileRepository historizedSclFileRepository) {
        this.archiveRepository = archiveRepository;
        this.historizedSclFileRepository = historizedSclFileRepository;
    }

    /**
     * Archives an external resource file (e.g. a PDF or binary attachment) linked to
     * an existing SCL resource.  The SCL resource must already have a location assigned
     * and the requested version must exist in the history.
     */
    @Transactional(REQUIRED)
    public ArchivedResource archiveResource(UUID id, String version, String author, String approver,
                                            String contentType, String filename, File body) {
        var historyEntries = historizedSclFileRepository.findAllBySclFileId(id);
        if (historyEntries.isEmpty() || historyEntries.stream().noneMatch(h -> h.sclFile.id.version().equals(version))) {
            throw new CompasNoDataFoundException(
                "Resource " + id + " with version " + version + " not found");
        }
        var latestEntry = historyEntries.get(0);
        if (latestEntry.location == null) {
            throw new CompasSclDataServiceException(INVALID_INPUT_ERROR_CODE,
                "Resource " + id + " has no location assigned");
        }
        var historyEntry = historyEntries.stream()
            .filter(h -> h.sclFile.id.version().equals(version))
            .findFirst()
            .orElseThrow();

        var entity = new org.lfenergy.compas.scl.data.entities.ArchivedResource();
        entity.resourceId = id;
        entity.name = filename != null && !filename.isBlank() ? filename : historyEntry.sclFile.name;
        entity.version = version;
        entity.locationId = historyEntry.location != null ? historyEntry.location.id.toString() : null;
        entity.location = historyEntry.location != null ? historyEntry.location.key : null;
        entity.author = author;
        entity.approver = approver;
        entity.type = historyEntry.sclFile.type;
        entity.contentType = contentType;
        entity.modifiedAt = historyEntry.changedAt;
        entity.archivedAt = OffsetDateTime.now();

        archiveRepository.persist(entity);
        return toDto(entity);
    }

    /**
     * Archives an SCL resource that already exists in the database. The resource must
     * have a location assigned and must not have been archived before (for the given
     * version).
     */
    @Transactional(REQUIRED)
    public ArchivedResource archiveSclResource(UUID id, String version) {
        var historizedSclFiles = historizedSclFileRepository.findAllBySclFileId(id);
        if (historizedSclFiles.isEmpty()) {
            throw new CompasNoDataFoundException("Resource " + id + " not found");
        }
        var latestEntry = historizedSclFiles.get(0);
        if (latestEntry.location == null) {
            throw new CompasSclDataServiceException(INVALID_INPUT_ERROR_CODE,
                "Resource " + id + " has no location assigned");
        }
        if (archiveRepository.existsByResourceIdAndVersion(id, version)) {
            throw new CompasSclDataServiceException(INVALID_INPUT_ERROR_CODE,
                "Resource " + id + " version " + version + " is already archived");
        }
        var historizedSclFile = historizedSclFiles.stream()
            .filter(h -> h.sclFile.id.version().equals(version))
            .findFirst()
            .orElseThrow(() -> new CompasNoDataFoundException(
                "Version " + version + " not found for resource " + id));

        // Mark the referenced resource entry as archived
        var v = new Version(version);
        historizedSclFileRepository.update(
            "archived = true where sclFile.id.id = ?1 and sclFile.id.majorVersion = ?2 and sclFile.id.minorVersion = ?3 and sclFile.id.patchVersion = ?4",
            id, (short) v.getMajorVersion(), (short) v.getMinorVersion(), (short) v.getPatchVersion());

        var entity = new org.lfenergy.compas.scl.data.entities.ArchivedResource();
        entity.resourceId = id;
        entity.name = historizedSclFile.sclFile.name;
        entity.version = version;
        entity.locationId = historizedSclFile.location != null ? historizedSclFile.location.id.toString() : null;
        entity.location = historizedSclFile.location != null ? historizedSclFile.location.key : null;
        entity.author = historizedSclFile.sclFile.createdBy;
        entity.type = historizedSclFile.sclFile.type;
        entity.contentType = historizedSclFile.sclFile.type;
        entity.modifiedAt = historizedSclFile.changedAt;
        entity.archivedAt = OffsetDateTime.now();

        archiveRepository.persist(entity);
        return toDto(entity);
    }

    /**
     * Returns all archived versions for a given resource UUID.
     */
    @Transactional(SUPPORTS)
    public ArchivedResourcesHistory retrieveArchivedResourceHistory(UUID id) {
        var entities = archiveRepository.findAllByResourceId(id);
        var versions = entities.stream().map(this::toVersionDto).toList();
        return new ArchivedResourcesHistory().versions(versions);
    }

    /**
     * Searches archived resources.  If {@code search.uuid} is set all versions for that
     * resource are returned; otherwise the optional filter criteria are applied.
     */
    @Transactional(SUPPORTS)
    public ArchivedResources searchArchivedResources(ArchivedResourcesSearch search) {
        List<org.lfenergy.compas.scl.data.entities.ArchivedResource> entities;
        if (search.getUuid() != null && !search.getUuid().isBlank()) {
            entities = archiveRepository.findAllByResourceId(UUID.fromString(search.getUuid()));
        } else {
            entities = archiveRepository.searchByFilters(
                search.getLocation(), search.getName(), search.getApprover(),
                search.getContentType(), search.getType(), search.getVoltage(),
                search.getFrom(), search.getTo());
        }
        var resources = entities.stream().map(entity -> toDto(entity)).toList();
        return new ArchivedResources().resources(resources);
    }

    private ArchivedResource toDto(org.lfenergy.compas.scl.data.entities.ArchivedResource entity) {
        return new ArchivedResource()
            .uuid(entity.resourceId != null ? entity.resourceId.toString() : entity.id.toString())
            .location(entity.location)
            .name(entity.name)
            .note(entity.note)
            .author(entity.author)
            .approver(entity.approver)
            .type(entity.type)
            .contentType(entity.contentType)
            .voltage(entity.voltage)
            .version(entity.version)
            .modifiedAt(entity.modifiedAt)
            .archivedAt(entity.archivedAt)
            .fields(toResourceTagDtos(entity.fields));
    }

    private ArchivedResourceVersion toVersionDto(org.lfenergy.compas.scl.data.entities.ArchivedResource entity) {
        return new ArchivedResourceVersion()
            .uuid(entity.resourceId != null ? entity.resourceId.toString() : entity.id.toString())
            .location(entity.location)
            .name(entity.name)
            .note(entity.note)
            .author(entity.author)
            .approver(entity.approver)
            .type(entity.type)
            .contentType(entity.contentType)
            .voltage(entity.voltage)
            .version(entity.version)
            .modifiedAt(entity.modifiedAt)
            .archivedAt(entity.archivedAt)
            .fields(toResourceTagDtos(entity.fields))
            .comment(entity.archivingComment);
    }

    private List<ResourceTag> toResourceTagDtos(
            List<org.lfenergy.compas.scl.data.entities.ResourceTag> tags) {
        if (tags == null) {
            return new ArrayList<>();
        }
        return tags.stream()
            .map(t -> new ResourceTag().key(t.key).value(t.value))
            .toList();
    }
}
