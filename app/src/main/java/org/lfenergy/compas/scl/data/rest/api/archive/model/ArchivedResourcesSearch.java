package org.lfenergy.compas.scl.data.rest.api.archive.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.OffsetDateTime;
import java.util.Objects;



@JsonTypeName("ArchivedResourcesSearch")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2024-11-18T15:39:21.464141400+01:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class ArchivedResourcesSearch   {
  private String uuid;
  private String location;
  private String name;
  private String approver;
  private String contentType;
  private String type;
  private String voltage;
  private OffsetDateTime from;
  private OffsetDateTime to;

  /**
   * If uuid is set no other filter must be set
   **/
  public ArchivedResourcesSearch uuid(String uuid) {
    this.uuid = uuid;
    return this;
  }

  
  @JsonProperty("uuid")
  public String getUuid() {
    return uuid;
  }

  @JsonProperty("uuid")
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  /**
   * Exact match of a location
   **/
  public ArchivedResourcesSearch location(String location) {
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
   * Partially match allowed
   **/
  public ArchivedResourcesSearch name(String name) {
    this.name = name;
    return this;
  }

  
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Fulltext match which can be retrieved via extra endpoint
   **/
  public ArchivedResourcesSearch approver(String approver) {
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
   * Fulltext match set to one of the supported scl types: SSD, IID, ICD, SCD, CID, SED, ISD, STD, etc.
   **/
  public ArchivedResourcesSearch contentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  
  @JsonProperty("contentType")
  public String getContentType() {
    return contentType;
  }

  @JsonProperty("contentType")
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * Type of the documented entity eg. Schütz, Leittechnik, etc
   **/
  public ArchivedResourcesSearch type(String type) {
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
   * Voltage of the documented entity eg. 110, 220, 380
   **/
  public ArchivedResourcesSearch voltage(String voltage) {
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
   * Starting date from where resources have been archived. Use ISO 8601 format (e.g., 2024-10-22T14:48:00Z).
   **/
  public ArchivedResourcesSearch from(OffsetDateTime from) {
    this.from = from;
    return this;
  }

  
  @JsonProperty("from")
  public OffsetDateTime getFrom() {
    return from;
  }

  @JsonProperty("from")
  public void setFrom(OffsetDateTime from) {
    this.from = from;
  }

  /**
   * Ending date from where resources have been archived. Use ISO 8601 format (e.g., 2024-10-22T14:48:00Z).
   **/
  public ArchivedResourcesSearch to(OffsetDateTime to) {
    this.to = to;
    return this;
  }

  
  @JsonProperty("to")
  public OffsetDateTime getTo() {
    return to;
  }

  @JsonProperty("to")
  public void setTo(OffsetDateTime to) {
    this.to = to;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ArchivedResourcesSearch archivedResourcesSearch = (ArchivedResourcesSearch) o;
    return Objects.equals(this.uuid, archivedResourcesSearch.uuid) &&
        Objects.equals(this.location, archivedResourcesSearch.location) &&
        Objects.equals(this.name, archivedResourcesSearch.name) &&
        Objects.equals(this.approver, archivedResourcesSearch.approver) &&
        Objects.equals(this.contentType, archivedResourcesSearch.contentType) &&
        Objects.equals(this.type, archivedResourcesSearch.type) &&
        Objects.equals(this.voltage, archivedResourcesSearch.voltage) &&
        Objects.equals(this.from, archivedResourcesSearch.from) &&
        Objects.equals(this.to, archivedResourcesSearch.to);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, location, name, approver, contentType, type, voltage, from, to);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ArchivedResourcesSearch {\n");
    
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    approver: ").append(toIndentedString(approver)).append("\n");
    sb.append("    contentType: ").append(toIndentedString(contentType)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    voltage: ").append(toIndentedString(voltage)).append("\n");
    sb.append("    from: ").append(toIndentedString(from)).append("\n");
    sb.append("    to: ").append(toIndentedString(to)).append("\n");
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

