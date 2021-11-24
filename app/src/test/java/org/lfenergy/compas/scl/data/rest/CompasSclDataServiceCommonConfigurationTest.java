// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.util.SclElementProcessor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

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

    @Test
    void createCompasSclDataService_WhenCalled_ThenObjectReturned() {
        var repository = mock(CompasSclDataRepository.class);
        var converter = mock(ElementConverter.class);
        var sclElementProcessor = mock(SclElementProcessor.class);

        assertNotNull(new CompasSclDataServiceCommonConfiguration()
                .createCompasSclDataService(repository, converter, sclElementProcessor));
    }
}