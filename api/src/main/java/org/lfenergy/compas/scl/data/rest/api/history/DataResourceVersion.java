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



@JsonTypeName("DataResourceVersion")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-04-24T08:07:06.426123+02:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class DataResourceVersion   {
  private UUID uuid;
  private String name;
  private String author;
  private String type;
  private OffsetDateTime changedAt;
  private String version;
  private Boolean available = true;
  private Boolean deleted = false;
  private String location;
  private String comment;
  private Boolean archived = false;

  /**
   * Unique identifier
   **/
  public DataResourceVersion uuid(UUID uuid) {
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
  public DataResourceVersion name(String name) {
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
  public DataResourceVersion author(String author) {
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
  public DataResourceVersion type(String type) {
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
  public DataResourceVersion changedAt(OffsetDateTime changedAt) {
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
  public DataResourceVersion version(String version) {
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
  public DataResourceVersion available(Boolean available) {
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
  public DataResourceVersion deleted(Boolean deleted) {
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
  public DataResourceVersion location(String location) {
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

  /**
   * Comment given when uploading the data resource
   **/
  public DataResourceVersion comment(String comment) {
    this.comment = comment;
    return this;
  }

  
  @JsonProperty("comment")
  public String getComment() {
    return comment;
  }

  @JsonProperty("comment")
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Defines if given data resource is archived
   **/
  public DataResourceVersion archived(Boolean archived) {
    this.archived = archived;
    return this;
  }

  
  @JsonProperty("archived")
  @NotNull public Boolean getArchived() {
    return archived;
  }

  @JsonProperty("archived")
  public void setArchived(Boolean archived) {
    this.archived = archived;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DataResourceVersion dataResourceVersion = (DataResourceVersion) o;
    return Objects.equals(this.uuid, dataResourceVersion.uuid) &&
        Objects.equals(this.name, dataResourceVersion.name) &&
        Objects.equals(this.author, dataResourceVersion.author) &&
        Objects.equals(this.type, dataResourceVersion.type) &&
        Objects.equals(this.changedAt, dataResourceVersion.changedAt) &&
        Objects.equals(this.version, dataResourceVersion.version) &&
        Objects.equals(this.available, dataResourceVersion.available) &&
        Objects.equals(this.deleted, dataResourceVersion.deleted) &&
        Objects.equals(this.location, dataResourceVersion.location) &&
        Objects.equals(this.comment, dataResourceVersion.comment) &&
        Objects.equals(this.archived, dataResourceVersion.archived);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, name, author, type, changedAt, version, available, deleted, location, comment, archived);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataResourceVersion {\n");
    
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    changedAt: ").append(toIndentedString(changedAt)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    available: ").append(toIndentedString(available)).append("\n");
    sb.append("    deleted: ").append(toIndentedString(deleted)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
    sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
    sb.append("    archived: ").append(toIndentedString(archived)).append("\n");
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

