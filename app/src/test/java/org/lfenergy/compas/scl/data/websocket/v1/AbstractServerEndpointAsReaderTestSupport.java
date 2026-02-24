// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.test.security.TestSecurity;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnClose;
import jakarta.websocket.Session;
import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.data.rest.Constants.READ_ROLE;

@TestSecurity(user = "test-editor", roles = {"SCD_" + READ_ROLE})
public abstract class AbstractServerEndpointAsReaderTestSupport extends AbstractServerEndpointTestSupport {
    private static final LinkedBlockingDeque<CloseReason> closeReasonQueue = new LinkedBlockingDeque<>();

    protected void testWhenForbiddenToExecute(URI uri) throws Exception {
        closeReasonQueue.clear();
        var container = ContainerProvider.getWebSocketContainer();
        try (var session = container.connectToServer(Client.class, uri)) {
            var closeReason = closeReasonQueue.poll(10, TimeUnit.SECONDS);
            assertNotNull(closeReason, "Expected session to be closed by server due to forbidden access");
            assertEquals(CloseReason.CloseCodes.VIOLATED_POLICY, closeReason.getCloseCode());
        }
    }

    @ClientEndpoint()
    static class Client {
        @OnClose
        public void onClose(Session session, CloseReason closeReason) {
            closeReasonQueue.add(closeReason);
        }
    }
}
