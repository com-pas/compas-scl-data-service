// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CompasSclDataServiceCommonConfigurationTest {
    @Test
    void createElementConverter_WhenCalled_ThenObjectReturned() {
        assertNotNull(new CompasSclDataServiceCommonConfiguration().createElementConverter());
    }

    @Test
    void creatSclElementProcessor_WhenCalled_ThenObjectReturned() {
        assertNotNull(new CompasSclDataServiceCommonConfiguration().creatSclElementProcessor());
    }

    @Test
    void createSclDataModelMarshaller_WhenCalled_ThenObjectReturned() {
        assertNotNull(new CompasSclDataServiceCommonConfiguration().createSclDataModelMarshaller());
    }
}