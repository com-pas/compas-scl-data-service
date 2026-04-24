// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.entities;

import io.quarkus.hibernate.panache.PanacheEntityBase;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "archived_resource_version")
public class ArchivedResource extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Column(name = "resource_id")
    public UUID resourceId;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false, length = 50)
    public String version;

    @Column(name = "location_id")
    public String locationId;

    @Column
    public String location;

    @Column(columnDefinition = "text")
    public String note;

    @Column(nullable = false)
    public String author;

    @Column
    public String approver;

    @Column(nullable = false, length = 50)
    public String type;

    @Column(name = "content_type", nullable = false, length = 50)
    public String contentType;

    @Column(length = 50)
    public String voltage;

    @Column(name = "modified_at", nullable = false)
    public OffsetDateTime modifiedAt;

    @Column(name = "archived_at", nullable = false)
    public OffsetDateTime archivedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "archived_resource_version_tag",
            joinColumns = @JoinColumn(name = "archived_resource_version_id")
    )
    public List<ResourceTag> fields = new ArrayList<>();

    @Column(columnDefinition = "text")
    public String archivingComment;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ArchivedResource that = (ArchivedResource) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(version, that.version)
                && Objects.equals(locationId, that.locationId)
                && Objects.equals(location, that.location)
                && Objects.equals(note, that.note)
                && Objects.equals(author, that.author)
                && Objects.equals(approver, that.approver)
                && Objects.equals(type, that.type)
                && Objects.equals(contentType, that.contentType)
                && Objects.equals(voltage, that.voltage)
                && Objects.equals(modifiedAt, that.modifiedAt)
                && Objects.equals(archivedAt, that.archivedAt)
                && Objects.equals(fields, that.fields)
                && Objects.equals(archivingComment, that.archivingComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, version, locationId, location, note, author, approver, type, contentType, voltage, modifiedAt, archivedAt, fields, archivingComment);
    }
}
