package org.lfenergy.compas.scl.data.rest.api.archive;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.lfenergy.compas.scl.data.model.*;
import org.lfenergy.compas.scl.data.rest.UserInfoProperties;
import org.lfenergy.compas.scl.data.rest.api.archive.model.*;
import org.lfenergy.compas.scl.data.rest.api.archive.model.ArchivedResourceVersion;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.UUID;

@RequestScoped
public class ArchiveResource implements ArchivingApi {

    private final CompasSclDataService compasSclDataService;
    private final JsonWebToken jsonWebToken;
    private final UserInfoProperties userInfoProperties;

    @Inject
    public ArchiveResource(CompasSclDataService compasSclDataService, JsonWebToken jsonWebToken, UserInfoProperties userInfoProperties) {
        this.compasSclDataService = compasSclDataService;
        this.jsonWebToken = jsonWebToken;
        this.userInfoProperties = userInfoProperties;
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
        String approver = jsonWebToken.getClaim(userInfoProperties.name());
        return Uni.createFrom()
            .item(() -> compasSclDataService.archiveSclResource(id, new Version(version), approver))
            .runSubscriptionOn(Infrastructure.getDefaultExecutor())
            .onItem()
            .transform(this::mapToArchivedResource);
    }

    @Override
    public Uni<ArchivedResourcesHistory> retrieveArchivedResourceHistory(UUID id) {
        return Uni.createFrom()
            .item(() -> compasSclDataService.getArchivedResourceHistory(id))
            .runSubscriptionOn(Infrastructure.getDefaultExecutor())
            .onItem()
            .transform(this::mapToArchivedResourcesHistory);
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

    private ArchivedResource mapToArchivedResource(IAbstractArchivedResourceMetaItem archivedResource) {
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

    private ArchivedResourcesHistory mapToArchivedResourcesHistory(IArchivedResourcesHistoryMetaItem archivedResourcesHistoryMetaItem) {
        return new ArchivedResourcesHistory()
            .versions(
                archivedResourcesHistoryMetaItem.getVersions()
                    .stream()
                    .map(this::mapToArchivedResourceVersion)
                    .toList()
            );
    }

    private ArchivedResourceVersion mapToArchivedResourceVersion(IArchivedResourceVersion resourceVersion) {
        return new ArchivedResourceVersion()
            .uuid(resourceVersion.getId())
            .location(resourceVersion.getLocation())
            .name(resourceVersion.getName())
            .note(resourceVersion.getNote())
            .author(resourceVersion.getAuthor())
            .approver(resourceVersion.getApprover())
            .type(resourceVersion.getType())
            .contentType(resourceVersion.getContentType())
            .voltage(resourceVersion.getVoltage())
            .version(resourceVersion.getVersion())
            .modifiedAt(resourceVersion.getModifiedAt())
            .archivedAt(resourceVersion.getArchivedAt())
            .fields(resourceVersion.getFields()
                .stream()
                .map(field ->
                    new ResourceTag()
                        .key(field.getKey())
                        .value(field.getValue())
                )
                .toList()
            )
            .comment(resourceVersion.getComment())
            .archived(resourceVersion.isArchived());
    }
}
