// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.lfenergy.compas.scl.data.rest.api.plugins.resources.DataEntry;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.DataEntryWithContent;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.PluginWithTypes;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.UploadDataResponse;

import jakarta.ws.rs.*;


import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.*;

/**
* Represents a collection of functions to interact with the API endpoints.
*/
@Path("/plugins-resources")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.12.0")
public interface PluginsCustomResourcesApi {

    /**
    * Retrieve all plugins with related resource types
     *
    * @return Plugins and their related types retrieved successfully
    * @return Internal server error
    */
    @GET
    @Path("/plugins")
    @Produces({ "application/json" })
    List<PluginWithTypes> getPluginsWithTypes();


    /**
    * Retrieve a single data entry by its unique identifier, including the full content
    *
    * @param plugin Plugin identifier
    * @param type Resource type
    * @param id Unique identifier of the data entry
    * @return Data entry retrieved successfully
    * @return Data entry not found
    * @return Internal server error
    */
    @GET
    @Path("/plugins/{plugin}/types/{type}/{id}")
    @Produces({ "application/json" })
    DataEntryWithContent getPluginResourceById(@PathParam("plugin") String plugin, @PathParam("type") String type, @PathParam("id") UUID id);


    /**
    * Delete all data entries for the given data type
    *
    * @param plugin Plugin identifier
    * @param type Resource type
    * @return Data entries deleted successfully
    * @return No data entries found for the given data type
    * @return Internal server error
    */
    @DELETE
    @Path("/plugins/{plugin}/types/{type}")
    void deletePluginResourcesByType(@PathParam("plugin") String plugin, @PathParam("type") String type);


    /**
    * Retrieve all resources for the given data type, reduced to the latest version per resource name, without content
    *
    * @param plugin Plugin identifier
    * @param type Resource type
    * @return Data entries retrieved successfully
    * @return Data entries not found
    * @return Internal server error
    */
    @GET
    @Path("/plugins/{plugin}/types/{type}/latest")
    @Produces({ "application/json" })
    List<DataEntry> getLatestPluginResourcesByType(@PathParam("plugin") String plugin, @PathParam("type") String type);


    /**
    * Delete all data entries for the given data type and resource name
    *
    * @param plugin Plugin identifier
    * @param type Resource type
    * @param name Name of the entries to delete
    * @return Data entries deleted successfully
    * @return No data entries found for the given data type and name
    * @return Internal server error
    */
    @DELETE
    @Path("/plugins/{plugin}/types/{type}/resources/{name}")
    void deletePluginResourceByName(@PathParam("plugin") String plugin, @PathParam("type") String type, @PathParam("name") String name);


    /**
    * Retrieve the latest version of a data entry for the given data type and resource name, including the full content
    *
    * @param plugin Plugin identifier
    * @param type Resource type
     * @param name Name of the entry to retrieve
     * @return Data entry retrieved successfully
     * @return Data entry not found
     * @return Internal server error
     */
    @GET
    @Path("/plugins/{plugin}/types/{type}/resources/{name}/latest")
    @Produces({ "application/json" })
    DataEntryWithContent getLatestPluginResourceByName(@PathParam("plugin") String plugin, @PathParam("type") String type, @PathParam("name") String name);


    /**
    * Retrieve all versions of a data entry for the given data type and resource name, without content
     *
    * @param plugin Plugin identifier
    * @param type Resource type
    * @param name Name of the entry to retrieve
    * @return Data entries retrieved successfully
    * @return Data entries not found
    * @return Internal server error
    */
    @GET
    @Path("/plugins/{plugin}/types/{type}/resources/{name}/versions")
    @Produces({ "application/json" })
    List<DataEntry> getPluginResourceVersionsByName(@PathParam("plugin") String plugin, @PathParam("type") String type, @PathParam("name") String name);


    /**
    * Upload a JSON or XML data file with associated metadata
    *
    * @param plugin Plugin identifier
    * @param type Resource type
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
    @Path("/plugins/{plugin}/types/{type}")
    @Consumes({ "multipart/form-data" })
    @Produces({ "application/json" })
    UploadDataResponse createPluginResource(@PathParam("plugin") String plugin, @PathParam("type") String type, @FormParam(value = "name") String name, @FormParam(value = "content-type") String contentType, @FormParam(value = "content") InputStream content, @FormParam(value = "data-compatibility-version") String dataCompatibilityVersion, @FormParam(value = "description") String description, @FormParam(value = "version") String version, @FormParam(value = "nextVersionType") String nextVersionType);

}
