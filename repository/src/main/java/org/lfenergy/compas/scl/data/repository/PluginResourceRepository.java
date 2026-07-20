// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import io.quarkus.hibernate.panache.managed.blocking.PanacheManagedBlockingRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.lfenergy.compas.scl.data.entities.v2.PluginResource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PluginResourceRepository
        implements PanacheManagedBlockingRepositoryBase<PluginResource, UUID> {

    /**
     * Returns the entity for the given id, restricted to the supplied plugin and type
     * so that a caller cannot fetch a resource that belongs to another plugin/type.
     */
    public Optional<PluginResource> findByIdForPluginAndType(String plugin, String type, UUID id) {
        return find("plugin = ?1 and type = ?2 and id = ?3", plugin, type, id).firstResultOptional();
    }

    /**
     * Returns every resource row for the given plugin and type. Reduction to the
     * latest version per name is performed in the service layer.
     */
    public List<PluginResource> findAllByPluginAndType(String plugin, String type) {
        return list("plugin = ?1 and type = ?2", plugin, type);
    }

    /**
     * Returns every resource row for the given plugin, type and name.
     * Ordered by uploadedAt descending as a stable default; callers may re-sort
     * by semver as needed.
     */
    public List<PluginResource> findAllByPluginTypeAndName(String plugin, String type, String name) {
        return list("plugin = ?1 and type = ?2 and name = ?3 order by uploadedAt desc",
                plugin, type, name);
    }

    /**
     * Returns true if an entry with the given plugin, type, tenant, name and version already exists.
     */
    public boolean existsByPluginTypeTenantNameAndVersion(String plugin, String type, String tenant,
                                                          String name, String version) {
        return count("plugin = ?1 and type = ?2 and tenant = ?3 and name = ?4 and version = ?5",
                plugin, type, tenant, name, version) > 0;
    }

    /**
     * Deletes every resource for the given plugin and type.
     *
     * @return the number of rows removed
     */
    public long deleteAllByPluginAndType(String plugin, String type) {
        return delete("plugin = ?1 and type = ?2", plugin, type);
    }

    /**
     * Deletes every resource for the given plugin, type and name.
     *
     * @return the number of rows removed
     */
    public long deleteAllByPluginTypeAndName(String plugin, String type, String name) {
        return delete("plugin = ?1 and type = ?2 and name = ?3", plugin, type, name);
    }
}
