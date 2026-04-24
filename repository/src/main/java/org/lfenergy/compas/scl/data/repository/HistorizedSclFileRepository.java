// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import io.quarkus.hibernate.panache.managed.blocking.PanacheManagedBlockingRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.lfenergy.compas.scl.data.entities.HistorizedSclFile;
import org.lfenergy.compas.scl.data.entities.Location;
import org.lfenergy.compas.scl.data.entities.SclFile;
import org.lfenergy.compas.scl.data.entities.SclFileId;
import org.lfenergy.compas.scl.data.model.Version;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class HistorizedSclFileRepository implements PanacheManagedBlockingRepositoryBase<HistorizedSclFile, UUID> {

    /**
     * Creates and persists a new {@link HistorizedSclFile} row for the given SCL file version.
     * Uses a proxy reference to {@link SclFile} to avoid an extra SELECT.
     *
     * @param sclFileId   UUID of the {@code scl_file} row
     * @param version     version of the {@code scl_file} row
     * @param contentType MIME type of the SCL content (e.g. {@code "application/xml"})
     * @param filename    logical filename (e.g. {@code "substation.scd"})
     * @param comment     optional free-text comment
     */
    public void createEntry(UUID sclFileId, Version version, String contentType, String filename, String comment) {
        var key = new SclFileId();
        key.id = sclFileId;
        key.majorVersion = (short) version.getMajorVersion();
        key.minorVersion = (short) version.getMinorVersion();
        key.patchVersion = (short) version.getPatchVersion();

        var sclFile = getSession().getReference(SclFile.class, key);

        var entry = new HistorizedSclFile();
        entry.sclFile = sclFile;
        entry.contentType = contentType;
        entry.filename = filename;
        entry.comment = comment;
        persist(entry);
    }

    /**
     * Returns the latest version (by changedAt) for each distinct sclFileId.
     */
    public List<HistorizedSclFile> findAllLatest() {
        return getSession().createQuery(
                "from HistorizedSclFile r " +
                "where r.changedAt = (select max(r2.changedAt) from HistorizedSclFile r2 where r2.sclFile.id.id = r.sclFile.id.id) " +
                "order by r.sclFile.name",
                HistorizedSclFile.class)
            .getResultList();
    }

    /**
     * Returns all versions for a given SCL file UUID, newest first.
     */
    public List<HistorizedSclFile> findAllBySclFileId(UUID sclFileId) {
        return list("sclFile.id.id = ?1 order by changedAt desc", sclFileId);
    }

    /**
     * Returns the most recent HistorizedSclFile for a SCL file, if any.
     */
    public Optional<HistorizedSclFile> findLatestBySclFileId(UUID sclFileId) {
        return find("sclFile.id.id = ?1 order by changedAt desc", sclFileId).firstResultOptional();
    }

    /**
     * Returns the HistorizedSclFile for a specific SCL file UUID and version string,
     * if it exists.
     */
    public Optional<HistorizedSclFile> findBySclFileIdAndVersion(UUID sclFileId, String version) {
        var v = new Version(version);
        return find(
                "sclFile.id.id = ?1 and sclFile.id.majorVersion = ?2 and sclFile.id.minorVersion = ?3 and sclFile.id.patchVersion = ?4",
                sclFileId, (short) v.getMajorVersion(), (short) v.getMinorVersion(), (short) v.getPatchVersion())
            .firstResultOptional();
    }

    /**
     * Returns the number of referenced resource entries for the given SCL file UUID.
     */
    public long countBySclFileId(UUID sclFileId) {
        return count("sclFile.id.id = ?1", sclFileId);
    }

    /**
     * Assigns all referenced resource entries of a SCL file to a location.
     *
     * @param sclFileId the SCL file UUID
     * @param location  the Location entity to assign to
     */
    public void assignToLocation(UUID sclFileId, Location location) {
        update("location = ?1 where sclFile.id.id = ?2", location, sclFileId);
    }

    /**
     * Unassigns all referenced resource entries of a SCL file from a specific location.
     *
     * @param sclFileId  the SCL file UUID
     * @param locationId UUID of the location to unassign from
     */
    public void unassignFromLocation(UUID sclFileId, UUID locationId) {
        update("location = null where sclFile.id.id = ?1 and location.id = ?2", sclFileId, locationId);
    }

    /**
     * Searches for the latest version of each resource matching the supplied optional
     * filter criteria. Any parameter that is {@code null} or blank is ignored.
     */
    public List<HistorizedSclFile> searchLatest(String type, String name, String locationId,
                                                 String author, OffsetDateTime from, OffsetDateTime to) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sb = new StringBuilder(
                "from HistorizedSclFile r " +
                "where r.changedAt = (select max(r2.changedAt) from HistorizedSclFile r2 where r2.sclFile.id.id = r.sclFile.id.id)");

        if (type != null && !type.isBlank()) {
            sb.append(" and r.sclFile.type = :type");
            params.put("type", type);
        }
        if (name != null && !name.isBlank()) {
            sb.append(" and lower(r.sclFile.name) like :name");
            params.put("name", "%" + name.toLowerCase() + "%");
        }
        if (locationId != null && !locationId.isBlank()) {
            sb.append(" and r.location.id = :locationId");
            params.put("locationId", UUID.fromString(locationId));
        }
        if (author != null && !author.isBlank()) {
            sb.append(" and lower(r.author) like :author");
            params.put("author", "%" + author.toLowerCase() + "%");
        }
        if (from != null) {
            sb.append(" and r.changedAt >= :from");
            params.put("from", from);
        }
        if (to != null) {
            sb.append(" and r.changedAt <= :to");
            params.put("to", to);
        }
        sb.append(" order by r.sclFile.name");

        var query = getSession().createQuery(sb.toString(), HistorizedSclFile.class);
        params.forEach(query::setParameter);
        return query.getResultList();
    }
}
