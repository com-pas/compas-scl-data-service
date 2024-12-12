package org.lfenergy.compas.scl.data.rest.api.archive.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;



@JsonTypeName("ResourceTag")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2024-11-18T07:52:46.875467800+01:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class ResourceTag   {
  private String key;
  private String value;

  /**
   * Tag key
   **/
  public ResourceTag key(String key) {
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
   * Tag value
   **/
  public ResourceTag value(String value) {
    this.value = value;
    return this;
  }


  @JsonProperty("value")
  @NotNull public String getValue() {
    return value;
  }

  @JsonProperty("value")
  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResourceTag resourceTag = (ResourceTag) o;
    return Objects.equals(this.key, resourceTag.key) &&
        Objects.equals(this.value, resourceTag.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ResourceTag {\n");

    sb.append("    key: ").append(toIndentedString(key)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

