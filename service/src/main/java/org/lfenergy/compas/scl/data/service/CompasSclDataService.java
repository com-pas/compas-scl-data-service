// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.service;

import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.TPrivate;
import org.lfenergy.compas.scl.compas.ObjectFactory;
import org.lfenergy.compas.scl.compas.TSclFileType;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.bind.JAXBElement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CompasSclDataService {
    private CompasSclDataRepository repository;

    @Inject
    public CompasSclDataService(CompasSclDataRepository repository) {
        this.repository = repository;
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

        setSclCompasPrivateElement(scl, name, type);

        repository.create(type, id, scl, version);
        return id;
    }

    public void update(SclType type, UUID id, ChangeSetType changeSetType, SCL scl) {
        var currentSCL = repository.findByUUID(type, id);
        // We always add a new version to the database, so add version record to the SCL and create a new record.
        var version = new Version(currentSCL.getHeader().getVersion());
        version = version.getNextVersion(changeSetType);
        scl.getHeader().setId(id.toString());
        scl.getHeader().setVersion(version.toString());
        // TODO: Add name and type to SCL before storing the SCL. Retrieve name from original SCL.
        var name = "";

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
     * @param scl the SCL file to edit.
     * @param filename the filename to add
     * @param fileType the file type to add.
     */
    private void setSclCompasPrivateElement(SCL scl, String filename, SclType fileType) {
        // Creating a private
        var compasPrivate = new TPrivate();
        var compasPrivateElementFactory = new ObjectFactory();

        // Setting the type (required for a SCL private element)
        compasPrivate.setType("compas_scl");

        // Adding the filename element and the file type element
        compasPrivate.getContent().add(compasPrivateElementFactory.createSclFilename(filename));

        // TODO: Make this work!
        // TSclFileType sclFileType = TSclFileType.valueOf(fileType.toString());
        // compasPrivate.getContent().add(compasPrivateElementFactory.createSclFileType(sclFileType));

        // Adding it to the SCL file.
        scl.getPrivate().add(compasPrivate);
    }
}
