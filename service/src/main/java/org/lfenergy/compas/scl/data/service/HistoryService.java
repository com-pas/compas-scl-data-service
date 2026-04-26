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
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.repository.HistorizedSclFileRepository;
import org.lfenergy.compas.scl.data.rest.api.history.DataResource;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourceHistory;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourceSearch;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourceVersion;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourcesResult;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.UUID;

import static jakarta.transaction.Transactional.TxType.SUPPORTS;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.CREATION_ERROR_CODE;

@ApplicationScoped
public class HistoryService {

    private final HistorizedSclFileRepository historizedSclFileRepository;
    private final CompasSclDataRepository compasSclDataRepository;

    @Inject
    public HistoryService(HistorizedSclFileRepository historizedSclFileRepository,
                          CompasSclDataRepository compasSclDataRepository) {
        this.historizedSclFileRepository = historizedSclFileRepository;
        this.compasSclDataRepository = compasSclDataRepository;
    }

    /**
     * Retrieves the SCL file content for a specific resource version and writes it to a
     * temporary file so that JAX-RS can stream it back to the caller.
     */
    @Transactional(SUPPORTS)
    public File retrieveDataResourceByVersion(UUID id, String version) {
        var entry = historizedSclFileRepository.findBySclFileIdAndVersion(id, version)
            .orElseThrow(() -> new CompasNoDataFoundException(
                "No referenced resource for resource " + id + " version " + version));

        var sclType = SclFileType.valueOf(entry.sclFile.type.toUpperCase());
        var sclContent = compasSclDataRepository.findByUUID(sclType, id, new Version(version));

        try {
            var ownerOnly = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rw-------"));
            var tempFile = Files.createTempFile("resource_" + id + "_", ".xml", ownerOnly).toFile();
            tempFile.deleteOnExit();
            Files.writeString(tempFile.toPath(), sclContent, StandardCharsets.UTF_8);
            return tempFile;
        } catch (IOException e) {
            throw new CompasSclDataServiceException(CREATION_ERROR_CODE,
                "Failed to write resource content to temporary file", e);
        }
    }

    /**
     * Returns the full version history (all versions) of a data resource.
     */
    @Transactional(SUPPORTS)
    public DataResourceHistory retrieveDataResourceHistory(UUID id) {
        var entries = historizedSclFileRepository.findAllBySclFileId(id);
        if (entries.isEmpty()) {
            throw new CompasNoDataFoundException(
                "No referenced resources found for resource " + id);
        }
        var versions = entries.stream().map(this::toDataResourceVersion).toList();
        return new DataResourceHistory().versions(versions);
    }

    /**
     * Searches for data resources.  If {@code search.uuid} is set, only entries for that
     * resource are considered (returning the latest version).  Otherwise the optional
     * filter fields (type, name, location, author, from, to) constrain the result.
     */
    @Transactional(SUPPORTS)
    public DataResourcesResult searchForResources(DataResourceSearch search) {
        List<org.lfenergy.compas.scl.data.entities.HistorizedSclFile> entries;
        if (search.getUuid() != null && !search.getUuid().isBlank()) {
            entries = historizedSclFileRepository
                .findLatestBySclFileId(UUID.fromString(search.getUuid()))
                .map(List::of)
                .orElse(List.of());
        } else {
            entries = historizedSclFileRepository.searchLatest(
                search.getType(), search.getName(), search.getLocation(),
                search.getAuthor(), search.getFrom(), search.getTo());
        }
        var results = entries.stream().map(this::toDataResource).toList();
        return new DataResourcesResult().results(results);
    }

    private DataResourceVersion toDataResourceVersion(org.lfenergy.compas.scl.data.entities.HistorizedSclFile entry) {
        return new DataResourceVersion()
            .uuid(entry.sclFile.id.id)
            .name(entry.sclFile.name)
            .author(entry.sclFile.createdBy)
            .type(entry.sclFile.type)
            .changedAt(entry.changedAt)
            .version(entry.sclFile.id.version())
            .available(entry.available)
            .deleted(entry.sclFile.isDeleted)
            .location(entry.location != null ? entry.location.id.toString() : null)
            .comment(entry.comment)
            .archived(entry.archived);
    }

    private DataResource toDataResource(org.lfenergy.compas.scl.data.entities.HistorizedSclFile entry) {
        return new DataResource()
            .uuid(entry.sclFile.id.id)
            .name(entry.sclFile.name)
            .author(entry.sclFile.createdBy)
            .type(entry.sclFile.type)
            .changedAt(entry.changedAt)
            .version(entry.sclFile.id.version())
            .available(entry.available)
            .deleted(entry.sclFile.isDeleted)
            .location(entry.location != null ? entry.location.id.toString() : null);
    }
}
