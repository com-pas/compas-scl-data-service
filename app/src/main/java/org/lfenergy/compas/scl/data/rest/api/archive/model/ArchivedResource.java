package org.lfenergy.compas.scl.data.rest.api.archive.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@JsonTypeName("ArchivedResource")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2024-11-18T15:39:21.464141400+01:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class ArchivedResource   {
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

  /**
   * Unique resource identifier
   **/
  public ArchivedResource uuid(String uuid) {
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
  public ArchivedResource location(String location) {
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
  public ArchivedResource name(String name) {
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
  public ArchivedResource note(String note) {
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
  public ArchivedResource author(String author) {
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
  public ArchivedResource approver(String approver) {
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
  public ArchivedResource type(String type) {
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
  public ArchivedResource contentType(String contentType) {
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
  public ArchivedResource voltage(String voltage) {
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
  public ArchivedResource version(String version) {
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
  public ArchivedResource modifiedAt(OffsetDateTime modifiedAt) {
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
  public ArchivedResource archivedAt(OffsetDateTime archivedAt) {
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
  public ArchivedResource fields(List<@Valid ResourceTag> fields) {
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

  public ArchivedResource addFieldsItem(ResourceTag fieldsItem) {
    if (this.fields == null) {
      this.fields = new ArrayList<>();
    }

    this.fields.add(fieldsItem);
    return this;
  }

  public ArchivedResource removeFieldsItem(ResourceTag fieldsItem) {
    if (fieldsItem != null && this.fields != null) {
      this.fields.remove(fieldsItem);
    }

    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArchivedResource archivedResource = (ArchivedResource) o;
    return Objects.equals(this.uuid, archivedResource.uuid) &&
        Objects.equals(this.location, archivedResource.location) &&
        Objects.equals(this.name, archivedResource.name) &&
        Objects.equals(this.note, archivedResource.note) &&
        Objects.equals(this.author, archivedResource.author) &&
        Objects.equals(this.approver, archivedResource.approver) &&
        Objects.equals(this.type, archivedResource.type) &&
        Objects.equals(this.contentType, archivedResource.contentType) &&
        Objects.equals(this.voltage, archivedResource.voltage) &&
        Objects.equals(this.version, archivedResource.version) &&
        Objects.equals(this.modifiedAt, archivedResource.modifiedAt) &&
        Objects.equals(this.archivedAt, archivedResource.archivedAt) &&
        Objects.equals(this.fields, archivedResource.fields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, location, name, note, author, approver, type, contentType, voltage, version, modifiedAt, archivedAt, fields);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ArchivedResource {\n");

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

