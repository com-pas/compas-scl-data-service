// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.data.websocket.v1.decoder.GetWsResponseDecoder;
import org.lfenergy.compas.scl.data.websocket.v1.encoder.GetWsRequestEncoder;
import org.lfenergy.compas.scl.data.websocket.v1.model.GetWsRequest;
import org.lfenergy.compas.scl.data.websocket.v1.model.GetWsResponse;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import java.net.URI;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
class CompasSclGetServerEndpointAsEditorTest extends AbstractServerEndpointAsEditorTestSupport {
    @InjectMock
    private CompasSclDataService service;

    @TestHTTPResource("/scl-ws/v1/SCD/get")
    private URI uri;

    @Test
    void getSCL_WhenCalled_ThenExpectedResponseIsRetrieved() throws Exception {
        var encoder = new GetWsRequestEncoder();
        var sclFileTye = SclFileType.SCD;
        var id = UUID.randomUUID();
        var sclData = readSCL();

        var request = new GetWsRequest();
        request.setId(id);

        when(service.findByUUID(sclFileTye, id)).thenReturn(sclData);

        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
            session.getAsyncRemote().sendText(encoder.encode(request));

            assertSclData(sclData);
            verify(service).findByUUID(sclFileTye, id);
        }
    }

    @Test
    void getSCL_WhenCalledWithInvalidText_ThenExceptionThrown() throws Exception {
        testWhenCalledWithInvalidTextThenExceptionThrown(uri);
    }

    @ClientEndpoint(decoders = GetWsResponseDecoder.class)
    static class Client {
        @OnMessage
        public void onMessage(GetWsResponse response) {
            sclDataQueue.add(response.getSclData());
        }
    }
}
