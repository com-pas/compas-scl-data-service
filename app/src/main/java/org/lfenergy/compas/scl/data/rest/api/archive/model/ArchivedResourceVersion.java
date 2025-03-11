package org.lfenergy.compas.scl.data.rest.api.archive.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@JsonTypeName("ArchivedResourceVersion")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2024-12-06T09:13:22.882514600+01:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class ArchivedResourceVersion   {
  private String uuid;
  private String location;
  private String name;
  private String note;
  private String author;
  private String approver;
  private String type;
  private String contentType;
  private String voltage;
  private String version;
  private OffsetDateTime modifiedAt;
  private OffsetDateTime archivedAt;
  private @Valid List<@Valid ResourceTag> fields = new ArrayList<>();
  private String comment;
  private Boolean archived = false;

  /**
   * Unique resource identifier
   **/
  public ArchivedResourceVersion uuid(String uuid) {
    this.uuid = uuid;
    return this;
  }


  @JsonProperty("uuid")
  @NotNull public String getUuid() {
    return uuid;
  }

  @JsonProperty("uuid")
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  /**
   * Location of the resource, might be empty
   **/
  public ArchivedResourceVersion location(String location) {
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
   * Resource name
   **/
  public ArchivedResourceVersion name(String name) {
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
   * Versioning note
   **/
  public ArchivedResourceVersion note(String note) {
    this.note = note;
    return this;
  }


  @JsonProperty("note")
  public String getNote() {
    return note;
  }

  @JsonProperty("note")
  public void setNote(String note) {
    this.note = note;
  }

  /**
   * Modifying author
   **/
  public ArchivedResourceVersion author(String author) {
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
   * Name of the approver
   **/
  public ArchivedResourceVersion approver(String approver) {
    this.approver = approver;
    return this;
  }


  @JsonProperty("approver")
  public String getApprover() {
    return approver;
  }

  @JsonProperty("approver")
  public void setApprover(String approver) {
    this.approver = approver;
  }

  /**
   * Content type
   **/
  public ArchivedResourceVersion type(String type) {
    this.type = type;
    return this;
  }


  @JsonProperty("type")
  public String getType() {
    return type;
  }

  @JsonProperty("type")
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Content type
   **/
  public ArchivedResourceVersion contentType(String contentType) {
    this.contentType = contentType;
    return this;
  }


  @JsonProperty("contentType")
  @NotNull public String getContentType() {
    return contentType;
  }

  @JsonProperty("contentType")
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * Content type
   **/
  public ArchivedResourceVersion voltage(String voltage) {
    this.voltage = voltage;
    return this;
  }


  @JsonProperty("voltage")
  public String getVoltage() {
    return voltage;
  }

  @JsonProperty("voltage")
  public void setVoltage(String voltage) {
    this.voltage = voltage;
  }

  /**
   * Version
   **/
  public ArchivedResourceVersion version(String version) {
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
   **/
  public ArchivedResourceVersion modifiedAt(OffsetDateTime modifiedAt) {
    this.modifiedAt = modifiedAt;
    return this;
  }


  @JsonProperty("modifiedAt")
  @NotNull public OffsetDateTime getModifiedAt() {
    return modifiedAt;
  }

  @JsonProperty("modifiedAt")
  public void setModifiedAt(OffsetDateTime modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  /**
   **/
  public ArchivedResourceVersion archivedAt(OffsetDateTime archivedAt) {
    this.archivedAt = archivedAt;
    return this;
  }


  @JsonProperty("archivedAt")
  @NotNull public OffsetDateTime getArchivedAt() {
    return archivedAt;
  }

  @JsonProperty("archivedAt")
  public void setArchivedAt(OffsetDateTime archivedAt) {
    this.archivedAt = archivedAt;
  }

  /**
   **/
  public ArchivedResourceVersion fields(List<@Valid ResourceTag> fields) {
    this.fields = fields;
    return this;
  }


  @JsonProperty("fields")
  @NotNull @Valid public List<@Valid ResourceTag> getFields() {
    return fields;
  }

  @JsonProperty("fields")
  public void setFields(List<@Valid ResourceTag> fields) {
    this.fields = fields;
  }

  public ArchivedResourceVersion addFieldsItem(ResourceTag fieldsItem) {
    if (this.fields == null) {
      this.fields = new ArrayList<>();
    }

    this.fields.add(fieldsItem);
    return this;
  }

  public ArchivedResourceVersion removeFieldsItem(ResourceTag fieldsItem) {
    if (fieldsItem != null && this.fields != null) {
      this.fields.remove(fieldsItem);
    }

    return this;
  }
  /**
   * Comment given when uploading the data resource
   **/
  public ArchivedResourceVersion comment(String comment) {
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
  public ArchivedResourceVersion archived(Boolean archived) {
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
    ArchivedResourceVersion archivedResourceVersion = (ArchivedResourceVersion) o;
    return Objects.equals(this.uuid, archivedResourceVersion.uuid) &&
        Objects.equals(this.location, archivedResourceVersion.location) &&
        Objects.equals(this.name, archivedResourceVersion.name) &&
        Objects.equals(this.note, archivedResourceVersion.note) &&
        Objects.equals(this.author, archivedResourceVersion.author) &&
        Objects.equals(this.approver, archivedResourceVersion.approver) &&
        Objects.equals(this.type, archivedResourceVersion.type) &&
        Objects.equals(this.contentType, archivedResourceVersion.contentType) &&
        Objects.equals(this.voltage, archivedResourceVersion.voltage) &&
        Objects.equals(this.version, archivedResourceVersion.version) &&
        Objects.equals(this.modifiedAt, archivedResourceVersion.modifiedAt) &&
        Objects.equals(this.archivedAt, archivedResourceVersion.archivedAt) &&
        Objects.equals(this.fields, archivedResourceVersion.fields) &&
        Objects.equals(this.comment, archivedResourceVersion.comment) &&
        Objects.equals(this.archived, archivedResourceVersion.archived);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, location, name, note, author, approver, type, contentType, voltage, version, modifiedAt, archivedAt, fields, comment, archived);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ArchivedResourceVersion {\n");

    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    note: ").append(toIndentedString(note)).append("\n");
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
    sb.append("    approver: ").append(toIndentedString(approver)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    contentType: ").append(toIndentedString(contentType)).append("\n");
    sb.append("    voltage: ").append(toIndentedString(voltage)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    modifiedAt: ").append(toIndentedString(modifiedAt)).append("\n");
    sb.append("    archivedAt: ").append(toIndentedString(archivedAt)).append("\n");
    sb.append("    fields: ").append(toIndentedString(fields)).append("\n");
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

