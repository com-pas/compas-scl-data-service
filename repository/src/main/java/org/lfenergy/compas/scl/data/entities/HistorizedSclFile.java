// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.entities;

import io.quarkus.hibernate.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "historized_scl_file")
public class HistorizedSclFile extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    /**
     * The location this resource is assigned to. May be {@code null} if unassigned.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    public Location location;

    /**
     * The referenced {@code scl_file} entry, eagerly fetched.
     * {@code name}, {@code type}, and version components are read from this relationship.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
        @JoinColumn(name = "scl_file_id",             referencedColumnName = "id"),
        @JoinColumn(name = "scl_file_major_version",  referencedColumnName = "major_version"),
        @JoinColumn(name = "scl_file_minor_version",  referencedColumnName = "minor_version"),
        @JoinColumn(name = "scl_file_patch_version",  referencedColumnName = "patch_version")
    })
    public SclFile sclFile;

    @Column(name = "content_type", nullable = false)
    public String contentType;

    @Column(nullable = false)
    public String filename;

    @Column(columnDefinition = "text")
    public String comment;

    @CreationTimestamp
    @Column(name = "changed_at", nullable = false) // duplicated from scl_file created_at but this timestamp contains timezone
    public OffsetDateTime changedAt;

    @Column(nullable = false)
    public boolean archived = false;

    @Column(nullable = false)
    public boolean available = true;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        HistorizedSclFile that = (HistorizedSclFile) o;
        return archived == that.archived
                && available == that.available
                && Objects.equals(id, that.id)
                && Objects.equals(sclFile, that.sclFile)
                && Objects.equals(contentType, that.contentType)
                && Objects.equals(filename, that.filename)
                && Objects.equals(changedAt, that.changedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sclFile, contentType, filename, changedAt, archived, available);
    }
}
