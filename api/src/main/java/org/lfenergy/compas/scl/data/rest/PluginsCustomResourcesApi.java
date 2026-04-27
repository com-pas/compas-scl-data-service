// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.lfenergy.compas.scl.data.rest.dto.DataEntry;
import org.lfenergy.compas.scl.data.rest.dto.DataEntryWithContent;
import java.util.Date;
import org.lfenergy.compas.scl.data.rest.dto.Error;
import java.io.File;
import org.lfenergy.compas.scl.data.rest.dto.PagedDataEntryResponse;
import java.util.UUID;
import org.lfenergy.compas.scl.data.rest.dto.UploadDataResponse;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;


import java.io.InputStream;
import java.util.Map;
import java.util.List;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

/**
* Represents a collection of functions to interact with the API endpoints.
*/
@Path("/plugins/resources/v1")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.12.0")
public interface PluginsCustomResourcesApi {

    /**
     * Deletes every version of the named resource
     *
     * @param type 
     * @param name 
     * @return Resource deleted
     * @return Resource not found
     * @return Internal server error
     */
    @DELETE
    @Path("/{type}/{name}")
    @Produces({ "application/json" })
    void deleteByName(@PathParam("type") String type,@PathParam("name") String name);


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
     * Returns the specified version of the named resource, with content
     *
     * @param type 
     * @param name 
     * @param version 
     * @return Resource version with content
     * @return Resource version not found
     * @return Internal server error
     */
    @GET
    @Path("/{type}/{name}/{version}")
    @Produces({ "application/json" })
    DataEntryWithContent getByNameAndVersion(@PathParam("type") String type,@PathParam("name") String name,@PathParam("version") String version);


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
     * Returns the latest version of the named resource, with content
     *
     * @param type 
     * @param name 
     * @return Latest version with content
     * @return Resource not found
     * @return Internal server error
     */
    @GET
    @Path("/{type}/{name}/latest")
    @Produces({ "application/json" })
    DataEntryWithContent getLatestByName(@PathParam("type") String type,@PathParam("name") String name);


    /**
     * Returns the latest version of every resource of the given data type, without content
     *
     * @param type Data type
     * @return List of latest entries per resource
     * @return Internal server error
     */
    @GET
    @Path("/{type}/latest")
    @Produces({ "application/json" })
    List<DataEntry> getLatestPerType(@PathParam("type") String type);


    /**
     * Returns all versions of the named resource as DataEntry objects (no content)
     *
     * @param type 
     * @param name 
     * @return List of versions for the resource
     * @return Resource not found
     * @return Internal server error
     */
    @GET
    @Path("/{type}/{name}")
    @Produces({ "application/json" })
    List<DataEntry> getVersionsByName(@PathParam("type") String type,@PathParam("name") String name);


    /**
     * Returns all versions of the named resource as DataEntryWithContent objects
     *
     * @param type 
     * @param name 
     * @return List of versions with content
     * @return Resource not found
     * @return Internal server error
     */
    @GET
    @Path("/{type}/{name}/versions")
    @Produces({ "application/json" })
    List<DataEntryWithContent> getVersionsWithContentByName(@PathParam("type") String type,@PathParam("name") String name);


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
    UploadDataResponse uploadData(@FormParam(value = "type")  String type,@FormParam(value = "name")  String name,@FormParam(value = "content-type")  String contentType, @FormParam(value = "content") InputStream content,@FormParam(value = "data-compatibility-version")  String dataCompatibilityVersion,@FormParam(value = "description")  String description,@FormParam(value = "version")  String version,@FormParam(value = "nextVersionType")  String nextVersionType);

}
