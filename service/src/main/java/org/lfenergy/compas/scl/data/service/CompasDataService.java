// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.service;

import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.repository.CompasDataRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class CompasDataService {
    private CompasDataRepository repository;

    @Inject
    public CompasDataService(CompasDataRepository repository) {
        this.repository = repository;
    }

    public SCL findSCLByUUID(SclType type, UUID uuid) {
        return repository.findSCLByUUID(type, uuid);
    }

    public UUID create(SclType type, String name, SCL scl) {
        return repository.create(type, name, scl);
    }

    public UUID update(SclType type, UUID uuid, ChangeSetType changeSetType, SCL scl) {
        // We always add a new version to the database, so add version record to the SCL and create a new record.
        SCL currentSCL = repository.findSCLByUUID(type, uuid);
        // TODO: Add version record to SCL.

        // TODO: Retrieve name from SCL
        String name = "";
        return repository.create(type, name, scl);
    }

    public void delete(SclType type, UUID uuid) {
        repository.delete(type, uuid);
    }
}
