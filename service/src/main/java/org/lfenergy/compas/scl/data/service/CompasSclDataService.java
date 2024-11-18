// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.core.commons.exception.CompasException;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.*;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.util.SclElementProcessor;
import org.lfenergy.compas.scl.data.xml.HistoryItem;
import org.lfenergy.compas.scl.data.xml.Item;
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.*;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.*;
import static org.lfenergy.compas.scl.extensions.commons.CompasExtensionsConstants.*;

/**
 * Service class that will be using a Repository instance to retrieve, create, update, delete SCL XML Files.
 * These methods contain standard behaviour that is executed for every type of repository.
 */
@ApplicationScoped
public class CompasSclDataService {

    private final CompasSclDataRepository repository;
    private final ElementConverter converter;
    private final SclElementProcessor sclElementProcessor;

    @Inject
    public CompasSclDataService(CompasSclDataRepository repository, ElementConverter converter,
                                SclElementProcessor sclElementProcessor) {
        this.repository = repository;
        this.converter = converter;
        this.sclElementProcessor = sclElementProcessor;
    }

    /**
     * List the latest version of all SCL XML Files for a specific type.
     *
     * @param type The type to search for.
     * @return The List of Items found.
     */
    @Transactional(SUPPORTS)
    public List<Item> list(SclFileType type) {
        return repository.list(type)
                .stream()
                .map(e -> new Item(e.getId(), e.getName(), e.getVersion(), e.getLabels()))
                .toList();
    }

    /**
     * Search for all versions of a specific SCL XML File (using the UUID) for a specific type.
     *
     * @param type The type to search for.
     * @param id   The UUID of the record to search for.
     * @return The list of versions found.
     */
    @Transactional(SUPPORTS)
    public List<HistoryItem> listVersionsByUUID(SclFileType type, UUID id) {
        var items = repository.listVersionsByUUID(type, id);
        if (items.isEmpty()) {
            var message = String.format("No versions found for type '%s' with ID '%s'", type, id);
            throw new CompasNoDataFoundException(message);
        }
        return items
                .stream()
                .map(e -> new HistoryItem(e.getId(), e.getName(), e.getVersion(), e.getWho(), e.getWhen(), e.getWhat()))
                .toList();
    }

    /**
     * Get the latest version of a specific SCL XML File (using the UUID) for a specific type.
     *
     * @param type The type to search for.
     * @param id   The UUID of the record to search for.
     * @return The latest version of the SCL XML Files.
     */
    @Transactional(SUPPORTS)
    public String findByUUID(SclFileType type, UUID id) {
        return repository.findByUUID(type, id);
    }

    /**
     * Get a specific version of a specific SCL XML File (using the UUID) for a specific type.
     *
     * @param id      The UUID of the record to search for.
     * @param version The version to search for.
     * @return The found version of the SCL XML Files.
     */
    @Transactional(SUPPORTS)
    public String findByUUID(UUID id, Version version) {
        return repository.findByUUID(id, version);
    }

    /**
     * Get a specific version of a specific SCL XML File (using the UUID) for a specific type.
     *
     * @param type    The type to search for.
     * @param id      The UUID of the record to search for.
     * @param version The version to search for.
     * @return The found version of the SCL XML Files.
     */
    @Transactional(SUPPORTS)
    public String findByUUID(SclFileType type, UUID id, Version version) {
        return repository.findByUUID(type, id, version);
    }

    /**
     * Create a new record for the passed SCL XML File with the passed name for a specific type.
     * A new UUID is generated to be set and also the CoMPAS Private Elements are added on SCL Level.
     *
     * @param type    The type to create it for.
     * @param name    The name that will be stored as CoMPAS Private extension.
     * @param comment Some comments that will be added to the THistory entry being added.
     * @param sclData The SCL XML File to store.
     * @return The ID of the new created SCL XML File in the database.
     */
    @Transactional(REQUIRED)
    public String create(SclFileType type, String name, String who, String comment, String sclData) {
        return create(type, name, who, comment, sclData, false);
    }

