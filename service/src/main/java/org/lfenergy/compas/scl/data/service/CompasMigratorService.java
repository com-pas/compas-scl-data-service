// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.lfenergy.compas.scl.data.repository.CompasMigrator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class CompasMigratorService {
    private CompasMigrator compasMigrator;

    @Inject
    public CompasMigratorService(CompasMigrator compasMigrator) {
        this.compasMigrator = compasMigrator;
    }

    public boolean migrate() {
        return compasMigrator.migrate();
    }
}
