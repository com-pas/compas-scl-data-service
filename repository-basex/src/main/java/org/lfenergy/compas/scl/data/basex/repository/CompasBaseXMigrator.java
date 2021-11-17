// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.basex.repository;

import org.lfenergy.compas.scl.data.repository.CompasMigrator;

import javax.enterprise.context.ApplicationScoped;

/**
 * Implementation of the Database Migrator for BaseX called after startup.
 */
@ApplicationScoped
public class CompasBaseXMigrator implements CompasMigrator {
    @Override
    public void migrate() {
        // For now there is no migration needed in the BaseX Implementation of the repository.
    }
}
