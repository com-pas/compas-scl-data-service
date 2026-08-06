// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lfenergy.compas.scl.data.exception.CompasInvalidInputException;
import org.lfenergy.compas.scl.data.entities.PluginsCustomResource;
import org.lfenergy.compas.scl.data.rest.PluginsCustomResourcesApi;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.DataEntry;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.DataEntryWithContent;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.PluginWithTypes;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.UploadDataResponse;
import org.lfenergy.compas.scl.data.service.CompasPluginsResourceService;
import org.lfenergy.compas.scl.data.service.UploadCustomPluginsResourceData;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequestScoped
@Blocking
public class CompasPluginsResource implements PluginsCustomResourcesApi {

    private static final Logger LOGGER = LogManager.getLogger(CompasPluginsResource.class);

    private final CompasPluginsResourceService service;

    @Inject
    public CompasPluginsResource(CompasPluginsResourceService service) {
        this.service = service;
    }

    @Override
    public List<PluginWithTypes> getPluginsWithTypes() {
        LOGGER.info("Listing plugins custom resource plugins with their types");

        return service.listPluginsWithTypes().entrySet().stream()
                .map(entry -> new PluginWithTypes()
                        .plugin(entry.getKey())
                        .types(entry.getValue()))
                .toList();
    }

    @Override
    public DataEntryWithContent getPluginResourceById(String plugin, String type, UUID id) {
        LOGGER.info("Getting plugins custom resource by plugin '{}', type '{}' and id '{}'", plugin, type, id);

        var entity = service.findByIdAndType(id, toQualifiedType(plugin, type));
        return toDataEntryWithContent(entity, type);
    }

    @Override
    public void deletePluginResourcesByType(String plugin, String type) {
        LOGGER.info("Deleting plugins custom resources by plugin '{}' and type '{}'", plugin, type);
        service.deleteByType(toQualifiedType(plugin, type));
    }

    @Override
    public List<DataEntry> getLatestPluginResourcesByType(String plugin, String type) {
        LOGGER.info("Getting latest plugins custom resources by plugin '{}' and type '{}'", plugin, type);

        return service.findLatestByType(toQualifiedType(plugin, type)).stream()
                .map(entity -> toDataEntry(entity, type))
                .toList();
    }

    @Override
    public void deletePluginResourceByName(String plugin, String type, String name) {
        LOGGER.info("Deleting plugins custom resources by plugin '{}', type '{}' and name '{}'", plugin, type, name);
        service.deleteByTypeAndName(toQualifiedType(plugin, type), name);
    }

    @Override
    public DataEntryWithContent getLatestPluginResourceByName(String plugin, String type, String name) {
        LOGGER.info("Getting latest plugins custom resource by plugin '{}', type '{}' and name '{}'", plugin, type, name);

        var entity = service.findLatestByTypeAndName(toQualifiedType(plugin, type), name);
        return toDataEntryWithContent(entity, type);
    }

    @Override
    public List<DataEntry> getPluginResourceVersionsByName(String plugin, String type, String name) {
        LOGGER.info("Getting plugins custom resource versions by plugin '{}', type '{}' and name '{}'", plugin, type, name);

        return service.findVersionsByTypeAndName(toQualifiedType(plugin, type), name).stream()
                .map(entity -> toDataEntry(entity, type))
                .toList();
    }

    @Override
    public UploadDataResponse createPluginResource(String plugin,
                                                   String type,
                                                   String name,
                                                   String contentType,
                                                   InputStream content,
                                                   String dataCompatibilityVersion,
                                                   String description,
                                                   String version,
                                                   String nextVersionType) {
        String contentText;
        try {
            contentText = new String(content.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CompasInvalidInputException("Failed to read content from upload");
        }

        var entity = service.upload(new UploadCustomPluginsResourceData(toQualifiedType(plugin, type), name, contentType, contentText,
                dataCompatibilityVersion, description, version, nextVersionType));

        var response = new UploadDataResponse();
        response.setId(entity.id);
        response.setType(type);
        response.setTenant(entity.tenant);
        response.setName(entity.name);
        response.setVersion(entity.version);
        response.setUploadedAt(toDate(entity.uploadedAt));
        return response;
    }

    private DataEntry toDataEntry(PluginsCustomResource entity, String type) {
        var entry = new DataEntry();
        mapCommonDataEntryFields(entry, entity, type);
        return entry;
    }

    private DataEntryWithContent toDataEntryWithContent(PluginsCustomResource entity, String type) {
        var entry = new DataEntryWithContent();
        mapCommonDataEntryFields(entry, entity, type);
        entry.setContent(entity.content);
        return entry;
    }


    private void mapCommonDataEntryFields(DataEntry entry, PluginsCustomResource entity, String type) {
        entry.setId(entity.id);
        entry.setType(type);
        entry.setTenant(entity.tenant);
        entry.setName(entity.name);
        entry.setDescription(entity.description);
        entry.setContentType(DataEntry.ContentTypeEnum.fromString(entity.contentType));
        entry.setVersion(entity.version);
        entry.setDataCompatibilityVersion(entity.dataCompatibilityVersion);
        entry.setUploadedAt(toDate(entity.uploadedAt));
    }

    private Date toDate(OffsetDateTime odt) {
        if (odt == null) return null;
        return Date.from(odt.toInstant());
    }

    private String toQualifiedType(String plugin, String type) {
        if (plugin == null || plugin.isBlank() || type == null || type.isBlank()) {
            throw new CompasInvalidInputException("Plugin and type must be provided");
        }
        if (plugin.contains("_") || type.contains("_")) {
            throw new CompasInvalidInputException("Plugin and type must not contain '_'");
        }
        return plugin + "_" + type;
    }
}
