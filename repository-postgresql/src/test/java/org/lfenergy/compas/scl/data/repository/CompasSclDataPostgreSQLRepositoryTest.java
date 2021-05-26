// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CompasSclDataPostgreSQLRepositoryTest {
    @InjectMocks
    private CompasSclDataPostgreSQLRepository repository;

    @Test
    void list_WhenCalled_ThenUnsupportedExceptionThrown() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.list(SclType.SCD);
        });
    }

    @Test
    void listSCLVersionsByUUID_WhenCalled_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.listSCLVersionsByUUID(SclType.SCD, uuid);
        });
    }

    @Test
    void findSCLByUUID_WhenCalledWithoutVersion_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.findSCLByUUID(SclType.SCD, uuid);
        });
    }

    @Test
    void findSCLByUUID_WhenCalledWithVersion_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        var version = new Version(1, 0, 0);
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.findSCLByUUID(SclType.SCD, uuid, version);
        });
    }

    @Test
    void create_WhenCalled_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        var scl = new SCL();
        var version = new Version(1, 0, 0);
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.create(SclType.SCD, uuid, scl, version);
        });
    }

    @Test
    void delete_WhenCalledWithoutVersion_ThenUnsupportedExceptionThrown() {
        UUID uuid = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.delete(SclType.SCD, uuid);
        });
    }

    @Test
    void delete_WhenCalledWithVersion_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        var version = new Version(1, 0, 0);
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.delete(SclType.SCD, uuid, version);
        });
    }
}