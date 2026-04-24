// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import io.quarkus.hibernate.panache.managed.blocking.PanacheManagedBlockingRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.lfenergy.compas.scl.data.entities.Location;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class LocationRepository implements PanacheManagedBlockingRepositoryBase<Location, UUID> {

    /**
     * Returns true if another Location with the same key or name already exists.
     */
    public boolean hasDuplicateValues(String key, String name) {
        return count("key = ?1 or name = ?2", key, name) > 0;
    }

    /**
     * Returns true if another Location (different from excludeId) has the same key or name.
     */
    public boolean hasDuplicateValuesExcluding(String key, String name, UUID excludeId) {
        return count("(key = ?1 or name = ?2) and id <> ?3", key, name, excludeId) > 0;
    }

    /**
     * Returns a page of locations ordered by name.
     */
    public List<Location> listPaged(int page, int pageSize) {
        return findAll(Sort.by("name")).page(page, pageSize).list();
    }

    /**
     * Returns the total number of locations.
     */
    public long countTotal() {
        return count();
    }

    /**
     * Recalculates and persists the assignedResources counter for the given location
     * by counting distinct resource_ids in data_resource_history.
     * Should be called after every assign or unassign operation.
     */
    public void recalculateAssignedResources(UUID locationId) {
        getSession().createNativeMutationQuery(
            "UPDATE location " +
            "SET assigned_resources = (" +
            "  SELECT COUNT(DISTINCT scl_file_id) " +
            "  FROM historized_scl_file " +
            "  WHERE location_id = :locationId" +
            ") " +
            "WHERE id = :locationId")
            .setParameter("locationId", locationId)
            .executeUpdate();
    }
}
