package org.lfenergy.compas.scl.data.repository;

import io.quarkus.hibernate.panache.managed.blocking.PanacheManagedBlockingRepositoryBase;
import org.lfenergy.compas.scl.data.entities.PluginsCustomResource;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.time.ZoneOffset;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CustomPluginsResourceRepository implements PanacheManagedBlockingRepositoryBase<PluginsCustomResource, UUID> {

    public List<PluginsCustomResource> findAllVersionsByTypeAndName(String type, String name) {
        return list("type = ?1 and name = ?2 order by uploadedAt desc", type, name);
    }

    public Optional<PluginsCustomResource> findSpecificVersionByTypeAndName(String type, String name, String version) {
        return find("type = ?1 and name = ?2 and version = ?3", type, name, version).firstResultOptional();
    }

    public List<PluginsCustomResource> listFiltered(String type, Date uploadedAfter, Date uploadedBefore, String name, int page, int size) {
        var queryBuilder = new StringBuilder("type = :type");
        var params = new HashMap<String, Object>();
        params.put("type", type);
        appendFilters(queryBuilder, params, uploadedAfter, uploadedBefore, name);
        return find(queryBuilder + " order by uploadedAt desc", params)
                .page(page, size)
                .list();
    }

    public long countFiltered(String type, Date uploadedAfter, Date uploadedBefore, String name) {
        var queryBuilder = new StringBuilder("type = :type");
        var params = new HashMap<String, Object>();
        params.put("type", type);
        appendFilters(queryBuilder, params, uploadedAfter, uploadedBefore, name);
        return count(queryBuilder.toString(), params);
    }

    public long countDuplicate(String type, String tenant, String name, String version) {
        return count("type = ?1 and tenant = ?2 and name = ?3 and version = ?4", type, tenant, name, version);
    }

    private void appendFilters(StringBuilder queryBuilder, Map<String, Object> params,
                               Date uploadedAfter, Date uploadedBefore, String name) {
        if (uploadedAfter != null) {
            queryBuilder.append(" and uploadedAt >= :uploadedAfter");
            params.put("uploadedAfter", uploadedAfter.toInstant().atOffset(ZoneOffset.UTC));
        }
        if (uploadedBefore != null) {
            queryBuilder.append(" and uploadedAt <= :uploadedBefore");
            params.put("uploadedBefore", uploadedBefore.toInstant().atOffset(ZoneOffset.UTC));
        }
        if (name != null && !name.isBlank()) {
            queryBuilder.append(" and lower(name) like :name");
            params.put("name", "%" + name.toLowerCase() + "%");
        }
    }
}
