// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.OffsetDateTime;
import java.util.Objects;



@JsonTypeName("DataResourceSearch")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-04-24T08:07:06.426123+02:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class DataResourceSearch   {
  private String uuid;
  private String type;
  private String name;
  private String location;
  private String author;
  private OffsetDateTime from;
  private OffsetDateTime to;

  /**
   * If uuid is set no other filter must be set
   **/
  public DataResourceSearch uuid(String uuid) {
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
   * Fulltext match set to one of the supported scl types: SSD, IID, ICD, SCD, CID, SED, ISD, STD, etc.
   **/
  public DataResourceSearch type(String type) {
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
   * Partially match allowed
   **/
  public DataResourceSearch name(String name) {
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
   * The location associated with the resource
   **/
  public DataResourceSearch location(String location) {
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
   * Fulltext match which can be retrieved via extra endpoint
   **/
  public DataResourceSearch author(String author) {
    this.author = author;
    return this;
  }

  
  @JsonProperty("author")
  public String getAuthor() {
    return author;
  }

  @JsonProperty("author")
  public void setAuthor(String author) {
    this.author = author;
  }

  /**
   * Starting date and time for filtering results. Use ISO 8601 format (e.g., 2024-10-22T14:48:00Z).
   **/
  public DataResourceSearch from(OffsetDateTime from) {
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
   * Ending date and time for filtering results. Use ISO 8601 format (e.g., 2024-10-22T14:48:00Z).
   **/
  public DataResourceSearch to(OffsetDateTime to) {
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
    DataResourceSearch dataResourceSearch = (DataResourceSearch) o;
    return Objects.equals(this.uuid, dataResourceSearch.uuid) &&
        Objects.equals(this.type, dataResourceSearch.type) &&
        Objects.equals(this.name, dataResourceSearch.name) &&
        Objects.equals(this.location, dataResourceSearch.location) &&
        Objects.equals(this.author, dataResourceSearch.author) &&
        Objects.equals(this.from, dataResourceSearch.from) &&
        Objects.equals(this.to, dataResourceSearch.to);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, type, name, location, author, from, to);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataResourceSearch {\n");
    
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    location: ").append(toIndentedString(location)).append("\n");
    sb.append("    author: ").append(toIndentedString(author)).append("\n");
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

