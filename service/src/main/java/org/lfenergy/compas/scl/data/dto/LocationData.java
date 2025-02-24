package org.lfenergy.compas.scl.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;



@JsonTypeName("LocationData")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-02-20T14:59:23.487447700+01:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class LocationData   {
  private String key;
  private String name;
  private String description;

  /**
   **/
  public LocationData key(String key) {
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
  public LocationData name(String name) {
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
  public LocationData description(String description) {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LocationData locationData = (LocationData) o;
    return Objects.equals(this.key, locationData.key) &&
        Objects.equals(this.name, locationData.name) &&
        Objects.equals(this.description, locationData.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, name, description);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LocationData {\n");
    
    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
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

