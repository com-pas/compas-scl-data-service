// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class CompasSclDataServicePostgreSQLConfigurationTest {
    @Test
    void creatCompasSclDataRepositoryProduction_WhenCalled_ThenObjectReturned() {
        var datasource = mock(DataSource.class);

        assertNotNull(new CompasSclDataServicePostgreSQLConfiguration().creatCompasSclDataRepositoryProduction(datasource));
    }

    @Test
    void creatCompasSclDataRepositoryDevelopment_WhenCalled_ThenObjectReturned() {
        var datasource = mock(DataSource.class);

        assertNotNull(new CompasSclDataServicePostgreSQLConfiguration().creatCompasSclDataRepositoryDevelopment(datasource));
    }
}