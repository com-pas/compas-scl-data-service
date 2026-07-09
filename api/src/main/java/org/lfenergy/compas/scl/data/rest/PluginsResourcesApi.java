// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.CreatePluginResourceRequest;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.PluginResource;
import org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.PluginResourceMeta;
import java.util.UUID;

import jakarta.ws.rs.*;

import java.util.List;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

/**
* Represents a collection of functions to interact with the API endpoints.
*/
@Path("/plugins-resources/plugins/{plugin}/types/{type}")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.12.0")
public interface PluginsResourcesApi {

    /**
     * Creates a new versioned resource for the given plugin and type. Either `version` (explicit semver) or `nextVersionType` (automatic increment) must be supplied — they are mutually exclusive. 
     *
     * @param plugin Plugin identifier. Must start with a lowercase letter and contain only lowercase letters, digits, and underscores. 
     * @param type Resource type defined by the plugin. Must start with a lowercase letter and contain only lowercase letters, digits, hyphens, and underscores. 
     * @param createPluginResourceRequest 
     * @return Resource version created successfully
     * @return Bad request — invalid path parameters, query parameters, or request body.
     * @return Conflict — a resource with this name and version already exists.
     * @return Internal server error — an unexpected condition occurred.
     */
    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    PluginResourceMeta createPluginResource(@PathParam("plugin") @Pattern(regexp="^[a-z][a-z0-9_]*$") String plugin,@PathParam("type") @Pattern(regexp="^[a-z][a-z0-9_-]*$") String type,@Valid @NotNull CreatePluginResourceRequest createPluginResourceRequest);


    /**
     * Deletes a specific named resource and every version that exists for it under the given plugin and type. This operation is irreversible. 
     *
     * @param plugin Plugin identifier. Must start with a lowercase letter and contain only lowercase letters, digits, and underscores. 
     * @param type Resource type defined by the plugin. Must start with a lowercase letter and contain only lowercase letters, digits, hyphens, and underscores. 
     * @param name Resource name. Must start with a lowercase letter and contain only lowercase letters, digits, hyphens, and underscores. 
     * @return Resource and all its versions deleted successfully
     * @return The requested resource was not found.
     * @return Internal server error — an unexpected condition occurred.
     */
    @DELETE
    @Path("/resources/{name}")
    @Produces({ "application/json" })
    void deletePluginResourceByName(@PathParam("plugin") @Pattern(regexp="^[a-z][a-z0-9_]*$") String plugin, @PathParam("type") @Pattern(regexp="^[a-z][a-z0-9_-]*$") String type, @PathParam("name") @Pattern(regexp="^[a-z][a-z0-9_-]*$") String name);


    /**
     * Deletes all resources (every name, every version) for the given plugin and type. Intended for use when a type is no longer needed, e.g. after plugin refactoring. This operation is irreversible. 
     *
     * @param plugin Plugin identifier. Must start with a lowercase letter and contain only lowercase letters, digits, and underscores. 
     * @param type Resource type defined by the plugin. Must start with a lowercase letter and contain only lowercase letters, digits, hyphens, and underscores. 
     * @return All resources of the given type deleted successfully
     * @return The requested resource was not found.
     * @return Internal server error — an unexpected condition occurred.
     */
    @DELETE
    @Produces({ "application/json" })
    void deletePluginResourcesByType(@PathParam("plugin") @Pattern(regexp="^[a-z][a-z0-9_]*$") String plugin,@PathParam("type") @Pattern(regexp="^[a-z][a-z0-9_-]*$") String type);


    /**
     * Retrieves the most recent version of the specified named resource, including its full content. Equivalent to resolving the latest semver tag for the name. 
     *
     * @param plugin Plugin identifier. Must start with a lowercase letter and contain only lowercase letters, digits, and underscores. 
     * @param type Resource type defined by the plugin. Must start with a lowercase letter and contain only lowercase letters, digits, hyphens, and underscores. 
     * @param name Resource name. Must start with a lowercase letter and contain only lowercase letters, digits, hyphens, and underscores. 
     * @return Latest resource version retrieved successfully
     * @return The requested resource was not found.
     * @return Internal server error — an unexpected condition occurred.
     */
    @GET
    @Path("/resources/{name}/latest")
    @Produces({ "application/json" })
    PluginResource getLatestPluginResourceByName(@PathParam("plugin") @Pattern(regexp="^[a-z][a-z0-9_]*$") String plugin,@PathParam("type") @Pattern(regexp="^[a-z][a-z0-9_-]*$") String type,@PathParam("name") @Pattern(regexp="^[a-z][a-z0-9_-]*$") String name);


    /**
     * Retrieves metadata for all resources belonging to the given plugin and type, reduced to one entry per resource name (the latest version). Content is not included; use the `/resources/{name}/latest` endpoint to fetch content. 
     *
     * @param plugin Plugin identifier. Must start with a lowercase letter and contain only lowercase letters, digits, and underscores. 
     * @param type Resource type defined by the plugin. Must start with a lowercase letter and contain only lowercase letters, digits, hyphens, and underscores. 
     * @return Latest resource metadata retrieved successfully
     * @return The requested resource was not found.
     * @return Internal server error — an unexpected condition occurred.
     */
    @GET
    @Path("/latest")
    @Produces({ "application/json" })
    List<PluginResourceMeta> getLatestPluginResourcesByType(@PathParam("plugin") @Pattern(regexp="^[a-z][a-z0-9_]*$") String plugin,@PathParam("type") @Pattern(regexp="^[a-z][a-z0-9_-]*$") String type);


    /**
     * Retrieves a specific resource version, including its full content, identified by its unique UUID. Use this endpoint when you already have a concrete version ID (e.g. stored as a reference in another resource). 
     *
     * @param plugin Plugin identifier. Must start with a lowercase letter and contain only lowercase letters, digits, and underscores. 
     * @param type Resource type defined by the plugin. Must start with a lowercase letter and contain only lowercase letters, digits, hyphens, and underscores. 
     * @param id UUID of the resource version to retrieve
     * @return Resource version retrieved successfully
     * @return The requested resource was not found.
     * @return Internal server error — an unexpected condition occurred.
     */
    @GET
    @Path("/{id}")
    @Produces({ "application/json" })
    PluginResource getPluginResourceById(@PathParam("plugin") @Pattern(regexp="^[a-z][a-z0-9_]*$") String plugin,@PathParam("type") @Pattern(regexp="^[a-z][a-z0-9_-]*$") String type,@PathParam("id") UUID id);


    /**
     * Retrieves metadata for every version of the specified named resource, ordered by version descending (newest first). Content is not included; use the `/{id}` endpoint to fetch the content of a specific version. 
     *
     * @param plugin Plugin identifier. Must start with a lowercase letter and contain only lowercase letters, digits, and underscores. 
     * @param type Resource type defined by the plugin. Must start with a lowercase letter and contain only lowercase letters, digits, hyphens, and underscores. 
     * @param name Resource name. Must start with a lowercase letter and contain only lowercase letters, digits, hyphens, and underscores. 
     * @return Resource version history retrieved successfully
     * @return The requested resource was not found.
     * @return Internal server error — an unexpected condition occurred.
     */
    @GET
    @Path("/resources/{name}/versions")
    @Produces({ "application/json" })
    List<PluginResourceMeta> getPluginResourceVersionsByName(@PathParam("plugin") @Pattern(regexp="^[a-z][a-z0-9_]*$") String plugin,@PathParam("type") @Pattern(regexp="^[a-z][a-z0-9_-]*$") String type,@PathParam("name") @Pattern(regexp="^[a-z][a-z0-9_-]*$") String name);

}
