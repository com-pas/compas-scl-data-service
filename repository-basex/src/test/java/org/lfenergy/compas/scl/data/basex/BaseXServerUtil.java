// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.basex;

import org.basex.BaseXServer;

import java.io.IOException;
import java.net.ServerSocket;

public final class BaseXServerUtil {
    private BaseXServerUtil() {
    }

    public static int getFreePortNumber() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            if (serverSocket.getLocalPort() > 0) {
                return serverSocket.getLocalPort();
            }
        }
        throw new IOException("Port is not available");
    }

    public static BaseXServer createServer(int portNumber) throws IOException {
        System.setProperty("org.basex.path", "build/basex");
        return new BaseXServer("-p" + portNumber);
    }

    public static BaseXClientFactory createClientFactory(int portNumber) {
        return new BaseXClientFactory("localhost", portNumber, "admin", "admin");
    }
}
