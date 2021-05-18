// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.basex;

import org.basex.BaseXServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.data.basex.TcpPortUtil.getFreePortNumber;

class BaseXClientFactoryTest {
    private BaseXServer server;
    private BaseXClientFactory factory;

    @BeforeEach
    void beforeAll() throws IOException {
        int freePortNumber = getFreePortNumber();
        server = new BaseXServer("-p" + freePortNumber);
        factory = new BaseXClientFactory("localhost", freePortNumber, "admin", "admin");
    }

    @Test
    void createClient_WhenCalled_ThenReturnClient() throws IOException {
        assertNotNull(factory.createClient());
    }

    @AfterEach
    void afterAll() {
        server.stop();
    }
}