package org.lfenergy.compas.scl.data.rest.api.archive.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@JsonTypeName("ArchivedResources")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2024-11-18T15:39:21.464141400+01:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class ArchivedResources   {
  private @Valid List<@Valid ArchivedResource> resources = new ArrayList<>();

  /**
   **/
  public ArchivedResources resources(List<@Valid ArchivedResource> resources) {
    this.resources = resources;
    return this;
  }


  @JsonProperty("resources")
  @NotNull @Valid public List<@Valid ArchivedResource> getResources() {
    return resources;
  }

  @JsonProperty("resources")
  public void setResources(List<@Valid ArchivedResource> resources) {
    this.resources = resources;
  }

  public ArchivedResources addResourcesItem(ArchivedResource resourcesItem) {
    if (this.resources == null) {
      this.resources = new ArrayList<>();
    }

    this.resources.add(resourcesItem);
    return this;
  }

  public ArchivedResources removeResourcesItem(ArchivedResource resourcesItem) {
    if (resourcesItem != null && this.resources != null) {
      this.resources.remove(resourcesItem);
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
    ArchivedResources archivedResources = (ArchivedResources) o;
    return Objects.equals(this.resources, archivedResources.resources);
  }

  @Override
  public int hashCode() {
    return Objects.hash(resources);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ArchivedResources {\n");

    sb.append("    resources: ").append(toIndentedString(resources)).append("\n");
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

