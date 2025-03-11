package org.lfenergy.compas.scl.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;



@JsonTypeName("LocationMetaData")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-02-20T14:59:23.487447700+01:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class LocationMetaData   {
  private UUID uuid;
  private String key;
  private String name;
  private String description;
  private Integer assignedResources;

  /**
   **/
  public LocationMetaData uuid(UUID uuid) {
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
   **/
  public LocationMetaData key(String key) {
    this.key = key;
    return this;
  }

  
  @JsonProperty("key")
  @NotNull public String getKey() {
    return key;
  }

  @JsonProperty("key")
  public void setKey(String key) {
    this.key = key;
  }

  /**
   **/
  public LocationMetaData name(String name) {
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
   **/
  public LocationMetaData description(String description) {
    this.description = description;
    return this;
  }

  
  @JsonProperty("description")
  @NotNull public String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   **/
  public LocationMetaData assignedResources(Integer assignedResources) {
    this.assignedResources = assignedResources;
    return this;
  }

  
  @JsonProperty("assignedResources")
  @NotNull public Integer getAssignedResources() {
    return assignedResources;
  }

  @JsonProperty("assignedResources")
  public void setAssignedResources(Integer assignedResources) {
    this.assignedResources = assignedResources;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocationMetaData locationMetaData = (LocationMetaData) o;
    return Objects.equals(this.uuid, locationMetaData.uuid) &&
        Objects.equals(this.key, locationMetaData.key) &&
        Objects.equals(this.name, locationMetaData.name) &&
        Objects.equals(this.description, locationMetaData.description) &&
        Objects.equals(this.assignedResources, locationMetaData.assignedResources);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, key, name, description, assignedResources);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LocationMetaData {\n");
    
    sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    assignedResources: ").append(toIndentedString(assignedResources)).append("\n");
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

