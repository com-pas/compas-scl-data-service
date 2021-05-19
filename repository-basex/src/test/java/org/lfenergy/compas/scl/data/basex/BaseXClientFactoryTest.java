// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.basex;

import org.basex.BaseXServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.data.basex.BaseXServerUtil.createServer;
import static org.lfenergy.compas.scl.data.basex.BaseXServerUtil.getFreePortNumber;

class BaseXClientFactoryTest {
    private static BaseXServer server;
    private static BaseXClientFactory factory;

    @BeforeAll
    static void beforeAll() throws IOException {
        int portNumber = getFreePortNumber();
        server = createServer(portNumber);
        factory = new BaseXClientFactory("localhost", portNumber, "admin", "admin");
    }

    @Test
    void createClient_WhenCalled_ThenReturnClient() throws IOException {
        assertNotNull(factory.createClient());
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }
}