package org.lfenergy.compas.scl.data.rest.api.locations.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



@JsonTypeName("Locations")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2024-11-18T07:52:46.875467800+01:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class Locations   {
  private @Valid List<@Valid Location> locations = new ArrayList<>();
  private Pagination pagination;

  /**
   * List of locations
   **/
  public Locations locations(List<@Valid Location> locations) {
    this.locations = locations;
    return this;
  }


  @JsonProperty("locations")
  @NotNull @Valid public List<@Valid Location> getLocations() {
    return locations;
  }

  @JsonProperty("locations")
  public void setLocations(List<@Valid Location> locations) {
    this.locations = locations;
  }

  public Locations addLocationsItem(Location locationsItem) {
    if (this.locations == null) {
      this.locations = new ArrayList<>();
    }

    this.locations.add(locationsItem);
    return this;
  }

  public Locations removeLocationsItem(Location locationsItem) {
    if (locationsItem != null && this.locations != null) {
      this.locations.remove(locationsItem);
    }

    return this;
  }
  /**
   **/
  public Locations pagination(Pagination pagination) {
    this.pagination = pagination;
    return this;
  }


  @JsonProperty("pagination")
  @Valid public Pagination getPagination() {
    return pagination;
  }

  @JsonProperty("pagination")
  public void setPagination(Pagination pagination) {
    this.pagination = pagination;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Locations locations = (Locations) o;
    return Objects.equals(this.locations, locations.locations) &&
        Objects.equals(this.pagination, locations.pagination);
  }

  @Override
  public int hashCode() {
    return Objects.hash(locations, pagination);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Locations {\n");

    sb.append("    locations: ").append(toIndentedString(locations)).append("\n");
    sb.append("    pagination: ").append(toIndentedString(pagination)).append("\n");
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

