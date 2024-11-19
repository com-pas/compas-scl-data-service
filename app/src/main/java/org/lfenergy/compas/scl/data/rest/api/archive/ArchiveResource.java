package org.lfenergy.compas.scl.data.rest.api.archive;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.lfenergy.compas.scl.data.model.IArchivedResourceMetaItem;
import org.lfenergy.compas.scl.data.model.IArchivedResourcesMetaItem;
import org.lfenergy.compas.scl.data.rest.api.archive.model.ArchivedResource;
import org.lfenergy.compas.scl.data.rest.api.archive.model.ArchivedResources;
import org.lfenergy.compas.scl.data.rest.api.archive.model.ArchivedResourcesSearch;
import org.lfenergy.compas.scl.data.rest.api.archive.model.ResourceTag;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.UUID;

@RequestScoped
public class ArchiveResource implements ArchivingApi {

    private final CompasSclDataService compasSclDataService;

    @Inject
    public ArchiveResource(CompasSclDataService compasSclDataService) {
        this.compasSclDataService = compasSclDataService;
    }

    @Override
    public Uni<ArchivedResource> archiveResource(UUID id, String version, String xAuthor, String xApprover, String contentType, String xFilename, File body) {
        return Uni.createFrom()
            .item(() -> compasSclDataService.archiveResource(id, version, xAuthor, xApprover, contentType, xFilename, body))
            .runSubscriptionOn(Infrastructure.getDefaultExecutor())
            .onItem()
            .transform(this::mapToArchivedResource);
    }

    @Override
    public Uni<ArchivedResource> archiveSclResource(UUID id, String version) {
        return Uni.createFrom()
            .item(() -> compasSclDataService.archiveSclResource(id, version))
            .runSubscriptionOn(Infrastructure.getDefaultExecutor())
            .onItem()
            .transform(this::mapToArchivedResource);
    }

    @Override
    public Uni<ArchivedResources> searchArchivedResources(ArchivedResourcesSearch archivedResourcesSearch) {
        return Uni.createFrom()
            .item(() -> getArchivedResourcesMetaItem(archivedResourcesSearch))
            .runSubscriptionOn(Infrastructure.getDefaultExecutor())
            .onItem()
            .transform(this::mapToArchivedResources);
    }

    private IArchivedResourcesMetaItem getArchivedResourcesMetaItem(ArchivedResourcesSearch archivedResourcesSearch) {
        String uuid = archivedResourcesSearch.getUuid();
        if (uuid != null && !uuid.isBlank()) {
            return compasSclDataService.searchArchivedResources(UUID.fromString(uuid));
        }

        String location = archivedResourcesSearch.getLocation();
        String name = archivedResourcesSearch.getName();
        String approver = archivedResourcesSearch.getApprover();
        String contentType = archivedResourcesSearch.getContentType();
        String type = archivedResourcesSearch.getType();
        String voltage = archivedResourcesSearch.getVoltage();
        OffsetDateTime from = archivedResourcesSearch.getFrom();
        OffsetDateTime to = archivedResourcesSearch.getTo();
        return compasSclDataService.searchArchivedResources(location, name, approver, contentType, type, voltage, from, to);
    }

    private ArchivedResource mapToArchivedResource(IArchivedResourceMetaItem archivedResource) {
        return new ArchivedResource()
            .uuid(archivedResource.getId())
            .location(archivedResource.getLocation())
            .name(archivedResource.getName())
            .note(archivedResource.getNote())
            .author(archivedResource.getAuthor())
            .approver(archivedResource.getApprover())
            .type(archivedResource.getType())
            .contentType(archivedResource.getContentType())
            .voltage(archivedResource.getVoltage())
            .version(archivedResource.getVersion())
            .modifiedAt(archivedResource.getModifiedAt())
            .archivedAt(archivedResource.getArchivedAt())
            .fields(
                archivedResource.getFields()
                    .stream()
                    .map(item -> new ResourceTag().key(item.getKey()).value(item.getValue())).toList()
            );
    }

    private ArchivedResources mapToArchivedResources(IArchivedResourcesMetaItem archivedResources) {
        return new ArchivedResources()
            .resources(
                archivedResources.getResources()
                    .stream()
                    .map(this::mapToArchivedResource)
                    .toList()
            );
    }
}
