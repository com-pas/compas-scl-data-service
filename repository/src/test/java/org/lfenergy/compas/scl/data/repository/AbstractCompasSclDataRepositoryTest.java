// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.NO_DATA_FOUND_ERROR_CODE;

public abstract class AbstractCompasSclDataRepositoryTest {
    protected static final SclFileType TYPE = SclFileType.SCD;

    // Use different types, so tests don't conflict with each other.
    protected static final SclFileType LIST1_TYPE = SclFileType.CID;
    protected abstract CompasSclDataRepository getRepository();

    @Test
    void list_WhenCalledOnEmptyDatabase_ThenNoRecordsReturned() {
        var items = getRepository().list(LIST1_TYPE);

        assertNotNull(items);
        assertEquals(0, items.size());
    }

    @Test
    void listVersionsByUUID_WhenCalledWithUnknownID_ThenExceptionIsThrown() {
        var uuid = UUID.randomUUID();

        var items = getRepository().listVersionsByUUID(TYPE, uuid);

        assertNotNull(items);
        assertEquals(0, items.size());
    }

    @Test
    void findByUUID_WhenCalledWithUnknownUUID_ThenExceptionIsThrown() {
        var uuid = UUID.randomUUID();

        var repository = getRepository();
        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> repository.findByUUID(TYPE, uuid));
        assertEquals(NO_DATA_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void findMetaInfoByUUID_WhenCalledWithUnknownUUID_ThenExceptionIsThrown() {
        var uuid = UUID.randomUUID();

        var repository = getRepository();
        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> repository.findMetaInfoByUUID(TYPE, uuid));
        assertEquals(NO_DATA_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void delete_AllVersions_SoftDeleted() {
        UUID uuid = UUID.randomUUID();
        Version version1 = new Version(1, 0, 0);
        Version version2 = new Version(2, 0, 0);

        var repository = getRepository();
        repository.create(TYPE, uuid, "TestName", "<SCL></SCL>", version1, "tester", List.of("label"));
        repository.create(TYPE, uuid, "TestName", "<SCL></SCL>", version2, "tester", List.of("label"));

        repository.delete(TYPE, uuid);

        var items = repository.listVersionsByUUID(TYPE, uuid);
        assertEquals(0, items.size(), "All versions should be soft deleted");
    }

    @Test
    void delete_SpecificVersion_Deleted() {
        UUID uuid = UUID.randomUUID();
        Version version1 = new Version(1, 0, 0);
        Version version2 = new Version(2, 0, 0);

        var repository = getRepository();
        repository.create(TYPE, uuid, "TestName", "<SCL></SCL>", version1, "tester", List.of("label"));
        repository.create(TYPE, uuid, "TestName", "<SCL></SCL>", version2, "tester", List.of("label"));

        repository.delete(TYPE, uuid, version1);

        var items = repository.listVersionsByUUID(TYPE, uuid);
        assertEquals(1, items.size(), "Only one version should remain");
        assertEquals(version2.toString(), items.get(0).getVersion(), "Remaining version should be version2");
    }
}
