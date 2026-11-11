// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;



@JsonTypeName("DataResource")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-04-24T08:07:06.426123+02:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class DataResource   {
  private UUID uuid;
  private String name;
  private String author;
  private String type;
  private OffsetDateTime changedAt;
  private String version;
  private Boolean available = true;
  private Boolean deleted = false;
  private String location;

  /**
   * Unique identifier
   **/
  public DataResource uuid(UUID uuid) {
    this.uuid = uuid;
    return this;
  }

  
  @JsonProperty("uuid")
  @NotNull public UUID getUuid() {
    return uuid;
  }

  @JsonProperty("uuid")
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  /**
   * Name of the resource
   **/
  public DataResource name(String name) {
    this.name = name;
    return this;
  }

  
  @JsonProperty("name")
  @NotNull public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Name of the author last changed the document
   **/
  public DataResource author(String author) {
    this.author = author;
    return this;
  }

  
  @JsonProperty("author")
  @NotNull public String getAuthor() {
    return author;
  }

  @JsonProperty("author")
  public void setAuthor(String author) {
    this.author = author;
  }

  /**
   * One of the supported types: SSD, IID, ICD, SCD, CID, SED, ISD, STD, etc.
   **/
  public DataResource type(String type) {
    this.type = type;
    return this;
  }

  
  @JsonProperty("type")
  @NotNull public String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Point in time of last modification/upload
   **/
  public DataResource changedAt(OffsetDateTime changedAt) {
    this.changedAt = changedAt;
    return this;
  }

  
  @JsonProperty("changedAt")
  @NotNull public OffsetDateTime getChangedAt() {
    return changedAt;
  }

  @JsonProperty("changedAt")
  public void setChangedAt(OffsetDateTime changedAt) {
    this.changedAt = changedAt;
  }

  /**
   * Generated version by the scl-data-service
   **/
  public DataResource version(String version) {
    this.version = version;
    return this;
  }

  
  @JsonProperty("version")
  @NotNull public String getVersion() {
    return version;
  }

  @JsonProperty("version")
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Defines if a resource is available as download or version was created while uploading a file
   **/
  public DataResource available(Boolean available) {
    this.available = available;
    return this;
  }

  
  @JsonProperty("available")
  @NotNull public Boolean getAvailable() {
    return available;
  }

  @JsonProperty("available")
  public void setAvailable(Boolean available) {
    this.available = available;
  }

  /**
   * Defines if a resource is marked as deleted
   **/
  public DataResource deleted(Boolean deleted) {
    this.deleted = deleted;
    return this;
  }

  
  @JsonProperty("deleted")
  @NotNull public Boolean getDeleted() {
    return deleted;
  }

  @JsonProperty("deleted")
  public void setDeleted(Boolean deleted) {
    this.deleted = deleted;
  }

  /**
   * The location associated with the resource
   **/
  public DataResource location(String location) {
    this.location = location;
    return this;
  }

  
  @JsonProperty("location")
  public String getLocation() {
    return location;
  }

  @JsonProperty("location")
  public void setLocation(String location) {
    this.location = location;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DataResource dataResource = (DataResource) o;
    return Objects.equals(this.uuid, dataResource.uuid) &&
        Objects.equals(this.name, dataResource.name) &&
        Objects.equals(this.author, dataResource.author) &&
        Objects.equals(this.type, dataResource.type) &&
        Objects.equals(this.changedAt, dataResource.changedAt) &&
        Objects.equals(this.version, dataResource.version) &&
        Objects.equals(this.available, dataResource.available) &&
        Objects.equals(this.deleted, dataResource.deleted) &&
        Objects.equals(this.location, dataResource.location);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, name, author, type, changedAt, version, available, deleted, location);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataResource {\n");
    
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    changedAt: ").append(toIndentedString(changedAt)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    available: ").append(toIndentedString(available)).append("\n");
    sb.append("    deleted: ").append(toIndentedString(deleted)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
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

