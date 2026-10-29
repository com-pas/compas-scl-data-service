// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2;

import java.util.Date;
import java.util.UUID;
import jakarta.validation.constraints.*;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Resource version metadata — content is not included.
 **/

@JsonTypeName("PluginResourceMeta")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.12.0")
public class PluginResourceMeta   {
  private UUID id;
  private String type;
  private String name;
  private String description;
  private ContentType contentType;
  private String version;
  private String dataCompatibilityVersion;
  private Date uploadedAt;

  public PluginResourceMeta() {
  }

  @JsonCreator
  public PluginResourceMeta(
    @JsonProperty(required = true, value = "id") UUID id,
    @JsonProperty(required = true, value = "type") String type,
    @JsonProperty(required = true, value = "name") String name,
    @JsonProperty(required = true, value = "contentType") ContentType contentType,
    @JsonProperty(required = true, value = "version") String version,
    @JsonProperty(required = true, value = "dataCompatibilityVersion") String dataCompatibilityVersion,
    @JsonProperty(required = true, value = "uploadedAt") Date uploadedAt
  ) {
    this.id = id;
    this.type = type;
    this.name = name;
    this.contentType = contentType;
    this.version = version;
    this.dataCompatibilityVersion = dataCompatibilityVersion;
    this.uploadedAt = uploadedAt;
  }

  /**
   * Unique identifier for this resource version.
   **/
  public PluginResourceMeta id(UUID id) {
    this.id = id;
    return this;
  }

  
  @JsonProperty(required = true, value = "id")
  @NotNull public UUID getId() {
    return id;
  }

  @JsonProperty(required = true, value = "id")
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   * Combined type identifier in the form &#x60;{plugin}_{type}&#x60;.
   **/
  public PluginResourceMeta type(String type) {
    this.type = type;
    return this;
  }

  
  @JsonProperty(required = true, value = "type")
  @NotNull public String getType() {
    return type;
  }

  @JsonProperty(required = true, value = "type")
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Human-readable resource name, unique within a plugin–type combination.
   **/
  public PluginResourceMeta name(String name) {
    this.name = name;
    return this;
  }

  
  @JsonProperty(required = true, value = "name")
  @NotNull public String getName() {
    return name;
  }

  @JsonProperty(required = true, value = "name")
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Optional human-readable description of the resource.
   **/
  public PluginResourceMeta description(String description) {
    this.description = description;
    return this;
  }

  
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   **/
  public PluginResourceMeta contentType(ContentType contentType) {
    this.contentType = contentType;
    return this;
  }

  
  @JsonProperty(required = true, value = "contentType")
  @NotNull public ContentType getContentType() {
    return contentType;
  }

  @JsonProperty(required = true, value = "contentType")
  public void setContentType(ContentType contentType) {
    this.contentType = contentType;
  }

  /**
   * Semantic version (semver) of this resource version.
   **/
  public PluginResourceMeta version(String version) {
    this.version = version;
    return this;
  }

  
  @JsonProperty(required = true, value = "version")
  @NotNull  @Pattern(regexp="^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$")public String getVersion() {
    return version;
  }

  @JsonProperty(required = true, value = "version")
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Data schema compatibility version. Consumers can use this to decide whether they can process the content without migration. 
   **/
  public PluginResourceMeta dataCompatibilityVersion(String dataCompatibilityVersion) {
    this.dataCompatibilityVersion = dataCompatibilityVersion;
    return this;
  }

  
  @JsonProperty(required = true, value = "dataCompatibilityVersion")
  @NotNull  @Pattern(regexp="^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$")public String getDataCompatibilityVersion() {
    return dataCompatibilityVersion;
  }

  @JsonProperty(required = true, value = "dataCompatibilityVersion")
  public void setDataCompatibilityVersion(String dataCompatibilityVersion) {
    this.dataCompatibilityVersion = dataCompatibilityVersion;
  }

  /**
   * ISO 8601 timestamp when this resource version was stored.
   **/
  public PluginResourceMeta uploadedAt(Date uploadedAt) {
    this.uploadedAt = uploadedAt;
    return this;
  }

  
  @JsonProperty(required = true, value = "uploadedAt")
  @NotNull public Date getUploadedAt() {
    return uploadedAt;
  }

  @JsonProperty(required = true, value = "uploadedAt")
  public void setUploadedAt(Date uploadedAt) {
    this.uploadedAt = uploadedAt;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PluginResourceMeta pluginResourceMeta = (PluginResourceMeta) o;
    return Objects.equals(this.id, pluginResourceMeta.id) &&
        Objects.equals(this.type, pluginResourceMeta.type) &&
        Objects.equals(this.name, pluginResourceMeta.name) &&
        Objects.equals(this.description, pluginResourceMeta.description) &&
        Objects.equals(this.contentType, pluginResourceMeta.contentType) &&
        Objects.equals(this.version, pluginResourceMeta.version) &&
        Objects.equals(this.dataCompatibilityVersion, pluginResourceMeta.dataCompatibilityVersion) &&
        Objects.equals(this.uploadedAt, pluginResourceMeta.uploadedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, name, description, contentType, version, dataCompatibilityVersion, uploadedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PluginResourceMeta {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    contentType: ").append(toIndentedString(contentType)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    dataCompatibilityVersion: ").append(toIndentedString(dataCompatibilityVersion)).append("\n");
    sb.append("    uploadedAt: ").append(toIndentedString(uploadedAt)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }


}

