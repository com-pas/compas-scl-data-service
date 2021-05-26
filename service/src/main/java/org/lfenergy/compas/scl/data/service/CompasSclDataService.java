// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.service;

import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
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
        // TODO: Add name and type to SCL before storing the SCL.

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
}
