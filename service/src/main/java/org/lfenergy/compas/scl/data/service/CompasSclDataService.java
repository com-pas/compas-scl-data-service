// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.service;

import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.TAnyContentFromOtherNamespace;
import org.lfenergy.compas.scl.TPrivate;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.extensions.ObjectFactory;
import org.lfenergy.compas.scl.extensions.TSclFileType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
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

        // Set name and type to SCL before storing the SCL.
        setSclCompasPrivateElement(scl, Optional.of(name), type);

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

        // Add name and type to SCL before storing the SCL.
        setSclCompasPrivateElement(scl, Optional.empty(), type);

        repository.create(type, id, scl, version);
    }

    public void delete(SclType type, UUID id) {
        repository.delete(type, id);
    }

    public void delete(SclType type, UUID id, Version version) {
        repository.delete(type, id, version);
    }

    private Optional<TPrivate> getCompasPrivate(SCL scl) {
        return scl.getPrivate()
                .stream()
                .filter(tPrivate -> tPrivate.getType().equals("compas_scl"))
                .findFirst();
    }

    private Optional<JAXBElement> getCompasElement(SCL scl, String name) {
        return getCompasPrivate(scl).stream()
                .map(TAnyContentFromOtherNamespace::getContent)
                .flatMap(List::stream)
                .filter(element -> element instanceof JAXBElement)
                .map(element -> (JAXBElement) element)
                .filter(element -> element.getName().equals(new QName("https://www.lfenergy.org/compas/v1", name)))
                .findFirst();
    }

    /**
     * Set the full CoMPAS private element for the given SCL file.
     *
     * @param scl      the SCL file to edit.
     * @param filename the filename to add
     * @param fileType the file type to add.
     */
    private void setSclCompasPrivateElement(SCL scl, Optional<String> filename, SclType fileType) {
        var compasPrivateElementFactory = new ObjectFactory();
        var compasPrivate = getCompasPrivate(scl)
                .orElseGet(() -> {
                    // Creating a private
                    var newPrivate = new TPrivate();
                    // Setting the type (required for a SCL private element)
                    newPrivate.setType("compas_scl");
                    // Adding it to the SCL file.
                    scl.getPrivate().add(newPrivate);
                    return newPrivate;
                });

        // Adding the filename element and the file type element
        filename.ifPresent(value ->
                getCompasElement(scl, "SclFilename")
                        .ifPresentOrElse(
                                element -> element.setValue(value),
                                () -> compasPrivate.getContent().add(compasPrivateElementFactory.createSclFilename(value)))
        );

        TSclFileType sclFileType = TSclFileType.valueOf(fileType.toString());
        getCompasElement(scl, "SclFileType")
                .ifPresentOrElse(
                        element -> element.setValue(sclFileType),
                        () -> compasPrivate.getContent().add(compasPrivateElementFactory.createSclFileType(sclFileType))
                );
    }
}
