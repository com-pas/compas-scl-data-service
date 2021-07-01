// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.service;

import org.lfenergy.compas.scl.commons.CompasExtensionsManager;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.extensions.model.ObjectFactory;
import org.lfenergy.compas.scl.extensions.model.TSclFileType;
import org.lfenergy.compas.scl.model.SCL;
import org.lfenergy.compas.scl.model.TPrivate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.lfenergy.compas.scl.extensions.commons.CompasExtensionsField.SCL_FILETYPE_EXTENSION;
import static org.lfenergy.compas.scl.extensions.commons.CompasExtensionsField.SCL_NAME_EXTENSION;

@ApplicationScoped
public class CompasSclDataService {
    private CompasSclDataRepository repository;
    private CompasExtensionsManager compasExtensionsManager;

    @Inject
    public CompasSclDataService(CompasSclDataRepository repository, CompasExtensionsManager compasExtensionsManager) {
        this.repository = repository;
        this.compasExtensionsManager = compasExtensionsManager;
    }

    public List<Item> list(SclType type) {
        return repository.list(type);
    }

    public List<Item> listVersionsByUUID(SclType type, UUID id) {
        return repository.listVersionsByUUID(type, id);
    }

    public SCL findByUUID(SclType type, UUID id) {
        return repository.findByUUID(type, id);
    }

    public SCL findByUUID(SclType type, UUID id, Version version) {
        return repository.findByUUID(type, id, version);
    }

    public UUID create(SclType type, String name, SCL scl) {
        var id = UUID.randomUUID();
        scl.getHeader().setId(id.toString());
        // When the SCL is create the version will be set to 1.0.0
        var version = new Version(1, 0, 0);
        scl.getHeader().setVersion(version.toString());

        // Set name and type to SCL before storing the SCL.
        setSclCompasPrivateElement(scl, Optional.empty(), Optional.of(name), type);

        repository.create(type, id, scl, version);
        return id;
    }

    public void update(SclType type, UUID id, ChangeSetType changeSetType, SCL scl) {
        var currentScl = repository.findByUUID(type, id);
        // We always add a new version to the database, so add version record to the SCL and create a new record.
        var version = new Version(currentScl.getHeader().getVersion());
        version = version.getNextVersion(changeSetType);
        scl.getHeader().setId(id.toString());
        scl.getHeader().setVersion(version.toString());

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
    @SuppressWarnings("unchecked")
    private void setSclCompasPrivateElement(SCL scl, Optional<SCL> currentScl, Optional<String> name, SclType fileType) {
        var compasPrivate = compasExtensionsManager.getCompasPrivate(scl)
                .orElseGet(() -> {
                    // Creating a private
                    var newPrivate = compasExtensionsManager.createCompasPrivate();
                    // Adding it to the SCL file.
                    scl.getPrivate().add(newPrivate);
                    return newPrivate;
                });

        // If the new SCL contains the Name Element we will use that value (or set the new name if passed to this method)
        // Otherwise if there is no Name Element there are 2 options, if the new name is passed that will be used to create a Name Element
        // If no name is passed, but a previous version of the SCL exists, we will copy the Name Element from there.
        compasExtensionsManager.getCompasElement(compasPrivate, SCL_NAME_EXTENSION)
                .ifPresentOrElse(
                        element -> name.ifPresent(element::setValue),
                        () -> name.ifPresentOrElse(
                                value -> addCompasName(compasPrivate, value),
                                () -> currentScl
                                        .flatMap(previousScl -> compasExtensionsManager.getCompasPrivate(previousScl))
                                        .flatMap(previousCompasPrivate -> compasExtensionsManager.getCompasSclName(previousCompasPrivate))
                                        .ifPresent(previousSclName -> addCompasName(compasPrivate, previousSclName))
                        )
                );

        // Always set the file type as private element.
        var sclFileType = TSclFileType.valueOf(fileType.toString());
        compasExtensionsManager.getCompasElement(compasPrivate, SCL_FILETYPE_EXTENSION)
                .ifPresentOrElse(
                        element -> element.setValue(sclFileType),
                        () -> addCompasType(compasPrivate, sclFileType)
                );
    }

    private void addCompasName(TPrivate compasPrivate, String name) {
        var compasPrivateElementFactory = new ObjectFactory();
        compasPrivate.getContent().add(compasPrivateElementFactory.createSclName(name));
    }

    private void addCompasType(TPrivate compasPrivate, TSclFileType sclFileType) {
        var compasPrivateElementFactory = new ObjectFactory();
        compasPrivate.getContent().add(compasPrivateElementFactory.createSclFileType(sclFileType));
    }
}
