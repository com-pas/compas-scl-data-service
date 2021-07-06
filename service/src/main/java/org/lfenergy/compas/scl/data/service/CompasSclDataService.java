// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.service;

import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.repository.SclDataRepositoryException;
import org.lfenergy.compas.scl.data.util.SclElementProcessor;
import org.w3c.dom.Element;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.lfenergy.compas.scl.data.Constants.*;

@ApplicationScoped
public class CompasSclDataService {
    private final CompasSclDataRepository repository;
    private final SclElementProcessor sclElementProcessor;

    @Inject
    public CompasSclDataService(CompasSclDataRepository repository) {
        this.repository = repository;
        this.sclElementProcessor = new SclElementProcessor();
    }

    public List<Item> list(SclType type) {
        return repository.list(type);
    }

    public List<Item> listVersionsByUUID(SclType type, UUID id) {
        return repository.listVersionsByUUID(type, id);
    }

    public Element findByUUID(SclType type, UUID id) {
        return repository.findByUUID(type, id);
    }

    public Element findByUUID(SclType type, UUID id, Version version) {
        return repository.findByUUID(type, id, version);
    }

    public UUID create(SclType type, String name, Element scl) {
        var id = UUID.randomUUID();
        // When the SCL is create the version will be set to 1.0.0
        var version = new Version(1, 0, 0);
        var header = sclElementProcessor.getSclHeader(scl)
                .orElseGet(() -> sclElementProcessor.addSclHeader(scl));
        header.setAttribute(SCL_HEADER_ID_ATTR, id.toString());
        header.setAttribute(SCL_HEADER_VERSION_ATTR, version.toString());

        // Set name and type to SCL before storing the SCL.
        setSclCompasPrivateElement(scl, Optional.empty(), Optional.of(name), type);

        repository.create(type, id, scl, version);
        return id;
    }

    public void update(SclType type, UUID id, ChangeSetType changeSetType, Element scl) {
        var currentScl = repository.findByUUID(type, id);
        // We always add a new version to the database, so add version record to the SCL and create a new record.
        var currentHeader = sclElementProcessor.getSclHeader(currentScl)
                .orElseThrow(() -> new SclDataRepositoryException("Previous version doesn't contain header!"));
        var version = new Version(currentHeader.getAttribute(SCL_HEADER_VERSION_ATTR));
        version = version.getNextVersion(changeSetType);
        var header = sclElementProcessor.getSclHeader(scl)
                .orElseGet(() -> sclElementProcessor.addSclHeader(scl));
        header.setAttribute(SCL_HEADER_ID_ATTR, id.toString());
        header.setAttribute(SCL_HEADER_VERSION_ATTR, version.toString());

        // Add name and type to SCL before storing the SCL.
        setSclCompasPrivateElement(scl, Optional.of(currentScl), Optional.empty(), type);

        repository.create(type, id, scl, version);
    }

    public void delete(SclType type, UUID id) {
        repository.delete(type, id);
    }

    public void delete(SclType type, UUID id, Version version) {
        repository.delete(type, id, version);
    }

    /**
     * Set the full CoMPAS private element for the given SCL file.
     *
     * @param scl      the SCL file to edit.
     * @param name     the name to add
     * @param fileType the file type to add.
     */
    private void setSclCompasPrivateElement(Element scl, Optional<Element> currentScl, Optional<String> name, SclType fileType) {
        var compasPrivate = sclElementProcessor.getCompasPrivate(scl)
                .orElseGet(() -> sclElementProcessor.addCompasPrivate(scl));

        // If the new SCL contains the Name Element we will use that value (or set the new name if passed to this method)
        // Otherwise if there is no Name Element there are 2 options, if the new name is passed that will be used to create a Name Element
        // If no name is passed, but a previous version of the SCL exists, we will copy the Name Element from there.
        sclElementProcessor.getChildNodeByName(compasPrivate, COMPAS_SCL_NAME_EXTENSION)
                .ifPresentOrElse(
                        // Override the value of the element with the name passed.
                        element -> name.ifPresent(element::setTextContent),
                        () -> name.ifPresentOrElse(
                                // Add the Compas Element and give it a value with the name passed.
                                value -> sclElementProcessor.addCompasElement(compasPrivate, COMPAS_SCL_NAME_EXTENSION, value),
                                // Retrieve the name from the previous version and copy that value, if present, to the
                                // new Element that will be added.
                                () -> currentScl
                                        .flatMap(sclElementProcessor::getCompasPrivate)
                                        .flatMap(previousCompasPrivate ->
                                                sclElementProcessor.getChildNodeByName(previousCompasPrivate, COMPAS_SCL_NAME_EXTENSION))
                                        .ifPresent(previousSclName ->
                                                sclElementProcessor.addCompasElement(compasPrivate, COMPAS_SCL_NAME_EXTENSION, previousSclName.getTextContent()))
                        )
                );

        // Always set the file type as private element.
        sclElementProcessor.getChildNodeByName(compasPrivate, COMPAS_SCL_FILE_TYPE_EXTENSION)
                .ifPresentOrElse(
                        element -> element.setTextContent(fileType.toString()),
                        () -> sclElementProcessor.addCompasElement(compasPrivate, COMPAS_SCL_FILE_TYPE_EXTENSION, fileType.toString())
                );
    }
}
