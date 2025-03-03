package org.lfenergy.compas.scl.data.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.lfenergy.compas.scl.data.dto.*;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.IAbstractArchivedResourceMetaItem;
import org.lfenergy.compas.scl.data.model.ILocationMetaItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.CREATION_ERROR_CODE;

@ApplicationScoped
public class CompasSclDataArchivingEloServiceImpl implements ICompasSclDataArchivingService {

    private static final Logger LOGGER = LogManager.getLogger(CompasSclDataArchivingEloServiceImpl.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    @RestClient
    IEloConnectorRestClient eloClient;

    @Override
    public Uni<LocationMetaData> createLocation(ILocationMetaItem location) {
        LOGGER.debug("Creating a new location '{}' in ELO!", location.getId());
        LocationData locationData = new LocationData()
            .name(location.getName())
            .key(location.getKey())
            .description(location.getDescription());
        return eloClient.createLocation(locationData)
            .onFailure()
            .transform(throwable -> new CompasSclDataServiceException(CREATION_ERROR_CODE, String.format("Error while creating location '%s' in ELO: %s", location.getId(), throwable.getMessage())))
            .onItem()
            .invoke(Unchecked.consumer(eloLocation ->
            {
                try {
                    LOGGER.debug("returned ELO location item {}", objectMapper.writeValueAsString(eloLocation));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }));
    }

    @Override
    public Uni<ResourceMetaData> archiveData(String locationKey, String filename, UUID uuid, File body, IAbstractArchivedResourceMetaItem archivedResource) {
        LOGGER.debug("Archiving related resource '{}' in ELO!", archivedResource.getId());

        String extension = archivedResource.getName().substring(archivedResource.getName().lastIndexOf(".") + 1).toLowerCase();
        String contentType = archivedResource.getContentType();
        String name = archivedResource.getName().substring(0, archivedResource.getName().lastIndexOf("."));
        ResourceData resourceData = new ResourceData();
        generateBaseResourceDataDto(archivedResource, resourceData, extension, contentType);
        resourceData.uuid(UUID.fromString(archivedResource.getId()))
            .type(TypeEnum.RESOURCE)
            .location(locationKey)
            .name(name);

        try (FileInputStream fis = new FileInputStream(body)) {
            return Uni.createFrom()
                .item(fis.readAllBytes())
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .map( fileData -> resourceData.data(Base64.getEncoder().encodeToString(fileData)))
                .flatMap(resource -> eloClient.createArchivedResource(resourceData)
                    .onFailure()
                    .transform(throwable ->  new CompasSclDataServiceException(CREATION_ERROR_CODE, String.format("Error while archiving referenced resource '%s' in ELO!", uuid)))
                    .onItem()
                    .invoke(Unchecked.consumer(item ->
                        {
                            try {
                                LOGGER.debug("returned archived related item {} ", objectMapper.writeValueAsString(item));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        })
                    ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateBaseResourceDataDto(IAbstractArchivedResourceMetaItem archivedResource, ResourceData resourceData, String extension, String contentType) {
        resourceData.version(archivedResource.getVersion())
            .extension(extension)
            .contentType(contentType);
        archivedResource.getFields().stream()
            .filter(field -> field.getValue() != null)
            .map(field ->
                new ResourceTag(field.getKey(), field.getValue()))
            .forEach(
                resourceData::addTagsItem
            );
    }

    @Override
    public Uni<ResourceMetaData> archiveSclData(UUID uuid, IAbstractArchivedResourceMetaItem archivedResource, String locationKey, String data) throws CompasSclDataServiceException {
        LOGGER.debug("Archiving scl resource '{}' in ELO!", uuid);

        String extension = archivedResource.getType();
        String contentType = MediaType.APPLICATION_OCTET_STREAM_TYPE.getType();
        String encodedDataString = Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
        ResourceData resourceData = new ResourceData();
        generateBaseResourceDataDto(archivedResource, resourceData, extension, contentType);
        resourceData.uuid(uuid)
            .type(TypeEnum.RESOURCE)
            .location(locationKey)
            .name(archivedResource.getName())
            .data(encodedDataString);

        return eloClient.createArchivedResource(resourceData)
            .onFailure()
            .transform(throwable -> new CompasSclDataServiceException(CREATION_ERROR_CODE, String.format("Error while archiving scl resource '%s' in ELO!", uuid)))
            .onItem()
            .invoke(Unchecked.consumer(item ->
                {
                    try {
                        LOGGER.debug("returned archived scl item {} ", objectMapper.writeValueAsString(item));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
            );
    }
}
