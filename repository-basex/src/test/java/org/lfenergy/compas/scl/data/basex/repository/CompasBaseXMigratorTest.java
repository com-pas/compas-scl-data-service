// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.basex.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class CompasBaseXMigratorTest {
    @InjectMocks
    private CompasBaseXMigrator compasBaseXMigrator;

    @Test
    void migrate_WhenCalled_ThenMigratorIsCalled() {
        // Call should just work, because nothing is done.
        compasBaseXMigrator.migrate();
    }

}