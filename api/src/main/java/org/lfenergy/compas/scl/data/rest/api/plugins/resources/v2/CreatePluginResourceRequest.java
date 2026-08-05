// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2;

import jakarta.validation.constraints.*;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Request body for creating a new resource version. Either &#x60;version&#x60; or &#x60;nextVersionType&#x60; must be provided — they are mutually exclusive. 
 **/

@JsonTypeName("CreatePluginResourceRequest")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.12.0")
public class CreatePluginResourceRequest   {
  private String name;
  private String description;
  private ContentType contentType;
  private String version;
  public enum NextVersionTypeEnum {

    MAJOR(String.valueOf("major")), MINOR(String.valueOf("minor")), PATCH(String.valueOf("patch"));


    private String value;

    NextVersionTypeEnum (String v) {
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
    public static NextVersionTypeEnum fromString(String s) {
        for (NextVersionTypeEnum b : NextVersionTypeEnum.values()) {
            // using Objects.toString() to be safe if value type non-object type
            // because types like 'int' etc. will be auto-boxed
            if (java.util.Objects.toString(b.value).equals(s)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected string value '" + s + "'");
    }

    @JsonCreator
    public static NextVersionTypeEnum fromValue(String value) {
        for (NextVersionTypeEnum b : NextVersionTypeEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

  private NextVersionTypeEnum nextVersionType;
  private String dataCompatibilityVersion;
  private String content;

  public CreatePluginResourceRequest() {
  }

  @JsonCreator
  public CreatePluginResourceRequest(
    @JsonProperty(required = true, value = "name") String name,
    @JsonProperty(required = true, value = "contentType") ContentType contentType,
    @JsonProperty(required = true, value = "dataCompatibilityVersion") String dataCompatibilityVersion,
    @JsonProperty(required = true, value = "content") String content
  ) {
    this.name = name;
    this.contentType = contentType;
    this.dataCompatibilityVersion = dataCompatibilityVersion;
    this.content = content;
  }

  /**
   * Resource name. Must be unique within the plugin–type scope. Use the same name across versions to build a version history. 
   **/
  public CreatePluginResourceRequest name(String name) {
    this.name = name;
    return this;
  }

  
  @JsonProperty(required = true, value = "name")
  @NotNull  @Pattern(regexp="^[a-z][a-z0-9_-]*$")public String getName() {
    return name;
  }

  @JsonProperty(required = true, value = "name")
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Optional human-readable description of the resource.
   **/
  public CreatePluginResourceRequest description(String description) {
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
   **/
  public CreatePluginResourceRequest contentType(ContentType contentType) {
    this.contentType = contentType;
    return this;
  }

  
  @JsonProperty(required = true, value = "contentType")
  @NotNull public ContentType getContentType() {
    return contentType;
  }

  @JsonProperty(required = true, value = "contentType")
  public void setContentType(ContentType contentType) {
    this.contentType = contentType;
  }

  /**
   * Explicit semantic version for this resource version. Required when &#x60;nextVersionType&#x60; is not provided. 
   **/
  public CreatePluginResourceRequest version(String version) {
    this.version = version;
    return this;
  }

  
  @JsonProperty("version")
   @Pattern(regexp="^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$")public String getVersion() {
    return version;
  }

  @JsonProperty("version")
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Automatic version increment strategy applied to the current latest version of this resource. Required when &#x60;version&#x60; is not provided. 
   **/
  public CreatePluginResourceRequest nextVersionType(NextVersionTypeEnum nextVersionType) {
    this.nextVersionType = nextVersionType;
    return this;
  }

  
  @JsonProperty("nextVersionType")
  public NextVersionTypeEnum getNextVersionType() {
    return nextVersionType;
  }

  @JsonProperty("nextVersionType")
  public void setNextVersionType(NextVersionTypeEnum nextVersionType) {
    this.nextVersionType = nextVersionType;
  }

  /**
   * Data schema compatibility version for this resource version.
   **/
  public CreatePluginResourceRequest dataCompatibilityVersion(String dataCompatibilityVersion) {
    this.dataCompatibilityVersion = dataCompatibilityVersion;
    return this;
  }

  
  @JsonProperty(required = true, value = "dataCompatibilityVersion")
  @NotNull  @Pattern(regexp="^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$")public String getDataCompatibilityVersion() {
    return dataCompatibilityVersion;
  }

  @JsonProperty(required = true, value = "dataCompatibilityVersion")
  public void setDataCompatibilityVersion(String dataCompatibilityVersion) {
    this.dataCompatibilityVersion = dataCompatibilityVersion;
  }

  /**
   * The resource content as a UTF-8 string. The format must match &#x60;contentType&#x60;. 
   **/
  public CreatePluginResourceRequest content(String content) {
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
    CreatePluginResourceRequest createPluginResourceRequest = (CreatePluginResourceRequest) o;
    return Objects.equals(this.name, createPluginResourceRequest.name) &&
        Objects.equals(this.description, createPluginResourceRequest.description) &&
        Objects.equals(this.contentType, createPluginResourceRequest.contentType) &&
        Objects.equals(this.version, createPluginResourceRequest.version) &&
        Objects.equals(this.nextVersionType, createPluginResourceRequest.nextVersionType) &&
        Objects.equals(this.dataCompatibilityVersion, createPluginResourceRequest.dataCompatibilityVersion) &&
        Objects.equals(this.content, createPluginResourceRequest.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, contentType, version, nextVersionType, dataCompatibilityVersion, content);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CreatePluginResourceRequest {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    contentType: ").append(toIndentedString(contentType)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    nextVersionType: ").append(toIndentedString(nextVersionType)).append("\n");
    sb.append("    dataCompatibilityVersion: ").append(toIndentedString(dataCompatibilityVersion)).append("\n");
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

