// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.basex.client;

import java.io.IOException;

public class BaseXClientFactory {
    private final String baseXHost;
    private final Integer baseXPort;
    private final String baseXUsername;
    private final String baseXPassword;

    public BaseXClientFactory(String baseXHost,
                              Integer baseXPort,
                              String baseXUsername,
                              String baseXPassword) {
        this.baseXHost = baseXHost;
        this.baseXPort = baseXPort;
        this.baseXUsername = baseXUsername;
        this.baseXPassword = baseXPassword;
    }

    public BaseXClient createClient() throws IOException {
        return new BaseXClient(baseXHost, baseXPort, baseXUsername, baseXPassword);
    }
}
