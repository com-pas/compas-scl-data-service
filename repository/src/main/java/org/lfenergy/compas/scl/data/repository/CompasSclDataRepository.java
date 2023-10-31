// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.lfenergy.compas.scl.data.model.*;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

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
     * Delete passed versions for a specific SCL File using its ID.
     *
     * @param type    The type of SCL where to find the SCL File
     * @param id      The ID of the SCL File to delete.
     * @param version The version of that SCL File to delete.
     */
    void delete(SclFileType type, UUID id, Version version);
}
