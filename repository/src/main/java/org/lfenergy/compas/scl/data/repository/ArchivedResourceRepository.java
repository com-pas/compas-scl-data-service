// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import io.quarkus.hibernate.panache.managed.blocking.PanacheManagedBlockingRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.lfenergy.compas.scl.data.entities.ArchivedResource;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ArchivedResourceRepository implements PanacheManagedBlockingRepositoryBase<ArchivedResource, UUID> {

    /**
     * Returns all archived versions for the given resource UUID, newest first.
     */
    public List<ArchivedResource> findAllByResourceId(UUID resourceId) {
        return list("resourceId = ?1 order by archivedAt desc", resourceId);
    }

    /**
     * Returns the most recently archived entry for a resource, if any.
     */
    public Optional<ArchivedResource> findLatestByResourceId(UUID resourceId) {
        return find("resourceId = ?1 order by archivedAt desc", resourceId).firstResultOptional();
    }

    /**
     * Returns true if the given resource version has already been archived.
     */
    public boolean existsByResourceIdAndVersion(UUID resourceId, String version) {
        return count("resourceId = ?1 and version = ?2 and archived = true", resourceId, version) > 0;
    }

    /**
     * Searches archived resources with optional filter criteria.
     * Any parameter that is {@code null} or blank is ignored.
     */
    public List<ArchivedResource> searchByFilters(String location, String name, String approver,
                                                   String contentType, String type, String voltage,
                                                   OffsetDateTime from, OffsetDateTime to) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        appendCondition(sb, "location = :location", "location", location, params);
        if (name != null && !name.isBlank()) {
            appendCondition(sb, "lower(name) like :name", "name", "%" + name.toLowerCase() + "%", params);
        }
        if (approver != null && !approver.isBlank()) {
            appendCondition(sb, "lower(approver) like :approver", "approver",
                    "%" + approver.toLowerCase() + "%", params);
        }
        appendCondition(sb, "contentType = :contentType", "contentType", contentType, params);
        appendCondition(sb, "type = :type", "type", type, params);
        appendCondition(sb, "voltage = :voltage", "voltage", voltage, params);
        if (from != null) {
            appendCondition(sb, "archivedAt >= :from", "from", from, params);
        }
        if (to != null) {
            appendCondition(sb, "archivedAt <= :to", "to", to, params);
        }

        return sb.isEmpty() ? listAll() : list(sb.toString(), params);
    }

    private void appendCondition(StringBuilder sb, String clause, String paramName,
                                  Object value, Map<String, Object> params) {
        if (value instanceof String s && (s == null || s.isBlank())) {
            return;
        }
        if (value == null) {
            return;
        }
        if (!sb.isEmpty()) {
            sb.append(" and ");
        }
        sb.append(clause);
        params.put(paramName, value);
    }
}
