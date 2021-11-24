// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.basex.client.BaseXClient;
import org.lfenergy.compas.scl.data.basex.client.BaseXClientFactory;
import org.lfenergy.compas.scl.data.util.SclDataModelMarshaller;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompasSclDataServiceBaseXConfigurationTest {
    @Test
    void createBaseXClientFactoryProduction_WhenCalled_ThenObjectReturned() {
        assertNotNull(new CompasSclDataServiceBaseXConfiguration()
                .createBaseXClientFactoryProduction("host", 8898, "admin", ""));
    }

    @Test
    void creatCompasSclDataRepositoryProduction_WhenCalled_ThenObjectReturned() throws IOException {
        var baseXClientFactory = mock(BaseXClientFactory.class);
        var sclDataModelMarshaller = mock(SclDataModelMarshaller.class);

        when(baseXClientFactory.createClient()).thenReturn(mock(BaseXClient.class));

        assertNotNull(new CompasSclDataServiceBaseXConfiguration()
                .creatCompasSclDataRepositoryProduction(baseXClientFactory, sclDataModelMarshaller));
    }

    @Test
    void createBaseXClientFactoryDevelopment_WhenCalled_ThenObjectReturned() {
        assertNotNull(new CompasSclDataServiceBaseXConfiguration()
                .createBaseXClientFactoryDevelopment("host", 8898, "admin", ""));
    }

    @Test
    void creatCompasSclDataRepositoryDevelopment_WhenCalled_ThenObjectReturned() throws IOException {
        var baseXClientFactory = mock(BaseXClientFactory.class);
        var sclDataModelMarshaller = mock(SclDataModelMarshaller.class);

        when(baseXClientFactory.createClient()).thenReturn(mock(BaseXClient.class));

        assertNotNull(new CompasSclDataServiceBaseXConfiguration()
                .creatCompasSclDataRepositoryDevelopment(baseXClientFactory, sclDataModelMarshaller));
    }
}