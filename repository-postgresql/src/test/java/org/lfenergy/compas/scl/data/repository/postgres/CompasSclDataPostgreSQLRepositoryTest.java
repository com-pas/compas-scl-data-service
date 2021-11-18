// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository.postgres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({MockitoExtension.class, PostgreSQLServerJUnitExtension.class})
class CompasSclDataPostgreSQLRepositoryTest {
    private CompasSclDataPostgreSQLRepository repository;

    @BeforeEach
    void beforeEach() {
        repository = new CompasSclDataPostgreSQLRepository(PostgreSQLServerJUnitExtension.getDataSource());
    }

    @Test
    void list_WhenCalled_ThenUnsupportedExceptionThrown() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.list(SclType.SCD);
        });
    }

    @Test
    void listVersionsByUUID_WhenCalled_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.listVersionsByUUID(SclType.SCD, uuid);
        });
    }

    @Test
    void findByUUID_WhenCalledWithoutVersion_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.findByUUID(SclType.SCD, uuid);
        });
    }

    @Test
    void findByUUID_WhenCalledWithVersion_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        var version = new Version(1, 0, 0);
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.findByUUID(SclType.SCD, uuid, version);
        });
    }

    @Test
    void findMetaInfoByUUID_WhenCalled_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.findMetaInfoByUUID(SclType.SCD, uuid);
        });
    }

    @Test
    void create_WhenCalled_ThenUnsupportedExceptionThrown() {
        var uuid = UUID.randomUUID();
        var scl = "<SCL></SCL>";
        var name = "SCL-Name";
        var version = new Version(1, 0, 0);
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.create(SclType.SCD, uuid, name, scl, version);
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