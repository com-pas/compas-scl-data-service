// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lfenergy.compas.scl.data.exception.CompasDuplicateVersionException;
import org.lfenergy.compas.scl.data.exception.CompasInvalidInputException;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.entities.PluginsCustomResource;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CustomPluginsResourceRepository;
import org.lfenergy.compas.scl.data.util.UploadCustomPluginsResourceData;

import java.util.Comparator;
import java.util.*;
import java.util.stream.Collectors;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
public class CompasPluginsResourceService {

    private static final Logger LOGGER = LogManager.getLogger(CompasPluginsResourceService.class);
    private static final String DEFAULT_TENANT = "default";


    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "application/json",
            "application/xml"
    );

    private final CustomPluginsResourceRepository repository;

    @Inject
    public CompasPluginsResourceService(CustomPluginsResourceRepository repository) {
        this.repository = repository;
    }

    @Transactional(SUPPORTS)
    public List<PluginsCustomResource> list(String type, Date uploadedAfter, Date uploadedBefore,
                                            String name, int page, int size) {
        return repository.listFiltered(type, uploadedAfter, uploadedBefore, name, page, size);
    }

    @Transactional(SUPPORTS)
    public long count(String type, Date uploadedAfter, Date uploadedBefore, String name) {
        return repository.countFiltered(type, uploadedAfter, uploadedBefore, name);
    }

    @Transactional(SUPPORTS)
    public PluginsCustomResource findById(UUID id) {
        return repository.findByIdOptional(id).orElseThrow(() -> new CompasNoDataFoundException(
                String.format("Data entry with id '%s' not found", id)));
    }

    @Transactional(SUPPORTS)
    public List<PluginsCustomResource> findLatestByType(String type) {
        List<PluginsCustomResource> entities = repository.list("type", type);

        if (entities.isEmpty()) {
            throw new CompasNoDataFoundException(
                    String.format("No data entries found for type '%s'", type));
        }

        return entities.stream()
            .collect(Collectors.toMap(
                entity -> entity.name,
                entity -> entity,
                (left, right) -> new Version(left.version).compareTo(new Version(right.version)) >= 0 ? left : right,
                TreeMap::new))
            .values()
            .stream()
            .toList();
    }

    @Transactional(SUPPORTS)
    public PluginsCustomResource findLatestByTypeAndName(String type, String name) {
        List<PluginsCustomResource> entities = repository.list("type = ?1 and name = ?2", type, name);

        if (entities.isEmpty()) {
            throw new CompasNoDataFoundException(
                    String.format("No data entries found for type '%s' and name '%s'", type, name));
        }

        return entities.stream()
                .max(Comparator.comparing(entity -> new Version(entity.version)))
                .orElseThrow(() -> new CompasNoDataFoundException(
                        String.format("No data entries found for type '%s' and name '%s'", type, name)));
    }

    public List<PluginsCustomResource> getAllVersionsWithContentByTypeAndName(String dataType, String name) {
        List<PluginsCustomResource> entities = repository.findAllVersionsByTypeAndName(dataType, name);

        if (entities.isEmpty()) {
            throw new CompasNoDataFoundException(
                    String.format("No data entries found for type '%s' and name '%s'", dataType, name));
        }

        return entities;
    }

    public PluginsCustomResource getSpecificVersionByTypeAndName(String dataType, String name, String version) {
        return repository.findSpecificVersionByTypeAndName(dataType, name, version)
                .orElseThrow(() -> new CompasNoDataFoundException(
                    String.format("No data entry found for type '%s', name '%s' and version '%s'", dataType, name, version)
                )
        );
    }

    @Transactional(REQUIRED)
    public void deleteByType(String type) {
        long deletedCount = repository.delete("type = ?1", type);

        if (deletedCount == 0) {
            throw new CompasNoDataFoundException(
                    String.format("No data entries found for type '%s'", type));
        }
    }

    @Transactional(REQUIRED)
    public void deleteByTypeAndName(String type, String name) {
        long deletedCount = repository.delete("type = ?1 and name = ?2", type, name);

        if (deletedCount == 0) {
            throw new CompasNoDataFoundException(
                    String.format("No data entries found for type '%s' and name '%s'", type, name));
        }
    }

    @Transactional(REQUIRED)
    public PluginsCustomResource upload(UploadCustomPluginsResourceData request) {
        LOGGER.info("Uploading plugins custom resource type='{}', name='{}'", request.type(), request.name());

        validateContentType(request.contentType());
        validateSemver(request.dataCompatibilityVersion(), "data-compatibility-version");

        String resolvedVersion = resolveVersion(request.type(), request.name(), request.version(), request.nextVersionType());

        long duplicateCount = repository.countDuplicate(request.type(), DEFAULT_TENANT, request.name(), resolvedVersion);

        if (duplicateCount > 0) {
            throw new CompasDuplicateVersionException(
                    String.format("Data with name '%s' and version '%s' already exists for type '%s'",
                            request.name(), resolvedVersion, request.type()));
        }

        var entity = new PluginsCustomResource();
        entity.type = request.type();
        entity.tenant = DEFAULT_TENANT;
        entity.name = request.name();
        entity.description = request.description();
        entity.contentType = request.contentType();
        entity.content = request.content();
        entity.version = resolvedVersion;
        entity.dataCompatibilityVersion = request.dataCompatibilityVersion();
        repository.persist(entity);

        LOGGER.info("Persisted plugins custom resource with id '{}'", entity.id);
        return entity;
    }

    private String resolveVersion(String type, String name,
                                  String explicitVersion, String nextVersionType) {
        if (explicitVersion != null && !explicitVersion.isBlank()) {
            validateSemver(explicitVersion, "version");
            return explicitVersion;
        }
        if (nextVersionType != null && !nextVersionType.isBlank()) {
            ChangeSetType changeSetType;
            try {
                changeSetType = ChangeSetType.valueOf(nextVersionType.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CompasInvalidInputException(
                        "Invalid nextVersionType: must be 'major', 'minor', or 'patch'");
            }
            return findLatestVersionAndIncrement(type, name, changeSetType);
        }
        throw new CompasInvalidInputException(
                "Either 'version' or 'nextVersionType' must be provided");
    }

    private String findLatestVersionAndIncrement(String type, String name,
                                                 ChangeSetType changeSetType) {
        List<PluginsCustomResource> existing = repository.list("type = ?1 and tenant = ?2 and name = ?3", type, DEFAULT_TENANT, name);

        if (existing.isEmpty()) {
            return new Version(1, 0, 0).toString();
        }

        Version latest = existing.stream()
                .map(e -> new Version(e.version))
                .max(Version::compareTo)
                .orElse(new Version(1, 0, 0));

        return latest.getNextVersion(changeSetType).toString();
    }

    private void validateContentType(String contentType) {
        var normalizedContentType = contentType != null ? contentType.trim().toLowerCase() : "";
        if (!ALLOWED_CONTENT_TYPES.contains(normalizedContentType)) {
            throw new CompasInvalidInputException(
                    "Content type must be one of the following: " + String.join(", ", ALLOWED_CONTENT_TYPES));
        }
    }

    private void validateSemver(String version, String fieldName) {
        if (version == null || !version.matches(Version.PATTERN)) {
            throw new CompasInvalidInputException(
                    String.format("Invalid semantic version format for field '%s': '%s'",
                            fieldName, version));
        }
    }
}