    /**
     * Create a new record for the passed SCL XML File with the passed name for a specific type.
     * A new UUID is generated to be set and also the CoMPAS Private Elements are added on SCL Level.
     *
     * @param type             The type to create it for.
     * @param name             The name that will be stored as CoMPAS Private extension.
     * @param comment          Some comments that will be added to the THistory entry being added.
     * @param sclData          The SCL XML File to store.
     * @param isHistoryEnabled Feature Flag to enable creation of history entries in a history repository.
     * @return The ID of the new created SCL XML File in the database.
     */
    @Transactional(REQUIRED)
    public String create(SclFileType type, String name, String who, String comment, String sclData, boolean isHistoryEnabled) {
        var when = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
        var scl = converter.convertToElement(new BufferedInputStream(new ByteArrayInputStream(sclData.getBytes(StandardCharsets.UTF_8))), SCL_ELEMENT_NAME, SCL_NS_URI);
        if (scl == null) {
            throw new CompasException(NO_SCL_ELEMENT_FOUND_ERROR_CODE, "No valid SCL found in the passed SCL Data.");
        }

        if (hasDuplicateSclName(type, name)) {
            throw new CompasException(DUPLICATE_SCL_NAME_ERROR_CODE, "Given name of SCL File already used.");
        }

        // A unique ID is generated to store it under.
        var id = UUID.randomUUID();
        // When the SCL is created the version will be set to 1.0.0
        var version = new Version(1, 0, 0);

        // Update the Header of the SCL (or create if not exists.)
        var header = createOrUpdateHeader(scl, id, version);
        sclElementProcessor.cleanupHistoryItem(header, version);
        var what = createMessage("SCL created", comment);
        sclElementProcessor.addHistoryItem(header, who, when, what, version);

        // Update or add the Compas Private Element to the SCL File.
        setSclCompasPrivateElement(scl, name, type);

        // Validate the labels from the Private Element, if there are any.
        var labels = validateLabels(scl);

        var newSclData = converter.convertToString(scl);
        repository.create(type, id, name, newSclData, version, who, labels);

        if (isHistoryEnabled) {
            repository.createHistoryVersion(id, name, version, type, who, what, OffsetDateTime.parse(when), false, true);
        }

        return newSclData;
    }

    public boolean hasDuplicateSclName(SclFileType type, String name) {
        return repository.hasDuplicateSclName(type, name);
    }

    /**
     * Create a new version of a specific SCL XML File. The content will be the passed SCL XML File.
     * The UUID and new version (depending on the passed ChangeSetType) are set and
     * the CoMPAS Private elements will also be copied, the SCL Name will only be copied if it isn't available in the
     * SCL XML File.
     *
     * @param type          The type to update it for.
     * @param id            The UUID of the record to update.
     * @param changeSetType The type of change to determine the new version.
     * @param comment       Some comments that will be added to the THistory entry being added.
     * @param sclData       The SCL XML File with the updated content.
     */
    @Transactional(REQUIRED)
    public String update(SclFileType type, UUID id, ChangeSetType changeSetType, String who, String comment, String sclData) {
        return update(type, id, changeSetType, who, comment, sclData, false);
    }

