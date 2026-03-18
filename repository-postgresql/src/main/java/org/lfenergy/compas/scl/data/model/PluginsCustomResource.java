// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import io.quarkus.hibernate.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "plugins_custom_resource")
public class PluginsCustomResource extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Column(nullable = false)
    public String type;

    @Column(nullable = false)
    public String tenant = "default";

    @Column(nullable = false)
    public String name;

    @Column(columnDefinition = "text")
    public String description;

    @Column(name = "content_type", nullable = false, length = 50)
    public String contentType;

    @Column(nullable = false, columnDefinition = "text")
    public String content;

    @Column(name = "version", nullable = false, length = 50)
    public String version;

    @Column(name = "data_compatibility_version", nullable = false, length = 50)
    public String dataCompatibilityVersion;

    @Column(name = "uploaded_at", nullable = false)
    @CreationTimestamp
    public OffsetDateTime uploadedAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PluginsCustomResource that = (PluginsCustomResource) o;
        return Objects.equals(id, that.id) && Objects.equals(type, that.type) && Objects.equals(tenant, that.tenant) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(contentType, that.contentType) && Objects.equals(content, that.content) && Objects.equals(version, that.version) && Objects.equals(dataCompatibilityVersion, that.dataCompatibilityVersion) && Objects.equals(uploadedAt, that.uploadedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, tenant, name, description, contentType, content, version, dataCompatibilityVersion, uploadedAt);
    }
}
