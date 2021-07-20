// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.basex;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;

@ApplicationScoped
public class BaseXClientFactory {
    private final String baseXHost;
    private final Integer baseXPort;
    private final String baseXUsername;
    private final String baseXPassword;

    @Inject
    public BaseXClientFactory(@ConfigProperty(name = "basex.host") String baseXHost,
                              @ConfigProperty(name = "basex.port") Integer baseXPort,
                              @ConfigProperty(name = "basex.username") String baseXUsername,
                              @ConfigProperty(name = "basex.password") String baseXPassword) {
        this.baseXHost = baseXHost;
        this.baseXPort = baseXPort;
        this.baseXUsername = baseXUsername;
        this.baseXPassword = baseXPassword;
    }

    public BaseXClient createClient() throws IOException {
        return new BaseXClient(baseXHost, baseXPort, baseXUsername, baseXPassword);
    }
}
