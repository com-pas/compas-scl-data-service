// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources;

import java.util.Date;
import java.util.UUID;

import jakarta.validation.constraints.*;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("DataEntryWithContent")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.12.0")
public class DataEntryWithContent extends DataEntry  {
  private String content;

  public DataEntryWithContent() {
  }

  @JsonCreator
  public DataEntryWithContent(
    @JsonProperty(required = true, value = "content") String content,
    @JsonProperty(required = true, value = "id") UUID id,
    @JsonProperty(required = true, value = "type") String type,
    @JsonProperty(required = true, value = "tenant") String tenant,
    @JsonProperty(required = true, value = "name") String name,
    @JsonProperty(required = true, value = "contentType") ContentTypeEnum contentType,
    @JsonProperty(required = true, value = "version") String version,
    @JsonProperty(required = true, value = "dataCompatibilityVersion") String dataCompatibilityVersion,
    @JsonProperty(required = true, value = "uploadedAt") Date uploadedAt
  ) {
    super(
      id,
      type,
      tenant,
      name,
      contentType,
      version,
      dataCompatibilityVersion,
      uploadedAt
    );
    this.content = content;
  }

  /**
   * The actual content of the file (JSON or XML as string)
   **/
  public DataEntryWithContent content(String content) {
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
    DataEntryWithContent dataEntryWithContent = (DataEntryWithContent) o;
    return Objects.equals(this.content, dataEntryWithContent.content) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataEntryWithContent {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
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

