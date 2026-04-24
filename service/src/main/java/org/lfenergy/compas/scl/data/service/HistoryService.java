// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourceHistory;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourceSearch;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourcesResult;

import java.io.File;
import java.util.UUID;

import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
public class HistoryService {

    private final CompasSclDataRepository repository;

    @Inject
    public HistoryService(CompasSclDataRepository repository) {
        this.repository = repository;
    }

    @Transactional(SUPPORTS)
    public File retrieveDataResourceByVersion(UUID id, String version) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Transactional(SUPPORTS)
    public DataResourceHistory retrieveDataResourceHistory(UUID id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Transactional(SUPPORTS)
    public DataResourcesResult searchForResources(DataResourceSearch search) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
