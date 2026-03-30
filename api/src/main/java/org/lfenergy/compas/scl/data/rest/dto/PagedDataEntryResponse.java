// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.lfenergy.compas.scl.data.rest.dto.DataEntry;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonTypeName;



@JsonTypeName("PagedDataEntryResponse")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.12.0")
public class PagedDataEntryResponse   {
  private @Valid List<@Valid DataEntry> content = new ArrayList<>();
  private Integer totalElements;
  private Integer totalPages;
  private Integer page;
  private Integer size;

  public PagedDataEntryResponse() {
  }

  /**
   **/
  public PagedDataEntryResponse content(List<@Valid DataEntry> content) {
    this.content = content;
    return this;
  }

  
  @JsonProperty("content")
  @Valid public List<@Valid DataEntry> getContent() {
    return content;
  }

  @JsonProperty("content")
  public void setContent(List<@Valid DataEntry> content) {
    this.content = content;
  }

  public PagedDataEntryResponse addContentItem(DataEntry contentItem) {
    if (this.content == null) {
      this.content = new ArrayList<>();
    }

    this.content.add(contentItem);
    return this;
  }

  public PagedDataEntryResponse removeContentItem(DataEntry contentItem) {
    if (contentItem != null && this.content != null) {
      this.content.remove(contentItem);
    }

    return this;
  }
  /**
   * Total number of entries
   **/
  public PagedDataEntryResponse totalElements(Integer totalElements) {
    this.totalElements = totalElements;
    return this;
  }

  
  @JsonProperty("totalElements")
  public Integer getTotalElements() {
    return totalElements;
  }

  @JsonProperty("totalElements")
  public void setTotalElements(Integer totalElements) {
    this.totalElements = totalElements;
  }

  /**
   * Total number of pages
   **/
  public PagedDataEntryResponse totalPages(Integer totalPages) {
    this.totalPages = totalPages;
    return this;
  }

  
  @JsonProperty("totalPages")
  public Integer getTotalPages() {
    return totalPages;
  }

  @JsonProperty("totalPages")
  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  /**
   * Current page number
   **/
  public PagedDataEntryResponse page(Integer page) {
    this.page = page;
    return this;
  }

  
  @JsonProperty("page")
  public Integer getPage() {
    return page;
  }

  @JsonProperty("page")
  public void setPage(Integer page) {
    this.page = page;
  }

  /**
   * Page size
   **/
  public PagedDataEntryResponse size(Integer size) {
    this.size = size;
    return this;
  }

  
  @JsonProperty("size")
  public Integer getSize() {
    return size;
  }

  @JsonProperty("size")
  public void setSize(Integer size) {
    this.size = size;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PagedDataEntryResponse pagedDataEntryResponse = (PagedDataEntryResponse) o;
    return Objects.equals(this.content, pagedDataEntryResponse.content) &&
        Objects.equals(this.totalElements, pagedDataEntryResponse.totalElements) &&
        Objects.equals(this.totalPages, pagedDataEntryResponse.totalPages) &&
        Objects.equals(this.page, pagedDataEntryResponse.page) &&
        Objects.equals(this.size, pagedDataEntryResponse.size);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, totalElements, totalPages, page, size);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PagedDataEntryResponse {\n");
    
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
    sb.append("    totalElements: ").append(toIndentedString(totalElements)).append("\n");
    sb.append("    totalPages: ").append(toIndentedString(totalPages)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    size: ").append(toIndentedString(size)).append("\n");
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

