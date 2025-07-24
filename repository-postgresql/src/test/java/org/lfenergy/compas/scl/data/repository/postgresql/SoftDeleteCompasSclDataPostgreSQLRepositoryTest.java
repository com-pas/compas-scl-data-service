// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository.postgresql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.repository.AbstractCompasSclDataRepositoryTest;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith({MockitoExtension.class, PostgreSQLServerJUnitExtension.class})
class SoftDeleteCompasSclDataPostgreSQLRepositoryTest extends AbstractCompasSclDataRepositoryTest {
    private SoftDeleteCompasSclDataPostgreSQLRepository repository;

    @Override
    protected CompasSclDataRepository getRepository() {
        return repository;
    }

    @BeforeEach
    void beforeEach() {
        repository = new SoftDeleteCompasSclDataPostgreSQLRepository(PostgreSQLServerJUnitExtension.getDataSource());
    }
}