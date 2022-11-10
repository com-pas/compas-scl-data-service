// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import org.lfenergy.compas.core.commons.model.ErrorMessage;
import org.lfenergy.compas.core.commons.model.ErrorResponse;
import org.lfenergy.compas.core.websocket.ErrorResponseDecoder;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lfenergy.compas.core.commons.exception.CompasErrorCode.WEBSOCKET_DECODER_ERROR_CODE;

public class AbstractServerEndpointTest {
    protected static final LinkedBlockingDeque<String> sclDataQueue = new LinkedBlockingDeque<>();
    protected static final LinkedBlockingDeque<ErrorMessage> errorQueue = new LinkedBlockingDeque<>();

    void testWhenCalledWithInvalidTextThenExceptionThrown(URI uri) throws Exception {
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(ErrorClient.class, uri)) {
            session.getAsyncRemote().sendText("Invalid Message");

            assertErrorResponse(WEBSOCKET_DECODER_ERROR_CODE);
        }
    }

    protected void assertErrorResponse(String expectedErrorCode) throws InterruptedException {
        var errorMessage = errorQueue.poll(10, TimeUnit.SECONDS);
        assertEquals(expectedErrorCode, errorMessage.getCode());
        assertEquals(0, errorQueue.size());
    }

    protected void assertSclData(String expectedSclData) throws InterruptedException {
        var returnedSclData = sclDataQueue.poll(10, TimeUnit.SECONDS);
        assertEquals(expectedSclData, returnedSclData);
        assertEquals(0, sclDataQueue.size());
    }

    protected String readSCL() throws IOException {
        try (var inputStream = getClass().getResourceAsStream("/scl/icd_import_ied_test.scd")) {
            assert inputStream != null;

            return new String(inputStream.readAllBytes());
        }
    }

    @ClientEndpoint(decoders = ErrorResponseDecoder.class)
    static class ErrorClient {
        @OnMessage
        public void onMessage(ErrorResponse response) {
            if (response.getErrorMessages().size() > 0) {
                errorQueue.addAll(response.getErrorMessages());
            }
        }
    }
}
