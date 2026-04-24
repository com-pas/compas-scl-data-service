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



@JsonTypeName("DataResourcesResult")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2026-04-24T08:07:06.426123+02:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class DataResourcesResult   {
  private @Valid List<@Valid DataResource> results = new ArrayList<>();

  /**
   **/
  public DataResourcesResult results(List<@Valid DataResource> results) {
    this.results = results;
    return this;
  }

  
  @JsonProperty("results")
  @NotNull @Valid public List<@Valid DataResource> getResults() {
    return results;
  }

  @JsonProperty("results")
  public void setResults(List<@Valid DataResource> results) {
    this.results = results;
  }

  public DataResourcesResult addResultsItem(DataResource resultsItem) {
    if (this.results == null) {
      this.results = new ArrayList<>();
    }

    this.results.add(resultsItem);
    return this;
  }

  public DataResourcesResult removeResultsItem(DataResource resultsItem) {
    if (resultsItem != null && this.results != null) {
      this.results.remove(resultsItem);
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
    DataResourcesResult dataResourcesResult = (DataResourcesResult) o;
    return Objects.equals(this.results, dataResourcesResult.results);
  }

  @Override
  public int hashCode() {
    return Objects.hash(results);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataResourcesResult {\n");
    
    sb.append("    results: ").append(toIndentedString(results)).append("\n");
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

