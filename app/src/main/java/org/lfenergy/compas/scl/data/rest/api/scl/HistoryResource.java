package org.lfenergy.compas.scl.data.rest.api.scl;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lfenergy.compas.scl.data.model.IHistoryMetaItem;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.rest.api.scl.model.*;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.UnexpectedException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RequestScoped
public class HistoryResource implements HistoryApi {
    private static final Logger LOGGER = LogManager.getLogger(HistoryResource.class);

    private final CompasSclDataService compasSclDataService;

    @Inject
    public HistoryResource(CompasSclDataService compasSclDataService) {
        this.compasSclDataService = compasSclDataService;
    }

    @Override
    public Uni<DataResourcesResult> searchForResources(DataResourceSearch dataResourceSearch) {
        LOGGER.info("Triggering search with filter: {}", dataResourceSearch);
        return Uni.createFrom()
                .item(() -> getHistoryMetaItems(dataResourceSearch))
                .onItem().transform(items -> new DataResourcesResult().results(items.stream().map(this::mapToDataResource).toList()))
                .onFailure().recoverWithItem(e -> {
                    LOGGER.error("Unexpected error while searching for resources", e);
                    return new DataResourcesResult(); // Return an empty result or handle as needed
                });
    }

    private List<IHistoryMetaItem> getHistoryMetaItems(DataResourceSearch dataResourceSearch) {
        String uuid = dataResourceSearch.getUuid();

        if (uuid != null) {
            return compasSclDataService.listHistory(UUID.fromString(uuid));
        }

        SclFileType type = dataResourceSearch.getType() != null ? SclFileType.valueOf(dataResourceSearch.getType()) : null;
        String name = dataResourceSearch.getName();
        String author = dataResourceSearch.getAuthor();
        OffsetDateTime from = dataResourceSearch.getFrom();
        OffsetDateTime to = dataResourceSearch.getTo();

        if (type != null || name != null || author != null || from != null || to != null) {
            return compasSclDataService.listHistory(type, name, author, from, to);
        }

        return compasSclDataService.listHistory();
    }

    private DataResource mapToDataResource(IHistoryMetaItem e) {
        return new DataResource()
                .uuid(UUID.fromString(e.getId()))
                .name(e.getName())
                .author(e.getAuthor())
                .type(e.getType())
                .changedAt(e.getChangedAt())
                .version(e.getVersion())
                .available(e.isAvailable())
                .deleted(e.isDeleted())
                .location(e.getLocation());
    }

    @Override
    public Uni<DataResourceHistory> retrieveDataResourceHistory(UUID id) {
        LOGGER.info("Retrieving history for data resource ID: {}", id);
        return Uni.createFrom()
                .item(() -> compasSclDataService.listHistoryVersionsByUUID(id))
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .onItem().transform(versions -> new DataResourceHistory().versions(versions.stream().map(this::mapToDataResourceVersion).toList()));
    }

    private DataResourceVersion mapToDataResourceVersion(IHistoryMetaItem e) {
        return new DataResourceVersion()
                .uuid(UUID.fromString(e.getId()))
                .name(e.getName())
                .author(e.getAuthor())
                .type(e.getType())
                .changedAt(e.getChangedAt())
                .version(e.getVersion())
                .available(e.isAvailable())
                .deleted(e.isDeleted())
                .comment(e.getComment())
                .archived(e.isArchived())
                .location(e.getLocation());
    }

    @Override
    public Uni<File> retrieveDataResourceByVersion(UUID id, String version) {
        LOGGER.info("Retrieving data resource for ID: {} and version: {}", id, version);
        return Uni.createFrom()
                .item(() -> compasSclDataService.findByUUID(id, new Version(version)))
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .onItem().transformToUni(this::createTempFileWithData)
                .onFailure().transform(e -> {
                    LOGGER.error("Failed to retrieve or create temp file", e);
                    return new UnexpectedException("Error while retrieving data resource", (Exception) e);
                });
    }

    private Uni<File> createTempFileWithData(String data) {
        return Uni.createFrom()
                .item(() -> createTempFile(data))
                .runSubscriptionOn(Infrastructure.getDefaultExecutor());
    }

    private File createTempFile(String data) {
        try {
            Path tempFile = Files.createTempFile("resource_", ".tmp");
            Files.writeString(tempFile, data);
            return tempFile.toFile();
        } catch (IOException e) {
            throw new RuntimeException("Error creating or writing to temp file", e);
        }
    }
}
