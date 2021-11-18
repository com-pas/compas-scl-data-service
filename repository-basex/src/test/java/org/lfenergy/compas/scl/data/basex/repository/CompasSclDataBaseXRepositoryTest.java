// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.basex.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.basex.client.BaseXClientFactory;
import org.lfenergy.compas.scl.data.basex.client.BaseXServerJUnitExtension;
import org.lfenergy.compas.scl.data.repository.AbstractCompasSclDataRepository;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.util.SclDataModelMarshaller;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.lfenergy.compas.scl.data.basex.client.BaseXServerUtil.createClientFactory;

@ExtendWith({MockitoExtension.class, BaseXServerJUnitExtension.class})
class CompasSclDataBaseXRepositoryTest extends AbstractCompasSclDataRepository {
    private static BaseXClientFactory factory;
    private CompasSclDataBaseXRepository repository;

    @Override
    protected CompasSclDataRepository getRepository() {
        return repository;
    }

    @BeforeAll
    static void beforeAll() {
        factory = createClientFactory(BaseXServerJUnitExtension.getPortNumber());
    }

    @BeforeEach
    void beforeEach() throws Exception {
        repository = new CompasSclDataBaseXRepository(factory, new SclDataModelMarshaller());
    }
}