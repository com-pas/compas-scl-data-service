package org.lfenergy.compas.scl.data.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.lfenergy.compas.scl.data.dto.ResourceData;
import org.lfenergy.compas.scl.data.dto.ResourceMetaData;
import org.lfenergy.compas.scl.data.dto.ResourceTag;
import org.lfenergy.compas.scl.data.dto.TypeEnum;
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

    @Inject
    @RestClient
    IEloConnectorRestClient eloClient;

    @Override
    public void createLocation(ILocationMetaItem location) {
        LOGGER.info("Creating a new location in ELO!");
    }

    @Override
    public Uni<ResourceMetaData> archiveData(String locationName, String filename, UUID uuid, File body, IAbstractArchivedResourceMetaItem archivedResource) {
        LOGGER.debug("Archiving related resource in ELO!");

        ResourceData resourceData = new ResourceData();
        String extension = archivedResource.getName().substring(archivedResource.getName().lastIndexOf(".") + 1).toLowerCase();
        String contentType = archivedResource.getContentType();
        generateBaseResourceDataDto(archivedResource, resourceData, extension, contentType);
        resourceData.uuid(uuid);
        resourceData.type(TypeEnum.RESOURCE);
        resourceData.location(locationName);
        String name = archivedResource.getName().substring(0, archivedResource.getName().lastIndexOf("."));
        resourceData.name(name);

        try (FileInputStream fis = new FileInputStream(body)) {
            byte[] fileData = new byte[(int) body.length()];
            fis.read(fileData);
            resourceData.data(Base64.getEncoder().encodeToString(fileData));
            return eloClient.createArchivedResource(resourceData)
                .onFailure()
                .transform(throwable ->  new CompasSclDataServiceException(CREATION_ERROR_CODE, String.format("Error while archiving referenced resource '%s' in ELO!", uuid)))
                .onItem()
                .invoke(item ->
                    LOGGER.debug("returned archived related item {} ", item)
                );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void generateBaseResourceDataDto(IAbstractArchivedResourceMetaItem archivedResource, ResourceData resourceData, String extension, String contentType) {
        resourceData.version(archivedResource.getVersion());
        resourceData.extension(extension);
        resourceData.contentType(contentType);
        archivedResource.getFields().stream()
            .filter(field -> field.getValue() != null)
            .map(field ->
                new ResourceTag(field.getKey(), field.getValue()))
            .forEach(
                resourceData::addTagsItem
            );
    }

    @Override
    public Uni<ResourceMetaData> archiveSclData(UUID uuid, IAbstractArchivedResourceMetaItem archivedResource, String locationName, String data) throws CompasSclDataServiceException {
        LOGGER.debug("Archiving scl resource in ELO!");

        ResourceData resourceData = new ResourceData();
        String extension = archivedResource.getType();
        String contentType = MediaType.APPLICATION_OCTET_STREAM_TYPE.getType();
        generateBaseResourceDataDto(archivedResource, resourceData, extension, contentType);
        resourceData.uuid(uuid);
        resourceData.type(TypeEnum.RESOURCE);
        resourceData.location(locationName);
        resourceData.name(archivedResource.getName());
        String encodedDataString = Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
        resourceData.data(encodedDataString);

        return eloClient.createArchivedResource(resourceData)
            .onFailure()
            .transform(throwable -> new CompasSclDataServiceException(CREATION_ERROR_CODE, String.format("Error while archiving scl resource '%s' in ELO!", uuid)))
            .onItem()
            .invoke(item ->
                LOGGER.debug("returned archived scl item {} ", item)
            );
    }
}
