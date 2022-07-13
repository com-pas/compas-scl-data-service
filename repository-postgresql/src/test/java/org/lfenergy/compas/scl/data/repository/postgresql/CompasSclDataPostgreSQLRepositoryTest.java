// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository.postgresql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.AbstractCompasSclDataRepositoryTest;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({MockitoExtension.class, PostgreSQLServerJUnitExtension.class})
class CompasSclDataPostgreSQLRepositoryTest extends AbstractCompasSclDataRepositoryTest {
    private CompasSclDataPostgreSQLRepository repository;

    @Override
    protected CompasSclDataRepository getRepository() {
        return repository;
    }

    @BeforeEach
    void beforeEach() {
        repository = new CompasSclDataPostgreSQLRepository(PostgreSQLServerJUnitExtension.getDataSource());
    }

    /*
     * TODO: Method beneath needs to be moved to AbstractCompasSclDataRepositoryTest
     * when hasDuplicateSclName has been implemented by CompasSclDataBaseXRepository. */
    @Test
    void hasDuplicateSclName_WhenUsingSclNameThatHasBeenUsedYet_ThenDuplicateIsFound() {
        var expectedVersion = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, expectedVersion, NAME_1);
        getRepository().create(TYPE, uuid, NAME_1, scl, expectedVersion, WHO);

        assertTrue(getRepository().hasDuplicateSclName(TYPE, NAME_1));
    }
}