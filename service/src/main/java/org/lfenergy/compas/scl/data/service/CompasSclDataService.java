// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.service;

import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class CompasSclDataService {
    private CompasSclDataRepository repository;

    @Inject
    public CompasSclDataService(CompasSclDataRepository repository) {
        this.repository = repository;
    }

    public SCL findSCLByUUID(SclType type, UUID uuid) {
        return repository.findSCLByUUID(type, uuid);
    }

    public SCL findSCLByUUID(SclType type, UUID uuid, Version version) {
        return repository.findSCLByUUID(type, uuid, version);
    }

    public UUID create(SclType type, String name, SCL scl) {
        // When the SCL is create the version will be set to 1.0.0
        var uuid = UUID.randomUUID();
        var version = new Version(1, 0, 0);
        scl.getHeader().setVersion(version.toString());
        // TODO: Add name and type to SCL before storing the SCL.

        repository.create(type, uuid, scl, version);
        return uuid;
    }

    public void update(SclType type, UUID uuid, ChangeSetType changeSetType, SCL scl) {
        // We always add a new version to the database, so add version record to the SCL and create a new record.
        // TODO: Add version record to SCL.
        var currentSCL = repository.findSCLByUUID(type, uuid);
        var version = new Version(currentSCL.getHeader().getVersion());
        version = version.getNextVersion(changeSetType);
        scl.getHeader().setVersion(version.toString());
        // TODO: Add name and type to SCL before storing the SCL. Retrieve name from original SCL.
        var name = "";

        repository.create(type, uuid, scl, version);
    }

    public void delete(SclType type, UUID uuid) {
        repository.delete(type, uuid);
    }

    public void delete(SclType type, UUID uuid, Version version) {
        repository.delete(type, uuid, version);
    }
}
