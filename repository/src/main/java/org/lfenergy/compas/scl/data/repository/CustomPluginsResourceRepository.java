package org.lfenergy.compas.scl.data.repository;

import io.quarkus.hibernate.panache.managed.blocking.PanacheManagedBlockingRepositoryBase;
import org.lfenergy.compas.scl.data.entities.PluginsCustomResource;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

public class CustomPluginsResourceRepository implements PanacheManagedBlockingRepositoryBase<PluginsCustomResource, UUID> {

    public List<PluginsCustomResource> findAllVersionsByTypeAndName(String type, String name) {
        return list("type = ?1 and name = ?2 order by uploadedAt desc", type, name);
    }

    public Optional<PluginsCustomResource> findSpecificVersionByTypeAndName(String type, String name, String version) {
        return find("type = ?1 and name = ?2 and version = ?3", type, name, version).firstResultOptional();
    }

}
