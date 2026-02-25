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
import org.lfenergy.compas.scl.data.model.PluginsCustomResource;
import org.lfenergy.compas.scl.data.rest.PluginsCustomResourcesApi;
import org.lfenergy.compas.scl.data.rest.dto.DataEntry;
import org.lfenergy.compas.scl.data.rest.dto.DataEntryWithContent;
import org.lfenergy.compas.scl.data.rest.dto.PagedDataEntryResponse;
import org.lfenergy.compas.scl.data.rest.dto.UploadDataResponse;
import org.lfenergy.compas.scl.data.service.CompasPluginsResourceService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public PagedDataEntryResponse getAllData(String type,
                                            Date uploadedAfter,
                                            Date uploadedBefore,
                                            String name,
                                            Integer page,
                                            Integer size) {
        LOGGER.info("Listing plugins custom resources for type '{}'", type);

        var entities = service.list(type, uploadedAfter, uploadedBefore, name, page, size);
        long totalElements = service.count(type, uploadedAfter, uploadedBefore, name);

        var entries = entities.stream()
                .map(this::toDataEntry)
                .collect(Collectors.toList());

        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;

        var response = new PagedDataEntryResponse();
        response.setContent(entries);
        response.setTotalElements((int) totalElements);
        response.setTotalPages(totalPages);
        response.setPage(page);
        response.setSize(size);
        return response;
    }

    @Override
    public DataEntryWithContent getDataById(UUID id) {
        LOGGER.info("Getting plugins custom resource by id '{}'", id);

        var entity = service.findById(id);
        return toDataEntryWithContent(entity);
    }

    @Override
    public UploadDataResponse uploadData(String type,
                                             String name,
                                             String contentType,
                                             InputStream contentInputStream,
                                             String dataCompatibilityVersion,
                                             String description,
                                             String version,
                                             String nextVersionType) {
        String content;
        try {
            content = new String(contentInputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CompasInvalidInputException("Failed to read content from upload");
        }

        var entity = service.upload(type, name, contentType, content,
                dataCompatibilityVersion, description, version, nextVersionType);

        var response = new UploadDataResponse();
        response.setId(entity.id);
        response.setType(entity.type);
        response.setTenant(entity.tenant);
        response.setName(entity.name);
        response.setVersion(entity.version);
        response.setUploadedAt(toDate(entity.uploadedAt));
        return response;
    }

    private DataEntry toDataEntry(PluginsCustomResource entity) {
        var entry = new DataEntry();
        entry.setId(entity.id);
        entry.setType(entity.type);
        entry.setTenant(entity.tenant);
        entry.setName(entity.name);
        entry.setDescription(entity.description);
        entry.setContentType(DataEntry.ContentTypeEnum.fromString(entity.contentType));
        entry.setVersion(entity.version);
        entry.setDataCompatibilityVersion(entity.dataCompatibilityVersion);
        entry.setUploadedAt(toDate(entity.uploadedAt));
        return entry;
    }

    private DataEntryWithContent toDataEntryWithContent(PluginsCustomResource entity) {
        var entry = new DataEntryWithContent();
        entry.setId(entity.id);
        entry.setType(entity.type);
        entry.setTenant(entity.tenant);
        entry.setName(entity.name);
        entry.setDescription(entity.description);
        entry.setContentType(DataEntryWithContent.ContentTypeEnum.fromString(entity.contentType));
        entry.setVersion(entity.version);
        entry.setDataCompatibilityVersion(entity.dataCompatibilityVersion);
        entry.setUploadedAt(toDate(entity.uploadedAt));
        entry.setContent(entity.content);
        return entry;
    }

    private Date toDate(OffsetDateTime odt) {
        if (odt == null) return null;
        return Date.from(odt.toInstant());
    }
}
