// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.basex.client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.data.basex.client.BaseXServerUtil.createClientFactory;

@ExtendWith({BaseXServerJUnitExtension.class})
class BaseXClientFactoryTest {
    private static BaseXClientFactory factory;

    @BeforeAll
    static void beforeAll() {
        factory = createClientFactory(BaseXServerJUnitExtension.getPortNumber());
    }

    @Test
    void createClient_WhenCalled_ThenReturnClient() throws IOException {
        assertNotNull(factory.createClient());
    }
}