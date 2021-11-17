// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.repository.CompasMigrator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasMigratorServiceTest {
    @Mock
    private CompasMigrator compasMigrator;

    @InjectMocks
    private CompasMigratorService compasMigratorService;

    @Test
    void migrate_WhenCalled_ThenMigratorIsCalled() {
        when(compasMigrator.migrate()).thenReturn(true);

        assertTrue(compasMigratorService.migrate());
        verify(compasMigrator, times(1)).migrate();
    }
}