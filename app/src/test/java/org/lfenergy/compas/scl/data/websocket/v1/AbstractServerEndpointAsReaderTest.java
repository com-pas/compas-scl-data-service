// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakeException;
import io.quarkus.test.security.TestSecurity;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import java.io.InterruptedIOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.compas.scl.data.rest.Constants.READ_ROLE;

@TestSecurity(user = "test-editor", roles = {"SCD_" + READ_ROLE})
public abstract class AbstractServerEndpointAsReaderTest {
    protected void testWhenForbiddenToExecute(URI uri) {
        var container = ContainerProvider.getWebSocketContainer();
        var exception = assertThrows(InterruptedIOException.class, () -> container.connectToServer(Client.class, uri));

        assertEquals(1, exception.getSuppressed().length);
        assertEquals(ExecutionException.class, exception.getSuppressed()[0].getClass());
        assertEquals(WebSocketClientHandshakeException.class, (exception.getSuppressed()[0]).getCause().getClass());
        var wschExp = (WebSocketClientHandshakeException) (exception.getSuppressed()[0]).getCause();
        assertEquals(HttpResponseStatus.FORBIDDEN, wschExp.response().status());
    }

    @ClientEndpoint()
    static class Client {
    }
}