    /**
     * Create a new version of a specific SCL XML File. The content will be the passed SCL XML File.
     * The UUID and new version (depending on the passed ChangeSetType) are set and
     * the CoMPAS Private elements will also be copied, the SCL Name will only be copied if it isn't available in the
     * SCL XML File.
     *
     * @param type             The type to update it for.
     * @param id               The UUID of the record to update.
     * @param changeSetType    The type of change to determine the new version.
     * @param comment          Some comments that will be added to the THistory entry being added.
     * @param sclData          The SCL XML File with the updated content.
     * @param isHistoryEnabled Feature Flag to enable creation of history entries in a history repository.
     */
    @Transactional(REQUIRED)
    public String update(SclFileType type, UUID id, ChangeSetType changeSetType, String who, String comment, String sclData, boolean isHistoryEnabled) {
        var when = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());
        var scl = converter.convertToElement(new BufferedInputStream(new ByteArrayInputStream(sclData.getBytes(StandardCharsets.UTF_8))), SCL_ELEMENT_NAME, SCL_NS_URI);
        if (scl == null) {
            throw new CompasException(NO_SCL_ELEMENT_FOUND_ERROR_CODE, "No valid SCL found in the passed SCL Data.");
        }

        var currentSclMetaInfo = repository.findMetaInfoByUUID(type, id);
        var newFileName = getFilenameFromXML(scl);

        if (newFileName.isPresent()
                && !newFileName.get().equals(currentSclMetaInfo.getName())
                && repository.hasDuplicateSclName(type, newFileName.get())) {
            throw new CompasException(DUPLICATE_SCL_NAME_ERROR_CODE, "Given name of SCL File already used.");
        }

        // We always add a new version to the database, so add version record to the SCL and create a new record.
        var version = new Version(currentSclMetaInfo.getVersion());
        version = version.getNextVersion(changeSetType);

        // Update the Header of the SCL (or create if not exists.)
        var header = createOrUpdateHeader(scl, id, version);
        sclElementProcessor.cleanupHistoryItem(header, version);
        var what = createMessage("SCL updated", comment);
        sclElementProcessor.addHistoryItem(header, who, when, what, version);

        // Update or add the Compas Private Element to the SCL File.
        var newSclName = newFileName.orElse(currentSclMetaInfo.getName());
        setSclCompasPrivateElement(scl, newSclName, type);

        // Validate the labels from the Private Element, if there are any.
        var labels = validateLabels(scl);

        var newSclData = converter.convertToString(scl);
        repository.create(type, id, newSclName, newSclData, version, who, labels);

        if (isHistoryEnabled) {
            repository.createHistoryVersion(id, newSclName, version, type, who, what, OffsetDateTime.parse(when), false, true);
        }

