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
        var sclFileOptional = historizedSclFileRepository.findBySclFileIdAndVersion(id, version);
        if (sclFileOptional.isEmpty()) {
            throw new CompasNoDataFoundException("Resource " + id + " not found");
        }
        if (sclFileOptional.get().location == null) {
            throw new CompasSclDataServiceException(INVALID_INPUT_ERROR_CODE,
                    "Resource " + id + " has no location assigned");
        }

        var sclFile = sclFileOptional.get();
        var entity = new org.lfenergy.compas.scl.data.entities.ArchivedResource();
        entity.resourceId = id;
        entity.name = filename != null && !filename.isBlank() ? filename : sclFile.sclFile.name;
        entity.version = version;
        entity.locationId = sclFile.location != null ? sclFile.location.id.toString() : null;
        entity.location = sclFile.location != null ? sclFile.location.key : null;
        entity.author = author;
        entity.approver = approver;
        entity.type = sclFile.sclFile.type;
        entity.contentType = contentType;
        entity.modifiedAt = sclFile.changedAt;
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
        var sclFileOptional = historizedSclFileRepository.findLatestBySclFileId(id);
        if (sclFileOptional.isEmpty()) {
            throw new CompasNoDataFoundException("Resource " + id + " not found");
        }
        if (sclFileOptional.get().location == null) {
            throw new CompasSclDataServiceException(INVALID_INPUT_ERROR_CODE,
                    "Resource " + id + " has no location assigned");
        }
        if (archiveRepository.existsByResourceIdAndVersion(id, version)) {
            throw new CompasSclDataServiceException(INVALID_INPUT_ERROR_CODE,
                    "Resource " + id + " version " + version + " is already archived");
        }

        var sclFile = sclFileOptional.get();
        // Mark the referenced resource entry as archived
        var v = new Version(version);
        historizedSclFileRepository.update(
            "archived = true where sclFile.id.id = ?1 and sclFile.id.majorVersion = ?2 and sclFile.id.minorVersion = ?3 and sclFile.id.patchVersion = ?4",
            id, (short) v.getMajorVersion(), (short) v.getMinorVersion(), (short) v.getPatchVersion());

        // FIXME: storing the SCL content in the archive as well, to be able to restore it later if needed
        var archivedResource = new org.lfenergy.compas.scl.data.entities.ArchivedResource();
        archivedResource.resourceId = id;
        archivedResource.name = sclFile.sclFile.name;
        archivedResource.version = version;
        archivedResource.locationId = sclFile.location != null ? sclFile.location.id.toString() : null;
        archivedResource.location = sclFile.location != null ? sclFile.location.key : null;
        archivedResource.author = sclFile.sclFile.createdBy;
        archivedResource.type = sclFile.sclFile.type;
        archivedResource.contentType = sclFile.sclFile.type;
        archivedResource.modifiedAt = sclFile.changedAt;
        archivedResource.archivedAt = OffsetDateTime.now();

        archiveRepository.persist(archivedResource);
        return toDto(archivedResource);
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
