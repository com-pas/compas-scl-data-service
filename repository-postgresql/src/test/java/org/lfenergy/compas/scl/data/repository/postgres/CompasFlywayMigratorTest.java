// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository.postgres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({PostgreSQLServerJUnitExtension.class})
class CompasFlywayMigratorTest {
    private CompasFlywayMigrator compasFlywayMigrator;

    @BeforeEach
    void beforeEach() {
        compasFlywayMigrator = new CompasFlywayMigrator(PostgreSQLServerJUnitExtension.getDataSource());
    }

    @Test
    void migrate_WhenCalled_ThenFlywayMigratesDatabaseAndNoErrorShouldOccur() {
        assertTrue(compasFlywayMigrator.migrate());
    }
}