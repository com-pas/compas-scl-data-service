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
import org.lfenergy.compas.scl.data.rest.HistoryApi;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourceHistory;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourceSearch;
import org.lfenergy.compas.scl.data.rest.api.history.DataResourcesResult;
import org.lfenergy.compas.scl.data.service.HistoryService;

import java.io.File;
import java.util.UUID;

@Blocking
@RequestScoped
public class CompasHistoryResource implements HistoryApi {

    private static final Logger LOGGER = LogManager.getLogger(CompasHistoryResource.class);

    private final HistoryService historyService;

    @Inject
    public CompasHistoryResource(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Override
    public Uni<File> retrieveDataResourceByVersion(UUID id, String version) {
        LOGGER.debug("Retrieving data resource with id {} and version {}", id, version);
        return Uni.createFrom().item(() -> historyService.retrieveDataResourceByVersion(id, version));
    }

    @Override
    public Uni<DataResourceHistory> retrieveDataResourceHistory(UUID id) {
        LOGGER.debug("Retrieving data resource history for resource with id {}", id);
        return Uni.createFrom().item(() -> historyService.retrieveDataResourceHistory(id));
    }

    @Override
    public Uni<DataResourcesResult> searchForResources(DataResourceSearch dataResourceSearch) {
        LOGGER.debug("Searching for data resources with criteria: {}", dataResourceSearch);
        return Uni.createFrom().item(() -> historyService.searchForResources(dataResourceSearch));
    }
}
