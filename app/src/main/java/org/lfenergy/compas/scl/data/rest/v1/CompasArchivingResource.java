// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lfenergy.compas.scl.data.rest.ArchivingApi;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResource;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResources;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResourcesHistory;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResourcesSearch;
import org.lfenergy.compas.scl.data.service.ArchivingService;

import java.io.File;
import java.util.UUID;

@Blocking
@RequestScoped
public class CompasArchivingResource implements ArchivingApi {

    private static final Logger LOGGER = LogManager.getLogger(CompasArchivingResource.class);

    private final ArchivingService archivingService;

    @Inject
    public CompasArchivingResource(ArchivingService archivingService) {
        this.archivingService = archivingService;
    }

    @Override
    public Uni<ArchivedResource> archiveResource(UUID id, String version, String xAuthor, String xApprover, String contentType, String xFilename, File body) {
        LOGGER.info("Archiving resource with id {} and version {} by {}", id, version, xAuthor);
        return Uni.createFrom().item(() -> archivingService.archiveResource(id, version, xAuthor, xApprover, contentType, xFilename, body));
    }

    @Override
    public Uni<ArchivedResource> archiveSclResource(UUID id, String version) {
        LOGGER.info("Archiving SCL resource with id {} and version {}", id, version);
        return Uni.createFrom().item(() -> archivingService.archiveSclResource(id, version));
    }

    @Override
    public Uni<ArchivedResourcesHistory> retrieveArchivedResourceHistory(UUID id) {
        LOGGER.debug("Retrieving archived resource history for resource with id {}", id);
        return Uni.createFrom().item(() -> archivingService.retrieveArchivedResourceHistory(id));
    }

    @Override
    public Uni<ArchivedResources> searchArchivedResources(ArchivedResourcesSearch archivedResourcesSearch) {
        LOGGER.debug("Searching archived resources with criteria: {}", archivedResourcesSearch);
        return Uni.createFrom().item(() -> archivingService.searchArchivedResources(archivedResourcesSearch));
    }
}
