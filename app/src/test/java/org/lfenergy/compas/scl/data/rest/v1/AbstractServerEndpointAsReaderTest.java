// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakeException;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import java.io.InterruptedIOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.lfenergy.compas.scl.data.rest.Constants.READ_ROLE;

@TestSecurity(user = "test-editor", roles = {"SCD_" + READ_ROLE})
@JwtSecurity(claims = {
        // Default the claim "name" is configured for Who, so we will set this claim for the test.
        @Claim(key = "name", value = CompasSclDataResourceAsEditorTest.USERNAME)
})
public abstract class AbstractServerEndpointAsReaderTest {
    protected void testWhenForbiddenToExecute(URI uri) {
        var container = ContainerProvider.getWebSocketContainer();
        var exception = assertThrows(InterruptedIOException.class, () -> container.connectToServer(Client.class, uri));

        assertEquals(exception.getSuppressed().length, 1);
        assertEquals(exception.getSuppressed()[0].getClass(), ExecutionException.class);
        assertEquals((exception.getSuppressed()[0]).getCause().getClass(), WebSocketClientHandshakeException.class);
        var wschExp = (WebSocketClientHandshakeException) (exception.getSuppressed()[0]).getCause();
        assertEquals(wschExp.response().status(), HttpResponseStatus.FORBIDDEN);
    }

    @ClientEndpoint()
    private static class Client {
    }
}
