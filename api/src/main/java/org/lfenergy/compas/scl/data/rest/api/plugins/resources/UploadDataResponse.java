// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources;

import java.util.Date;
import java.util.UUID;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("UploadDataResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.12.0")
public class UploadDataResponse   {
  private UUID id;
  private String type;
  private String tenant;
  private String name;
  private String version;
  private Date uploadedAt;

  public UploadDataResponse() {
  }

  /**
   * Unique identifier for the uploaded data
   **/
  public UploadDataResponse id(UUID id) {
    this.id = id;
    return this;
  }

  
  @JsonProperty("id")
  public UUID getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   **/
  public UploadDataResponse type(String type) {
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
   **/
  public UploadDataResponse tenant(String tenant) {
    this.tenant = tenant;
    return this;
  }

  
  @JsonProperty("tenant")
  public String getTenant() {
    return tenant;
  }

  @JsonProperty("tenant")
  public void setTenant(String tenant) {
    this.tenant = tenant;
  }

  /**
   **/
  public UploadDataResponse name(String name) {
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
   **/
  public UploadDataResponse version(String version) {
    this.version = version;
    return this;
  }

  
  @JsonProperty("version")
  public String getVersion() {
    return version;
  }

  @JsonProperty("version")
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Timestamp of the upload
   **/
  public UploadDataResponse uploadedAt(Date uploadedAt) {
    this.uploadedAt = uploadedAt;
    return this;
  }

  
  @JsonProperty("uploadedAt")
  public Date getUploadedAt() {
    return uploadedAt;
  }

  @JsonProperty("uploadedAt")
  public void setUploadedAt(Date uploadedAt) {
    this.uploadedAt = uploadedAt;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UploadDataResponse uploadDataResponse = (UploadDataResponse) o;
    return Objects.equals(this.id, uploadDataResponse.id) &&
        Objects.equals(this.type, uploadDataResponse.type) &&
        Objects.equals(this.tenant, uploadDataResponse.tenant) &&
        Objects.equals(this.name, uploadDataResponse.name) &&
        Objects.equals(this.version, uploadDataResponse.version) &&
        Objects.equals(this.uploadedAt, uploadDataResponse.uploadedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, type, tenant, name, version, uploadedAt);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UploadDataResponse {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    tenant: ").append(toIndentedString(tenant)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    uploadedAt: ").append(toIndentedString(uploadedAt)).append("\n");
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

