// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.basex.client;

import org.basex.BaseXServer;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.lfenergy.compas.scl.data.basex.client.BaseXServerUtil.createServer;
import static org.lfenergy.compas.scl.data.basex.client.BaseXServerUtil.getFreePortNumber;

/**
 * JUnit extension to start a BaseX Server. This server should only be started and stopped once for all
 * JUnit Tests.
 */
public class BaseXServerJUnitExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    private static BaseXServer server;
    private static int portNumber;

    // Gate keeper to prevent multiple Threads within the same routine
    private final static Lock lock = new ReentrantLock();

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        // lock the access so only one Thread has access to it
        lock.lock();
        if (server == null) {
            portNumber = getFreePortNumber();
            server = createServer(portNumber);

            // The following line registers a callback hook when the root test context is shut down
            context.getRoot().getStore(GLOBAL).put("BaseXServerJUnitExtension", this);
        }
        // free the access
        lock.unlock();
    }

    @Override
    public void close() {
        server.stop();
    }

    public static int getPortNumber() {
        return portNumber;
    }
}