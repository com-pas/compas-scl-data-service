// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lfenergy.compas.scl.data.entities.PluginResource;
import org.lfenergy.compas.scl.data.exception.CompasDuplicateVersionException;
import org.lfenergy.compas.scl.data.exception.CompasInvalidInputException;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.PluginResourceRepository;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
public class PluginResourcesService {

    private static final Logger LOGGER = LogManager.getLogger(PluginResourcesService.class);
    private static final String DEFAULT_TENANT = "default";

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "application/json",
            "application/xml"
    );

    private final PluginResourceRepository pluginResourceRepository;

    @Inject
    public PluginResourcesService(PluginResourceRepository pluginResourceRepository) {
        this.pluginResourceRepository = pluginResourceRepository;
    }

    @Transactional(SUPPORTS)
    public PluginResource findById(String plugin, String type, UUID id) {
        return pluginResourceRepository.findByIdForPluginAndType(plugin, type, id)
                .orElseThrow(() -> new CompasNoDataFoundException(
                        String.format("No resource found for plugin '%s', type '%s' and id '%s'",
                                plugin, type, id)));
    }

    @Transactional(SUPPORTS)
    public List<PluginResource> findLatestByType(String plugin, String type) {
        List<PluginResource> entities = pluginResourceRepository.findAllByPluginAndType(plugin, type);

        if (entities.isEmpty()) {
            throw new CompasNoDataFoundException(
                    String.format("No resources found for plugin '%s' and type '%s'", plugin, type));
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
    public PluginResource findLatestByName(String plugin, String type, String name) {
        List<PluginResource> entities =
                pluginResourceRepository.findAllByPluginTypeAndName(plugin, type, name);

        if (entities.isEmpty()) {
            throw new CompasNoDataFoundException(
                    String.format("No resource found for plugin '%s', type '%s' and name '%s'",
                            plugin, type, name));
        }

        return entities.stream()
                .max(Comparator.comparing(entity -> new Version(entity.version)))
                .orElseThrow(() -> new CompasNoDataFoundException(
                        String.format("No resource found for plugin '%s', type '%s' and name '%s'",
                                plugin, type, name)));
    }

    @Transactional(SUPPORTS)
    public List<PluginResource> findVersionsByName(String plugin, String type, String name) {
        List<PluginResource> entities =
                pluginResourceRepository.findAllByPluginTypeAndName(plugin, type, name);

        if (entities.isEmpty()) {
            throw new CompasNoDataFoundException(
                    String.format("No versions found for plugin '%s', type '%s' and name '%s'",
                            plugin, type, name));
        }

        return entities.stream()
                .sorted(Comparator.comparing((PluginResource e) -> new Version(e.version)).reversed())
                .toList();
    }

    @Transactional(REQUIRED)
    public void deleteByType(String plugin, String type) {
        long deletedCount = pluginResourceRepository.deleteAllByPluginAndType(plugin, type);
        if (deletedCount == 0) {
            throw new CompasNoDataFoundException(
                    String.format("No resources found for plugin '%s' and type '%s'", plugin, type));
        }
    }

    @Transactional(REQUIRED)
    public void deleteByName(String plugin, String type, String name) {
        long deletedCount = pluginResourceRepository.deleteAllByPluginTypeAndName(plugin, type, name);
        if (deletedCount == 0) {
            throw new CompasNoDataFoundException(
                    String.format("No resource found for plugin '%s', type '%s' and name '%s'",
                            plugin, type, name));
        }
    }

    @Transactional(REQUIRED)
    public PluginResource create(CreatePluginResourceData request) {
        LOGGER.info("Creating plugin resource plugin='{}', type='{}', name='{}'",
                request.plugin(), request.type(), request.name());

        validateContentType(request.contentType());
        validateSemver(request.dataCompatibilityVersion(), "dataCompatibilityVersion");

        String resolvedVersion = resolveVersion(request.plugin(), request.type(), request.name(),
                request.version(), request.nextVersionType());

        if (pluginResourceRepository.existsByPluginTypeTenantNameAndVersion(
                request.plugin(), request.type(), DEFAULT_TENANT, request.name(), resolvedVersion)) {
            throw new CompasDuplicateVersionException(
                    String.format("Resource '%s' version '%s' already exists for plugin '%s' and type '%s'",
                            request.name(), resolvedVersion, request.plugin(), request.type()));
        }

        var entity = new PluginResource();
        entity.plugin = request.plugin();
        entity.type = request.type();
        entity.tenant = DEFAULT_TENANT;
        entity.name = request.name();
        entity.description = request.description();
        entity.contentType = request.contentType();
        entity.content = request.content();
        entity.version = resolvedVersion;
        entity.dataCompatibilityVersion = request.dataCompatibilityVersion();
        pluginResourceRepository.persist(entity);

        LOGGER.info("Persisted plugin resource with id '{}'", entity.id);
        return entity;
    }

    private String resolveVersion(String plugin, String type, String name,
                                  String explicitVersion, String nextVersionType) {
        boolean hasExplicit = explicitVersion != null && !explicitVersion.isBlank();
        boolean hasNext = nextVersionType != null && !nextVersionType.isBlank();

        if (hasExplicit && hasNext) {
            throw new CompasInvalidInputException(
                    "'version' and 'nextVersionType' are mutually exclusive");
        }
        if (hasExplicit) {
            validateSemver(explicitVersion, "version");
            return explicitVersion;
        }
        if (hasNext) {
            ChangeSetType changeSetType;
            try {
                changeSetType = ChangeSetType.valueOf(nextVersionType.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CompasInvalidInputException(
                        "Invalid nextVersionType: must be 'major', 'minor', or 'patch'");
            }
            return findLatestVersionAndIncrement(plugin, type, name, changeSetType);
        }
        throw new CompasInvalidInputException(
                "Either 'version' or 'nextVersionType' must be provided");
    }

    private String findLatestVersionAndIncrement(String plugin, String type, String name,
                                                 ChangeSetType changeSetType) {
        List<PluginResource> existing =
                pluginResourceRepository.findAllByPluginTypeAndName(plugin, type, name);

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
