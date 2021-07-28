// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CompasSclDataServiceConfigurationTest {
    private CompasSclDataServiceConfiguration configuration = new CompasSclDataServiceConfiguration();

    @Test
    void createElementConverter_WhenCalled_ThenObjectReturned() {
        assertNotNull(configuration.createElementConverter());
    }

    @Test
    void creatSclElementProcessor_WhenCalled_ThenObjectReturned() {
        assertNotNull(configuration.creatSclElementProcessor());
    }

    @Test
    void createSclDataModelMarshaller_WhenCalled_ThenObjectReturned() {
        assertNotNull(configuration.createSclDataModelMarshaller());
    }
}