// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository.postgresql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.model.Version;

import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class, PostgreSQLServerJUnitExtension.class})
class CompasSclDataPostgreSQLRepositoryConfigurationTest {
    private SoftDeleteCompasSclDataPostgreSQLRepository repository;
    private static final String TYPE = "SCD";

    @BeforeEach
    void setUp() {
        repository = new SoftDeleteCompasSclDataPostgreSQLRepository(PostgreSQLServerJUnitExtension.getDataSource());
    }

    @Test
    void delete_WhenCalledWithExistingUUID_ThenRecordsAreSoftDeleted() {
        UUID uuid = UUID.randomUUID();
        Version version = new Version(1, 0, 0);
        repository.create(SclFileType.SCD, uuid, "TestName", "<SCL></SCL>", version, "tester", List.of("label"));

        repository.delete(SclFileType.SCD, uuid);

        var items = repository.listVersionsByUUID(SclFileType.SCD, uuid);
        assertEquals(0, items.size(), "All versions should be soft deleted");
    }

    @Test
    void deleteVersion_WhenCalledWithExistingUUIDAndVersion_ThenSpecificVersionIsDeleted() {
        UUID uuid = UUID.randomUUID();
        Version version1 = new Version(1, 0, 0);
        Version version2 = new Version(2, 0, 0);
        repository.create(SclFileType.SCD, uuid, "TestName", "<SCL></SCL>", version1, "tester", List.of("label"));
        repository.create(SclFileType.SCD, uuid, "TestName", "<SCL></SCL>", version2, "tester", List.of("label"));

        var items = repository.listVersionsByUUID(SclFileType.SCD, uuid);
        assertEquals(2, items.size(), "Exact to versions should be present before deletion");

        repository.delete(SclFileType.SCD, uuid, version1);

        items = repository.listVersionsByUUID(SclFileType.SCD, uuid);
        assertEquals(1, items.size(), "Only one version should remain");
        assertEquals(version2.toString(), items.get(0).getVersion(), "Remaining version should be version2");
    }
}