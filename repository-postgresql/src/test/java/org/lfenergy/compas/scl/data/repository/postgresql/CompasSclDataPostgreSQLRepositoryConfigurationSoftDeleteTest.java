// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository.postgresql;

import io.quarkus.arc.ClientProxy;
import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.component.TestConfigProperty;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@QuarkusComponentTest(CompasSclDataPostgreSQLRepositoryConfiguration.class)
@TestConfigProperty(key = "compas.scl-data-service.features.soft-delete-enabled", value = "true")
class CompasSclDataPostgreSQLRepositoryConfigurationSoftDeleteTest {

    @Inject
    CompasSclDataRepository repository;

    @Test
    void whenSoftDeleteEnabled_thenReturnsSoftDeleteRepository() {
        assertInstanceOf(SoftDeleteCompasSclDataPostgreSQLRepository.class, ClientProxy.unwrap(repository));
    }
}
