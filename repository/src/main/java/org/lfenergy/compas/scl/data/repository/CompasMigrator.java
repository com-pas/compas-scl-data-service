// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

/**
 * Interface used to migrate the database after the application is started.
 * Depending on the implementation different actions are executed.
 */
public interface CompasMigrator {
    /**
     * Method called to migrate the database to the new version of the software.
     */
    void migrate();
}
