// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@JsonTypeName("DataResourceHistory")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-04-24T08:07:06.426123+02:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class DataResourceHistory   {
  private @Valid List<DataResourceVersion> versions = new ArrayList<>();

  /**
   **/
  public DataResourceHistory versions(List<DataResourceVersion> versions) {
    this.versions = versions;
    return this;
  }

  
  @JsonProperty("versions")
  @NotNull @Valid public List<@Valid DataResourceVersion> getVersions() {
    return versions;
  }

  @JsonProperty("versions")
  public void setVersions(List<DataResourceVersion> versions) {
    this.versions = versions;
  }

  public DataResourceHistory addVersionsItem(DataResourceVersion versionsItem) {
    if (this.versions == null) {
      this.versions = new ArrayList<>();
    }

    this.versions.add(versionsItem);
    return this;
  }

  public DataResourceHistory removeVersionsItem(DataResourceVersion versionsItem) {
    if (versionsItem != null && this.versions != null) {
      this.versions.remove(versionsItem);
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
    DataResourceHistory dataResourceHistory = (DataResourceHistory) o;
    return Objects.equals(this.versions, dataResourceHistory.versions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(versions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataResourceHistory {\n");
    
    sb.append("    versions: ").append(toIndentedString(versions)).append("\n");
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

