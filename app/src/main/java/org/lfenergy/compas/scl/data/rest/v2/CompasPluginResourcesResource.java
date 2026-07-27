// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v2;

import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lfenergy.compas.scl.data.entities.v2.PluginResource;
import org.lfenergy.compas.scl.data.rest.PluginsResourcesApi;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.ContentType;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.CreatePluginResourceRequest;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.PluginResourceMeta;
import org.lfenergy.compas.scl.data.model.v2.CreatePluginResourceData;
import org.lfenergy.compas.scl.data.service.v2.PluginResourcesService;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Blocking
@RequestScoped
public class CompasPluginResourcesResource implements PluginsResourcesApi {

    private static final Logger LOGGER = LogManager.getLogger(CompasPluginResourcesResource.class);

    private final PluginResourcesService service;

    @Inject
    public CompasPluginResourcesResource(PluginResourcesService service) {
        this.service = service;
    }

    @Override
    public PluginResourceMeta createPluginResource(String plugin, String type,
                                                   CreatePluginResourceRequest request) {
        LOGGER.info("Creating plugin resource plugin='{}', type='{}', name='{}'",
                plugin, type, request.getName());
        var entity = service.create(new CreatePluginResourceData(
                plugin,
                type,
                request.getName(),
                request.getContentType().toString(),
                request.getContent(),
                request.getDataCompatibilityVersion(),
                request.getDescription(),
                request.getVersion(),
                request.getNextVersionType() != null ? request.getNextVersionType().value() : null
        ));
        return toMeta(entity);
    }

    @Override
    public void deletePluginResourcesByType(String plugin, String type) {
        LOGGER.info("Deleting all plugin resources for plugin='{}', type='{}'", plugin, type);
        service.deleteByType(plugin, type);
    }

    @Override
    public org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.PluginResource getPluginResourceById(
            String plugin, String type, UUID id) {
        LOGGER.debug("Getting plugin resource by id '{}' (plugin='{}', type='{}')", id, plugin, type);
        return toResource(service.findById(plugin, type, id));
    }

    @Override
    public List<PluginResourceMeta> getLatestPluginResourcesByType(String plugin, String type) {
        LOGGER.debug("Getting latest plugin resources for plugin='{}', type='{}'", plugin, type);
        return service.findLatestByType(plugin, type).stream()
                .map(this::toMeta)
                .toList();
    }

    @Override
    public void deletePluginResourceByName(String plugin, String type, String name) {
        LOGGER.info("Deleting plugin resource plugin='{}', type='{}', name='{}'", plugin, type, name);
        service.deleteByName(plugin, type, name);
    }

    @Override
    public org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.PluginResource getLatestPluginResourceByName(
            String plugin, String type, String name) {
        LOGGER.debug("Getting latest plugin resource plugin='{}', type='{}', name='{}'", plugin, type, name);
        return toResource(service.findLatestByName(plugin, type, name));
    }

    @Override
    public List<PluginResourceMeta> getPluginResourceVersionsByName(String plugin, String type, String name) {
        LOGGER.debug("Getting versions of plugin resource plugin='{}', type='{}', name='{}'",
                plugin, type, name);
        return service.findVersionsByName(plugin, type, name).stream()
                .map(this::toMeta)
                .toList();
    }

    private PluginResourceMeta toMeta(PluginResource entity) {
        var meta = new PluginResourceMeta();
        meta.setId(entity.id);
        meta.setType(entity.type);
        meta.setName(entity.name);
        meta.setDescription(entity.description);
        meta.setContentType(ContentType.fromString(entity.contentType));
        meta.setVersion(entity.version);
        meta.setDataCompatibilityVersion(entity.dataCompatibilityVersion);
        meta.setUploadedAt(toDate(entity.uploadedAt));
        return meta;
    }

    private org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.PluginResource toResource(
            PluginResource entity) {
        var dto = new org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.PluginResource();
        dto.setId(entity.id);
        dto.setType(entity.type);
        dto.setName(entity.name);
        dto.setDescription(entity.description);
        dto.setContentType(ContentType.fromString(entity.contentType));
        dto.setVersion(entity.version);
        dto.setDataCompatibilityVersion(entity.dataCompatibilityVersion);
        dto.setUploadedAt(toDate(entity.uploadedAt));
        dto.setContent(entity.content);
        return dto;
    }

    private Date toDate(OffsetDateTime odt) {
        if (odt == null) return null;
        return Date.from(odt.toInstant());
    }
}
