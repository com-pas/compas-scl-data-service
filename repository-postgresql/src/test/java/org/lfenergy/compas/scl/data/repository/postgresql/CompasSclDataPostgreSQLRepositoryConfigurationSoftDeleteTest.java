// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository.postgresql;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@QuarkusTest
@TestProfile(SoftDeleteEnabledTestProfile.class)
class CompasSclDataPostgreSQLRepositoryConfigurationSoftDeleteTest {

    @Inject
    CompasSclDataRepository repository;

    @Test
    void whenSoftDeleteEnabled_thenReturnsSoftDeleteRepository() {
        assertInstanceOf(SoftDeleteCompasSclDataPostgreSQLRepository.class, repository);
    }
}
