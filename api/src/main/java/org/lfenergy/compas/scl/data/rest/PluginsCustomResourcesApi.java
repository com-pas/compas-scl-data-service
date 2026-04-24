// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.lfenergy.compas.scl.data.rest.api.plugins.resources.DataEntryWithContent;
import java.util.Date;

import org.lfenergy.compas.scl.data.rest.api.plugins.resources.PagedDataEntryResponse;
import java.util.UUID;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.UploadDataResponse;

import jakarta.ws.rs.*;


import java.io.InputStream;

import jakarta.validation.constraints.*;

/**
* Represents a collection of functions to interact with the API endpoints.
*/
@Path("/plugins/resources/v1")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.12.0")
public interface PluginsCustomResourcesApi {

    /**
     * Retrieve metadata for all uploaded data entries with optional filtering
     *
     * @param type Filter by data type
     * @param uploadedAfter Filter by upload date (ISO 8601 format)
     * @param uploadedBefore Filter by upload date (ISO 8601 format)
     * @param name Filter by name (contains search)
     * @param page Page number for pagination (0-indexed)
     * @param size Number of items per page
     * @return List of data entries retrieved successfully
     * @return Bad request - Invalid query parameters
     * @return Internal server error
     */
    @GET
    @Produces({ "application/json" })
    PagedDataEntryResponse getAllData(@QueryParam("type") @NotNull   String type,@QueryParam("uploadedAfter")   Date uploadedAfter,@QueryParam("uploadedBefore")   Date uploadedBefore,@QueryParam("name")   String name,@QueryParam("page") @Min(0) @DefaultValue("0")   Integer page,@QueryParam("size") @Min(1) @Max(100) @DefaultValue("20")   Integer size);


    /**
     * Retrieve a single data entry by its unique identifier, including the full content
     *
     * @param id Unique identifier of the data entry
     * @return Data entry retrieved successfully
     * @return Data entry not found
     * @return Internal server error
     */
    @GET
    @Path("/{id}")
    @Produces({ "application/json" })
    DataEntryWithContent getDataById(@PathParam("id") UUID id);


    /**
     * Upload a JSON or XML data file with associated metadata
     *
     * @param type Type of the data being uploaded
     * @param name Name of the data file
     * @param contentType Content type of the uploaded file
     * @param content The JSON or XML file content
     * @param dataCompatibilityVersion Data compatibility version (semver format)
     * @param description Optional description of the data file
     * @param version Semantic version of the data file (semver format)
     * @param nextVersionType Type of the next version (used for automatic version increment) If provided, the service will automatically increment the version based on the specified type. If not provided, the version must be explicitly specified in the &#39;version&#39; field.
     * @return Data uploaded successfully
     * @return Bad request - Invalid input data
     * @return Conflict - Data with this name and version already exists
     * @return Payload too large
     * @return Unsupported media type
     * @return Internal server error
     */
    @POST
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json" })
    UploadDataResponse uploadData(@FormParam(value = "type")  String type,@FormParam(value = "name")  String name,@FormParam(value = "content-type")  String contentType, @FormParam(value = "content") InputStream contentInputStream,@FormParam(value = "data-compatibility-version")  String dataCompatibilityVersion,@FormParam(value = "description")  String description,@FormParam(value = "version")  String version,@FormParam(value = "nextVersionType")  String nextVersionType);

}
