// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.lfenergy.compas.scl.data.model.*;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository class that will be used to handle SCL File for a specific type of storage.
 * The repository class needs to be able to create and delete entries and find/list entries.
 */
public interface CompasSclDataRepository {
    /**
     * List the latest version of all SCL Entries for a type of SCL.
     *
     * @param type The type of SCL to search for.
     * @return The list of entries found for the passed type.
     */
    List<IItem> list(SclFileType type);

    /**
     * List all versions for a specific SCL Entry for a type of SCL.
     *
     * @param type The type of SCL to search for the specific SCL.
     * @param id   The ID of the SCL to search for.
     * @return The list of versions found for that specific sCl Entry.
     */
    List<IHistoryItem> listVersionsByUUID(SclFileType type, UUID id);

    /**
     * Return the latest version of a specific SCL Entry.
     *
     * @param type The type of SCL to search for the specific SCL.
     * @param id   The ID of the SCL to search for.
     * @return The SCL XML File Content that is search for.
     */
    String findByUUID(SclFileType type, UUID id);

    /**
     * Return the meta info of the latest version of a specific SCL Entry.
     *
     * @param type The type of SCL to search for the specific SCL.
     * @param id   The ID of the SCL to search for.
     * @return The Meta Info of SCL Entry that is search for.
     */
    IAbstractItem findMetaInfoByUUID(SclFileType type, UUID id);

    /**
     * Return the specific version of a specific SCL Entry.
     *
     * @param id      The ID of the SCL to search for.
     * @param version The version of the ScL to search for.
     * @return The SCL XML File Content that is search for.
     */
    String findByUUID(UUID id, Version version);

    /**
     * Return the specific version of a specific SCL Entry.
     *
     * @param type    The type of SCL to search for the specific SCL.
     * @param id      The ID of the SCL to search for.
     * @param version The version of the ScL to search for.
     * @return The SCL XML File Content that is search for.
     */
    String findByUUID(SclFileType type, UUID id, Version version);

    /**
     * Return the specific version of a specific SCL Entry.
     *
     * @param type The type of SCL to search for the specific SCL.
     * @param name The name of the SCL used for checking duplicates.
     * @return True if name is already used by another SCL File of the same File type, otherwise false.
     */
    boolean hasDuplicateSclName(SclFileType type, String name);

    /**
     * Create a new entry for the passed UUID with the version number passed.
     * <p>
     * For a complete new entry the service layer will create a new UUID and set the version to 1.0.0.
     * When a entry is updated the service layer will increase the version and always create a new entry
     * in the repository.
     *
     * @param type    The type of SCL to store it in.
     * @param id      The ID of the new entry to be created.
     * @param name    The name of the SCL to be stored.
     * @param scl     The SCL XML File content to store.
     * @param version The version of the new entry to be created.
     * @param who     The user that created the new entry.
     * @param labels  The list of Labels extracted from the SCL XML File.
     */
    void create(SclFileType type, UUID id, String name, String scl, Version version, String who, List<String> labels);

    /**
     * Delete all versions for a specific SCL File using its ID.
     *
     * @param type The type of SCL where to find the SCL File
     * @param id   The ID of the SCL File to delete.
     */
    void delete(SclFileType type, UUID id);

    /**
     * Mark all versions as deleted for a specific SCL File using its ID without really deleting them.
     *
     * @param type The type of SCL where to find the SCL File
     * @param id   The ID of the SCL File to delete.
     */
    void softDelete(SclFileType type, UUID id);

    /**
     * Delete passed version for a specific SCL File using its ID.
     *
     * @param type    The type of SCL where to find the SCL File
     * @param id      The ID of the SCL File to delete.
     * @param version The version of that SCL File to delete.
     */
    void deleteVersion(SclFileType type, UUID id, Version version);

    /**
     * Mark passed version for a specific SCL File as deleted using its ID without really deleting it.
     *
     * @param type    The type of SCL where to find the SCL File
     * @param id      The ID of the SCL File to delete.
     * @param version The version of that SCL File to delete.
     */
    void softDeleteVersion(SclFileType type, UUID id, Version version);

    /**
     * List the latest version of all SCL History Entries.
     *
     * @return The list of entries found.
     */
    List<IHistoryMetaItem> listHistory();

