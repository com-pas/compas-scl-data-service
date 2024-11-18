package org.lfenergy.compas.scl.data.rest.api.locations.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Objects;



@JsonTypeName("ErrorResponseDto")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2024-11-18T07:52:46.875467800+01:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class ErrorResponseDto   {
  private OffsetDateTime timestamp;
  private String code;
  private String message;

  /**
   * 2017-07-21T17:32:28Z.
   **/
  public ErrorResponseDto timestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  
  @JsonProperty("timestamp")
  @NotNull public OffsetDateTime getTimestamp() {
    return timestamp;
  }

  @JsonProperty("timestamp")
  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }

  /**
   **/
  public ErrorResponseDto code(String code) {
    this.code = code;
    return this;
  }

  
  @JsonProperty("code")
  @NotNull public String getCode() {
    return code;
  }

  @JsonProperty("code")
  public void setCode(String code) {
    this.code = code;
  }

  /**
   **/
  public ErrorResponseDto message(String message) {
    this.message = message;
    return this;
  }

  
  @JsonProperty("message")
  @NotNull public String getMessage() {
    return message;
  }

  @JsonProperty("message")
  public void setMessage(String message) {
    this.message = message;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ErrorResponseDto errorResponseDto = (ErrorResponseDto) o;
    return Objects.equals(this.timestamp, errorResponseDto.timestamp) &&
        Objects.equals(this.code, errorResponseDto.code) &&
        Objects.equals(this.message, errorResponseDto.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timestamp, code, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ErrorResponseDto {\n");
    
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

