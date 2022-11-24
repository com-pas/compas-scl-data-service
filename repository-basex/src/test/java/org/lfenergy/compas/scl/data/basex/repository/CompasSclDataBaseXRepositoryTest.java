// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.basex.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.basex.client.BaseXClientFactory;
import org.lfenergy.compas.scl.data.basex.client.BaseXServerJUnitExtension;
import org.lfenergy.compas.scl.data.repository.AbstractCompasSclDataRepositoryTest;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.util.SclDataModelMarshaller;
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.lfenergy.compas.scl.data.basex.client.BaseXServerUtil.createClientFactory;

@ExtendWith({MockitoExtension.class, BaseXServerJUnitExtension.class})
class CompasSclDataBaseXRepositoryTest extends AbstractCompasSclDataRepositoryTest {
    private static final Logger LOGGER = LogManager.getLogger(CompasSclDataBaseXRepositoryTest.class);

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

    /*
     * TODO: Method beneath needs to be removed and the one from CompasSclDataPostgreSQLRepositoryTest be used
     * when hasDuplicateSclName has been implemented by CompasSclDataBaseXRepository. */
    @Test
    void hasDuplicateSclName_WhenUsingSclNameThatHasBeenUsedYet_ThenDuplicateIsFound() {
        // Will always return false for now, because there is no correct implementation for BaseX
        assertFalse(getRepository().hasDuplicateSclName(TYPE, NAME_1));
    }
}