    /**
     * List the latest version of all SCL History Entries.
     *
     * @return The list of entries found.
     */
    List<IHistoryMetaItem> listHistory(UUID id);

    /**
     * List the latest version of all SCL History Entries.
     *
     * @return The list of entries found.
     */
    List<IHistoryMetaItem> listHistory(SclFileType type, String name, String author, OffsetDateTime from, OffsetDateTime to);

    /**
     * List all history versions for a specific SCL Entry.
     *
     * @param id The ID of the SCL to search for.
     * @return The list of versions found for that specific sCl Entry.
     */
    List<IHistoryMetaItem> listHistoryVersionsByUUID(UUID id);

    /**
     * Create a new Location
     *
     * @param key         The key of the Location
     * @param name        The name of the Location
     * @param description The description of the Location
     * @return The created Location
     */
    ILocationMetaItem createLocation(String key, String name, String description);

    void addLocationTags(ILocationMetaItem location, String author);

    void deleteLocationTags(ILocationMetaItem location);

    /**
     * List Location entries
     *
     * @param page     The page number of the result
     * @param pageSize The amount of Location entries on the page
     * @return The specified page with the specified number of Location entries
     */
    List<ILocationMetaItem> listLocations(int page, int pageSize);

    /**
     * Return the specific Location entry
     *
     * @param locationId The uuid of the Location
     * @return The Meta Info of the searched Location
     */
    ILocationMetaItem findLocationByUUID(UUID locationId);

    /**
     * Delete the specified Location entry
     *
     * @param locationId The uuid of the Location
     */
    void deleteLocation(UUID locationId);

    /**
     * Update an existing location
     *
     * @param locationId  The uuid of the existing Location
     * @param key         The key of the updated Location
     * @param name        The name of the updated Location
     * @param description The description of the updated Location
     * @return The Meta Info of the updated Location
     */
    ILocationMetaItem updateLocation(UUID locationId, String key, String name, String description);

    /**
     * Assign a resource to the specified location, if a resource is already assigned to a location, the previous assignment is removed
     *
     * @param locationId The uuid of the Location
     * @param resourceId The uuid of the Resource
     */
    void assignResourceToLocation(UUID locationId, UUID resourceId);

    /**
     * Remove the resource assignment from the specified location
     *
     * @param locationId The uuid of the Location
     * @param resourceId The uuid of the Resource
     */
    void unassignResourceFromLocation(UUID locationId, UUID resourceId);

    /**
     * Archive a resource and link it to the corresponding scl_file entry
     *
     * @param id The id of the scl_file
     * @param version The version of the scl_file
     * @param author The author of the resource
     * @param approver The approver of the resource
     * @param contentType The content type of the resource
     * @param filename The filename of the resource
     * @return The created archived resource item
     */
    IAbstractArchivedResourceMetaItem archiveResource(UUID id, Version version, String author, String approver, String contentType, String filename);

    /**
     * Archive an existing scl resource
     *
     * @param id The id of the resource to be archived
     * @param version The version of the resource to be archived
     * @param approver The approver of the archiving action
     * @return The archived resource item
     */
    IAbstractArchivedResourceMetaItem archiveSclResource(UUID id, Version version, String approver);

    /**
     * Retrieve all entries according to an archived resource id
     *
     * @param id The id of the archived resource
     * @return All archived entries of the given id
     */
    IArchivedResourcesMetaItem searchArchivedResource(UUID id);

    /**
     * Retrieve all archived entries according to the search parameters
     *
     * @param location The location of the archived resource
     * @param name The name of the archived resource
     * @param approver The approver of the archived resource
     * @param contentType The content type of the resource
     * @param type The type of the resource
     * @param voltage The voltage of the resource
     * @param from The start timestamp of archiving (including)
     * @param to The end timestamp of archiving (including)
     * @return All archived entries matching the search criteria
     */
    IArchivedResourcesMetaItem searchArchivedResource(String location, String name, String approver, String contentType, String type, String voltage, OffsetDateTime from, OffsetDateTime to);

    /**
     * Retrieve all archived resource history versions according to an archived resource id
     *
     * @param uuid The id of the archived resource
     * @return All archived versions of the given id
     */
    IArchivedResourcesHistoryMetaItem searchArchivedResourceHistory(UUID uuid);

}
