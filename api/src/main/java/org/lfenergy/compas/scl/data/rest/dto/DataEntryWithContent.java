// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.dto;

import java.util.Date;
import java.util.UUID;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("DataEntryWithContent")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.12.0")
public class DataEntryWithContent   {
  private UUID id;
  private String type;
  private String tenant;
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

  public DataEntryWithContent() {
  }

  @JsonCreator
  public DataEntryWithContent(
    @JsonProperty(required = true, value = "id") UUID id,
    @JsonProperty(required = true, value = "type") String type,
    @JsonProperty(required = true, value = "tenant") String tenant,
    @JsonProperty(required = true, value = "name") String name,
    @JsonProperty(required = true, value = "contentType") ContentTypeEnum contentType,
    @JsonProperty(required = true, value = "version") String version,
    @JsonProperty(required = true, value = "dataCompatibilityVersion") String dataCompatibilityVersion,
    @JsonProperty(required = true, value = "uploadedAt") Date uploadedAt,
    @JsonProperty(required = true, value = "content") String content
  ) {
    this.id = id;
    this.type = type;
    this.tenant = tenant;
    this.name = name;
    this.contentType = contentType;
    this.version = version;
    this.dataCompatibilityVersion = dataCompatibilityVersion;
    this.uploadedAt = uploadedAt;
    this.content = content;
  }

  /**
   * Unique identifier for the data entry
   **/
  public DataEntryWithContent id(UUID id) {
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
   * Type of the data
   **/
  public DataEntryWithContent type(String type) {
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
   * Tenant identifier
   **/
  public DataEntryWithContent tenant(String tenant) {
    this.tenant = tenant;
    return this;
  }

  
  @JsonProperty(required = true, value = "tenant")
  @NotNull public String getTenant() {
    return tenant;
  }

  @JsonProperty(required = true, value = "tenant")
  public void setTenant(String tenant) {
    this.tenant = tenant;
  }

  /**
   * Name of the data file
   **/
  public DataEntryWithContent name(String name) {
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
   * Optional description of the data file
   **/
  public DataEntryWithContent description(String description) {
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
   * Content type of the file
   **/
  public DataEntryWithContent contentType(ContentTypeEnum contentType) {
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
   * Semantic version of the data file
   **/
  public DataEntryWithContent version(String version) {
    this.version = version;
    return this;
  }

  
  @JsonProperty(required = true, value = "version")
  @NotNull public String getVersion() {
    return version;
  }

  @JsonProperty(required = true, value = "version")
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Data compatibility version
   **/
  public DataEntryWithContent dataCompatibilityVersion(String dataCompatibilityVersion) {
    this.dataCompatibilityVersion = dataCompatibilityVersion;
    return this;
  }

  
  @JsonProperty(required = true, value = "dataCompatibilityVersion")
  @NotNull public String getDataCompatibilityVersion() {
    return dataCompatibilityVersion;
  }

  @JsonProperty(required = true, value = "dataCompatibilityVersion")
  public void setDataCompatibilityVersion(String dataCompatibilityVersion) {
    this.dataCompatibilityVersion = dataCompatibilityVersion;
  }

  /**
   * Timestamp when the data was uploaded
   **/
  public DataEntryWithContent uploadedAt(Date uploadedAt) {
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
   * The actual content of the file (JSON or XML as string)
   **/
  public DataEntryWithContent content(String content) {
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
    DataEntryWithContent dataEntryWithContent = (DataEntryWithContent) o;
    return Objects.equals(this.id, dataEntryWithContent.id) &&
        Objects.equals(this.type, dataEntryWithContent.type) &&
        Objects.equals(this.tenant, dataEntryWithContent.tenant) &&
        Objects.equals(this.name, dataEntryWithContent.name) &&
        Objects.equals(this.description, dataEntryWithContent.description) &&
        Objects.equals(this.contentType, dataEntryWithContent.contentType) &&
        Objects.equals(this.version, dataEntryWithContent.version) &&
        Objects.equals(this.dataCompatibilityVersion, dataEntryWithContent.dataCompatibilityVersion) &&
        Objects.equals(this.uploadedAt, dataEntryWithContent.uploadedAt) &&
        Objects.equals(this.content, dataEntryWithContent.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, tenant, name, description, contentType, version, dataCompatibilityVersion, uploadedAt, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataEntryWithContent {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    tenant: ").append(toIndentedString(tenant)).append("\n");
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

