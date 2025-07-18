// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

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
    void deleteVersion_WhenCalledWithExistingUUID_ThenSpecificVersionIsDeleted() {
        var uuid = UUID.randomUUID();

        getRepository().delete(TYPE, uuid);
    }

    @Test
    void deleteVersion_WhenCalledWithExistingUUIDAndVersion_ThenSpecificVersionIsDeleted() {
        var uuid = UUID.randomUUID();

        getRepository().delete(TYPE, uuid, new Version(1, 3, 2));
    }
}
