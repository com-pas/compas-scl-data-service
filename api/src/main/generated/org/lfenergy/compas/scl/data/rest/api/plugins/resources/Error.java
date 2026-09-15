// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources;

import java.util.HashMap;
import java.util.Map;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("Error")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.12.0")
public class Error   {
  private String code;
  private String message;
  private @Valid Map<String, Object> details = new HashMap<>();

  public Error() {
  }

  @JsonCreator
  public Error(
    @JsonProperty(required = true, value = "code") String code,
    @JsonProperty(required = true, value = "message") String message
  ) {
    this.code = code;
    this.message = message;
  }

  /**
   * Error code
   **/
  public Error code(String code) {
    this.code = code;
    return this;
  }

  
  @JsonProperty(required = true, value = "code")
  @NotNull public String getCode() {
    return code;
  }

  @JsonProperty(required = true, value = "code")
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * Human-readable error message
   **/
  public Error message(String message) {
    this.message = message;
    return this;
  }

  
  @JsonProperty(required = true, value = "message")
  @NotNull public String getMessage() {
    return message;
  }

  @JsonProperty(required = true, value = "message")
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Additional error details
   **/
  public Error details(Map<String, Object> details) {
    this.details = details;
    return this;
  }

  
  @JsonProperty("details")
  public Map<String, Object> getDetails() {
    return details;
  }

  @JsonProperty("details")
  public void setDetails(Map<String, Object> details) {
    this.details = details;
  }

  public Error putDetailsItem(String key, Object detailsItem) {
    if (this.details == null) {
      this.details = new HashMap<>();
    }

    this.details.put(key, detailsItem);
    return this;
  }

  public Error removeDetailsItem(String key) {
    if (this.details != null) {
      this.details.remove(key);
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
    Error error = (Error) o;
    return Objects.equals(this.code, error.code) &&
        Objects.equals(this.message, error.message) &&
        Objects.equals(this.details, error.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, message, details);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Error {\n");
    
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
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

