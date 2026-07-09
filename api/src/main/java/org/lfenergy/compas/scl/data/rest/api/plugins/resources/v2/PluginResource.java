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
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("PluginResource")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.12.0")
public class PluginResource   {
  private UUID id;
  private String type;
  private String name;
  private String description;
  public enum ContentTypeEnum {

    APPLICATION_JSON(String.valueOf("application/json")), APPLICATION_XML(String.valueOf("application/xml"));


    private String value;

    ContentTypeEnum (String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Convert a String into String, as specified in the
     * <a href="https://download.oracle.com/otndocs/jcp/jaxrs-2_0-fr-eval-spec/index.html">See JAX RS 2.0 Specification, section 3.2, p. 12</a>
     */
    public static ContentTypeEnum fromString(String s) {
        for (ContentTypeEnum b : ContentTypeEnum.values()) {
            // using Objects.toString() to be safe if value type non-object type
            // because types like 'int' etc. will be auto-boxed
            if (java.util.Objects.toString(b.value).equals(s)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected string value '" + s + "'");
    }

    @JsonCreator
    public static ContentTypeEnum fromValue(String value) {
        for (ContentTypeEnum b : ContentTypeEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

  private ContentTypeEnum contentType;
  private String version;
  private String dataCompatibilityVersion;
  private Date uploadedAt;
  private String content;

  public PluginResource() {
  }

  @JsonCreator
  public PluginResource(
    @JsonProperty(required = true, value = "id") UUID id,
    @JsonProperty(required = true, value = "type") String type,
    @JsonProperty(required = true, value = "name") String name,
    @JsonProperty(required = true, value = "contentType") ContentTypeEnum contentType,
    @JsonProperty(required = true, value = "version") String version,
    @JsonProperty(required = true, value = "dataCompatibilityVersion") String dataCompatibilityVersion,
    @JsonProperty(required = true, value = "uploadedAt") Date uploadedAt,
    @JsonProperty(required = true, value = "content") String content
  ) {
    this.id = id;
    this.type = type;
    this.name = name;
    this.contentType = contentType;
    this.version = version;
    this.dataCompatibilityVersion = dataCompatibilityVersion;
    this.uploadedAt = uploadedAt;
    this.content = content;
  }

  /**
   * Unique identifier for this resource version.
   **/
  public PluginResource id(UUID id) {
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
  public PluginResource type(String type) {
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
  public PluginResource name(String name) {
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
  public PluginResource description(String description) {
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
   * Media type of the stored resource content.
   **/
  public PluginResource contentType(ContentTypeEnum contentType) {
    this.contentType = contentType;
    return this;
  }

  
  @JsonProperty(required = true, value = "contentType")
  @NotNull public ContentTypeEnum getContentType() {
    return contentType;
  }

  @JsonProperty(required = true, value = "contentType")
  public void setContentType(ContentTypeEnum contentType) {
    this.contentType = contentType;
  }

  /**
   * Semantic version (semver) of this resource version.
   **/
  public PluginResource version(String version) {
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
  public PluginResource dataCompatibilityVersion(String dataCompatibilityVersion) {
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
  public PluginResource uploadedAt(Date uploadedAt) {
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

  /**
   * The resource content encoded as a UTF-8 string. The format (JSON or XML) is indicated by the &#x60;contentType&#x60; field. 
   **/
  public PluginResource content(String content) {
    this.content = content;
    return this;
  }

  
  @JsonProperty(required = true, value = "content")
  @NotNull public String getContent() {
    return content;
  }

  @JsonProperty(required = true, value = "content")
  public void setContent(String content) {
    this.content = content;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PluginResource pluginResource = (PluginResource) o;
    return Objects.equals(this.id, pluginResource.id) &&
        Objects.equals(this.type, pluginResource.type) &&
        Objects.equals(this.name, pluginResource.name) &&
        Objects.equals(this.description, pluginResource.description) &&
        Objects.equals(this.contentType, pluginResource.contentType) &&
        Objects.equals(this.version, pluginResource.version) &&
        Objects.equals(this.dataCompatibilityVersion, pluginResource.dataCompatibilityVersion) &&
        Objects.equals(this.uploadedAt, pluginResource.uploadedAt) &&
        Objects.equals(this.content, pluginResource.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, name, description, contentType, version, dataCompatibilityVersion, uploadedAt, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PluginResource {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    contentType: ").append(toIndentedString(contentType)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    dataCompatibilityVersion: ").append(toIndentedString(dataCompatibilityVersion)).append("\n");
    sb.append("    uploadedAt: ").append(toIndentedString(uploadedAt)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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

