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
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

import static org.lfenergy.compas.scl.data.basex.client.BaseXServerUtil.createClientFactory;

@ExtendWith({MockitoExtension.class, BaseXServerJUnitExtension.class})
class CompasSclDataBaseXRepositoryTest extends AbstractCompasSclDataRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasSclDataBaseXRepositoryTest.class);

    private static BaseXClientFactory factory;
    private CompasSclDataBaseXRepository repository;

    @Override
    protected CompasSclDataRepository getRepository() {
        return repository;
    }

    @BeforeAll
    static void beforeAll() {
        factory = createClientFactory(BaseXServerJUnitExtension.getPortNumber());

        // To make it possible to re-run the test over and over (in your IDE),
        // Create all the database, because this will cause to old ones to be removed.
        Arrays.stream(SclFileType.values())
                .forEach(type -> {
                    try {
                        factory.createClient().executeXQuery("db:create('" + type + "')");
                    } catch (IOException exp) {
                        LOGGER.warn("Error re-creating database {}", type, exp);
                    }
                });
    }

    @BeforeEach
    void beforeEach() {
        repository = new CompasSclDataBaseXRepository(factory, new SclDataModelMarshaller());
    }
}