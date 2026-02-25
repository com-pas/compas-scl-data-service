// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lfenergy.compas.scl.data.exception.CompasDuplicateVersionException;
import org.lfenergy.compas.scl.data.exception.CompasInvalidInputException;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.PluginsCustomResource;
import org.lfenergy.compas.scl.data.model.Version;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
public class CompasPluginsResourceService {

    private static final Logger LOGGER = LogManager.getLogger(CompasPluginsResourceService.class);
    private static final String DEFAULT_TENANT = "default";

    private final EntityManager entityManager;

    @Inject
    public CompasPluginsResourceService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(SUPPORTS)
    public List<PluginsCustomResource> list(String type, Date uploadedAfter, Date uploadedBefore,
                                            String name, int page, int size) {
        var queryBuilder = new StringBuilder("SELECT e FROM PluginsCustomResource e WHERE e.type = :type");
        var params = new HashMap<String, Object>();
        params.put("type", type);

        appendFilters(queryBuilder, params, uploadedAfter, uploadedBefore, name);
        queryBuilder.append(" ORDER BY e.uploadedAt DESC");

        TypedQuery<PluginsCustomResource> query = entityManager.createQuery(queryBuilder.toString(), PluginsCustomResource.class);
        params.forEach(query::setParameter);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Transactional(SUPPORTS)
    public long count(String type, Date uploadedAfter, Date uploadedBefore, String name) {
        var queryBuilder = new StringBuilder("SELECT COUNT(e) FROM PluginsCustomResource e WHERE e.type = :type");
        var params = new HashMap<String, Object>();
        params.put("type", type);

        appendFilters(queryBuilder, params, uploadedAfter, uploadedBefore, name);

        TypedQuery<Long> query = entityManager.createQuery(queryBuilder.toString(), Long.class);
        params.forEach(query::setParameter);
        return query.getSingleResult();
    }

    @Transactional(SUPPORTS)
    public PluginsCustomResource findById(UUID id) {
        PluginsCustomResource entity = entityManager.find(PluginsCustomResource.class, id);
        if (entity == null) {
            throw new CompasNoDataFoundException(
                    String.format("Data entry with id '%s' not found", id));
        }
        return entity;
    }

    @Transactional(REQUIRED)
    public PluginsCustomResource upload(String type, String name, String contentType, String content,
                                        String dataCompatibilityVersion, String description,
                                        String version, String nextVersionType) {
        LOGGER.info("Uploading plugins custom resource type='{}', name='{}'", type, name);

        validateContentType(contentType);
        validateSemver(dataCompatibilityVersion, "data-compatibility-version");

        String resolvedVersion = resolveVersion(type, name, version, nextVersionType);

        Long duplicateCount = entityManager.createQuery(
                        "SELECT COUNT(e) FROM PluginsCustomResource e " +
                                "WHERE e.type = :type AND e.tenant = :tenant AND e.name = :name AND e.version = :version",
                        Long.class)
                .setParameter("type", type)
                .setParameter("tenant", DEFAULT_TENANT)
                .setParameter("name", name)
                .setParameter("version", resolvedVersion)
                .getSingleResult();

        if (duplicateCount > 0) {
            throw new CompasDuplicateVersionException(
                    String.format("Data with name '%s' and version '%s' already exists for type '%s'",
                            name, resolvedVersion, type));
        }

        var entity = new PluginsCustomResource();
        entity.type = type;
        entity.tenant = DEFAULT_TENANT;
        entity.name = name;
        entity.description = description;
        entity.contentType = contentType;
        entity.content = content;
        entity.version = resolvedVersion;
        entity.dataCompatibilityVersion = dataCompatibilityVersion;
        entityManager.persist(entity);

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
        List<PluginsCustomResource> existing = entityManager.createQuery(
                        "SELECT e FROM PluginsCustomResource e " +
                                "WHERE e.type = :type AND e.tenant = :tenant AND e.name = :name",
                        PluginsCustomResource.class)
                .setParameter("type", type)
                .setParameter("tenant", DEFAULT_TENANT)
                .setParameter("name", name)
                .getResultList();

        if (existing.isEmpty()) {
            return new Version(1, 0, 0).toString();
        }

        Version latest = existing.stream()
                .map(e -> new Version(e.version))
                .max(Version::compareTo)
                .orElse(new Version(1, 0, 0));

        return latest.getNextVersion(changeSetType).toString();
    }

    private void appendFilters(StringBuilder queryBuilder, Map<String, Object> params,
                               Date uploadedAfter, Date uploadedBefore, String name) {
        if (uploadedAfter != null) {
            queryBuilder.append(" AND e.uploadedAt >= :uploadedAfter");
            params.put("uploadedAfter", toOffsetDateTime(uploadedAfter));
        }
        if (uploadedBefore != null) {
            queryBuilder.append(" AND e.uploadedAt <= :uploadedBefore");
            params.put("uploadedBefore", toOffsetDateTime(uploadedBefore));
        }
        if (name != null && !name.isBlank()) {
            queryBuilder.append(" AND LOWER(e.name) LIKE :name");
            params.put("name", "%" + name.toLowerCase() + "%");
        }
    }

    private void validateContentType(String contentType) {
        if (contentType == null ||
                (!"application/json".equals(contentType) && !"application/xml".equals(contentType))) {
            throw new CompasInvalidInputException(
                    "Content type must be 'application/json' or 'application/xml'");
        }
    }

    private void validateSemver(String version, String fieldName) {
        if (version == null || !version.matches(Version.PATTERN)) {
            throw new CompasInvalidInputException(
                    String.format("Invalid semantic version format for field '%s': '%s'",
                            fieldName, version));
        }
    }

    private OffsetDateTime toOffsetDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atOffset(ZoneOffset.UTC);
    }
}
