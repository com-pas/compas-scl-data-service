package org.lfenergy.compas.scl.data.rest.api.archive.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@JsonTypeName("ArchivedResourcesHistory")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2024-12-06T09:13:22.882514600+01:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class ArchivedResourcesHistory   {
  private @Valid List<ArchivedResourceVersion> versions = new ArrayList<>();

  /**
   **/
  public ArchivedResourcesHistory versions(List<ArchivedResourceVersion> versions) {
    this.versions = versions;
    return this;
  }


  @JsonProperty("versions")
  @NotNull @Valid public List<@Valid ArchivedResourceVersion> getVersions() {
    return versions;
  }

  @JsonProperty("versions")
  public void setVersions(List<ArchivedResourceVersion> versions) {
    this.versions = versions;
  }

  public ArchivedResourcesHistory addVersionsItem(ArchivedResourceVersion versionsItem) {
    if (this.versions == null) {
      this.versions = new ArrayList<>();
    }

    this.versions.add(versionsItem);
    return this;
  }

  public ArchivedResourcesHistory removeVersionsItem(ArchivedResourceVersion versionsItem) {
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
    ArchivedResourcesHistory archivedResourcesHistory = (ArchivedResourcesHistory) o;
    return Objects.equals(this.versions, archivedResourcesHistory.versions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(versions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ArchivedResourcesHistory {\n");

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