        return newSclData;
    }

    /**
     * Delete all versions for a specific SCL File using it's ID.
     *
     * @param type                      The type of SCL where to find the SCL File
     * @param id                        The ID of the SCL File to delete.
     * @param isPersistentDeleteEnabled Feature Flag for a soft delete to mark the data as deleted instead of deleting it.
     */
    @Transactional(REQUIRED)
    public void delete(SclFileType type, UUID id, boolean isPersistentDeleteEnabled) {
        if (isPersistentDeleteEnabled) {
            repository.softDelete(type, id);
        } else {
            repository.delete(type, id);
        }
    }

    /**
     * Delete passed versions for a specific SCL File using it's ID.
     *
     * @param type                      The type of SCL where to find the SCL File
     * @param id                        The ID of the SCL File to delete.
     * @param version                   The version of that SCL File to delete.
     * @param isPersistentDeleteEnabled Feature Flag for a soft delete to mark the data as deleted instead of deleting it.
     */
    @Transactional(REQUIRED)
    public void deleteVersion(SclFileType type, UUID id, Version version, boolean isPersistentDeleteEnabled) {
        if (isPersistentDeleteEnabled) {
            repository.softDeleteVersion(type, id, version);
        } else {
            repository.deleteVersion(type, id, version);
        }
    }

    /**
     * Retrieve the Header from the SCL Fiel or create one if it doesn't exist and set the ID and
     * version on the Header.
     *
     * @param scl     The SCL file to edit.
     * @param id      The ID of the SCL File.
     * @param version The Version of the SCL File.
     * @return The header that was found or created.
     */
    private Element createOrUpdateHeader(Element scl, UUID id, Version version) {
        var header = sclElementProcessor.getSclHeader(scl)
                .orElseGet(() -> sclElementProcessor.addSclHeader(scl));
        header.setAttribute(SCL_ID_ATTR, id.toString());
        header.setAttribute(SCL_VERSION_ATTR, version.toString());

        return header;
    }

    /**
     * Retrieve the CoMPAS SCL Filename from the private element of CoMPAS.
     *
     * @param scl The SCL file to edit.
     * @return If there was a private SclName the value of this tag.
     */
    private Optional<String> getFilenameFromXML(Element scl) {
        return sclElementProcessor.getCompasPrivate(scl)
                .stream()
                .map(compasPrivate -> sclElementProcessor.getChildNodeByName(compasPrivate, COMPAS_SCL_NAME_EXTENSION,
                        COMPAS_EXTENSION_NS_URI))
                .flatMap(Optional::stream)
                .map(Element::getTextContent)
                .findFirst();
    }

    /**
     * Create/update the CoMPAS private element on the SCL Element for the given file.
     *
     * @param scl      The SCL file to edit.
     * @param name     The name to add.
     * @param fileType The file type to add.
     */
    private void setSclCompasPrivateElement(Element scl, String name, SclFileType fileType) {
        var compasPrivate = sclElementProcessor.getCompasPrivate(scl)
                .orElseGet(() -> sclElementProcessor.addCompasPrivate(scl));

        sclElementProcessor.getChildNodeByName(compasPrivate, COMPAS_SCL_NAME_EXTENSION, COMPAS_EXTENSION_NS_URI)
                .ifPresentOrElse(
                        // Override the value of the element with the name passed.
                        element -> element.setTextContent(name),
                        () -> sclElementProcessor.addCompasElement(compasPrivate, COMPAS_SCL_NAME_EXTENSION, name)
                );

        // Always set the file type as private element.
        sclElementProcessor.getChildNodeByName(compasPrivate, COMPAS_SCL_FILE_TYPE_EXTENSION, COMPAS_EXTENSION_NS_URI)
                .ifPresentOrElse(
                        element -> element.setTextContent(fileType.toString()),
                        () -> sclElementProcessor.addCompasElement(compasPrivate, COMPAS_SCL_FILE_TYPE_EXTENSION,
                                fileType.toString())
                );
    }

    /**
     * If a comment is added by the user, a standard message will be joined together with the comment from the user.
     *
     * @param standardMessage The standard message.
     * @param comment         The comment a user may have added.
     * @return The full message to be added to the HItem.
     */
    private String createMessage(String standardMessage, String comment) {
        var message = standardMessage;
        if (comment != null && !comment.isBlank()) {
            message += ", " + comment;
        }
        return message;
    }

    /**
     * Retrieve the Private Element from the SCL and validate if all Labels are correct, if any available.
     *
     * @param scl The SCL file to edit.
     * @return List of all Labels retrieved from the SCL XML File.
     */
    List<String> validateLabels(Element scl) {
        // Get all the Label Elements from the CoMPAS Private.
        var labelElements = sclElementProcessor.getCompasPrivate(scl)
                .map(compasPrivate -> sclElementProcessor.getChildNodeByName(compasPrivate, COMPAS_LABELS_EXTENSION,
                        COMPAS_EXTENSION_NS_URI))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(labelsElement -> sclElementProcessor.getChildNodesByName(labelsElement, COMPAS_LABEL_EXTENSION,
                        COMPAS_EXTENSION_NS_URI))
                .stream().flatMap(List::stream)
                .toList();
        if (labelElements.size() > 20) {
            throw new CompasSclDataServiceException(TOO_MANY_LABEL_ERROR_CODE,
                    "Only 20 Labels are allowed per SCL File");
        }

        var invalidLabels = labelElements.stream()
                .filter(labelElement -> !validateLabel(labelElement))
                .map(Node::getTextContent)
                .toList();
        if (!invalidLabels.isEmpty()) {
            throw new CompasSclDataServiceException(INVALID_LABEL_ERROR_CODE,
                    "Invalid label(s) passed in the CoMPAS Private Element (%s)"
                            .formatted(invalidLabels.stream().collect(Collectors.joining("','", "'", "'"))));
        }

        return labelElements.stream()
                .map(Element::getTextContent)
                .toList();
    }

    /**
     * Check if the label meets the expected RegEx.
     *
     * @param labelElement The Element which contains the Label to be checked.
     * @return true, if label meets RegEx, otherwise false.
     */
    boolean validateLabel(Element labelElement) {
        String label = labelElement.getTextContent();
        return label.matches("[A-Za-z][0-9A-Za-z_-]*");
    }

    /**
     * List the latest version of all SCL File history entries.
     *
     * @return The List of Items found.
     */
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistory() {
        return repository.listHistory();
    }

    /**
     * List the latest version of all SCL File history entries.
     *
     * @return The List of Items found.
     */
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistory(UUID id) {
        return repository.listHistory(id);
    }

    /**
     * List the latest version of all SCL File history entries.
     *
     * @return The List of Items found.
     */
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistory(SclFileType type, String name, String author, OffsetDateTime from, OffsetDateTime to) {
        return repository.listHistory(type, name, author, from, to);
    }

    /**
     * List the history entries of an SCL File specified by an uuid.
     *
     * @return The List of Items found.
     */
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistoryVersionsByUUID(UUID id) {
        return repository.listHistoryVersionsByUUID(id);
    }

    /**
     * Find the Location with the specified uuid
     *
     * @param id uuid of the Location
     * @return The Location entry with the matching id
     */
    @Transactional(SUPPORTS)
    public ILocationMetaItem findLocationByUUID(UUID id) {
        return repository.findLocationByUUID(id);
    }

    /**
     * List all Locations
     *
     * @param page The current page number to be displayed
     * @param pageSize The amount of entries per page
     * @return All Location entries in a paginated list
     */
    @Transactional(SUPPORTS)
    public List<ILocationMetaItem> listLocations(int page, int pageSize) {
        return repository.listLocations(page, pageSize);
    }

    /**
     * Create a Location entry according to the supplied parameters
     *
     * @param key The key value of the Location entry
     * @param name The name value of the Location entry
     * @param description The description value of the Location entry
     * @return The created Location entry
     */
    @Transactional(REQUIRED)
    public ILocationMetaItem createLocation(String key, String name, String description) {
        return repository.createLocation(UUID.randomUUID(), key, name, description);
    }

    /**
     * Delete the Location entry with the supplied id
     *
     * @param id The id of the Location entry to be deleted
     */
    @Transactional(REQUIRED)
    public void deleteLocation(UUID id) {
        repository.deleteLocation(id);
    }

    /**
     * Update a Location entry with the supplied parameter value
     *
     * @param id The id of the Location to be updated
     * @param key The updated key of the Location entry
     * @param name The updated name of the Location entry
     * @param description The updated description of the Location entry
     * @return The updated Location entry
     */
    @Transactional(REQUIRED)
    public ILocationMetaItem updateLocation(UUID id, String key, String name, String description) {
        return repository.updateLocation(id, key, name, description);
    }

    /**
     * Assigns a Resource id to a Location entry
     *
     * @param locationId The id of the Location entry
     * @param resourceId The id of the Resource
     */
    @Transactional(REQUIRED)
    public void assignResourceToLocation(UUID locationId, UUID resourceId) {
        repository.assignResourceToLocation(locationId, resourceId);
    }

    /**
     * Unassigns a Resource id from a Location entry
     *
     * @param locationId The id of the Location entry
     * @param resourceId The id of the Resource entry
     */
    @Transactional(REQUIRED)
    public void unassignResourceFromLocation(UUID locationId, UUID resourceId) {
        repository.unassignResourceFromLocation(locationId, resourceId);
    }
}
