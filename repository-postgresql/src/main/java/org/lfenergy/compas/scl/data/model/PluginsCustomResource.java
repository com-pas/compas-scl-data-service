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
    private UUID id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String tenant = "default";

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "version", nullable = false, length = 50)
    private String version;

    @Column(name = "data_compatibility_version", nullable = false, length = 50)
    private String dataCompatibilityVersion;

    @Column(name = "uploaded_at", nullable = false)
    @CreationTimestamp
    private OffsetDateTime uploadedAt;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDataCompatibilityVersion() {
        return dataCompatibilityVersion;
    }

    public void setDataCompatibilityVersion(String dataCompatibilityVersion) {
        this.dataCompatibilityVersion = dataCompatibilityVersion;
    }

    public OffsetDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(OffsetDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

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
