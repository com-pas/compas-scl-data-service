// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResource;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResources;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResourcesHistory;
import org.lfenergy.compas.scl.data.rest.api.archive.ArchivedResourcesSearch;

import java.io.File;
import java.util.UUID;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
public class ArchivingService {

    private final CompasSclDataRepository repository;

    @Inject
    public ArchivingService(CompasSclDataRepository repository) {
        this.repository = repository;
    }

    @Transactional(REQUIRED)
    public ArchivedResource archiveResource(UUID id, String version, String author, String approver, String contentType, String filename, File body) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Transactional(REQUIRED)
    public ArchivedResource archiveSclResource(UUID id, String version) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Transactional(SUPPORTS)
    public ArchivedResourcesHistory retrieveArchivedResourceHistory(UUID id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Transactional(SUPPORTS)
    public ArchivedResources searchArchivedResources(ArchivedResourcesSearch search) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
