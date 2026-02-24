// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.data.websocket.v1.decoder.GetWsResponseDecoder;
import org.lfenergy.compas.scl.data.websocket.v1.encoder.GetVersionWsRequestEncoder;
import org.lfenergy.compas.scl.data.websocket.v1.model.GetVersionWsRequest;
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
class CompasSclGetVersionServerEndpointAsEditorTest extends AbstractServerEndpointAsEditorTestSupport {
    @InjectMock
    private CompasSclDataService service;

    @TestHTTPResource("/scl-ws/v1/SCD/get-version")
    private URI uri;

    @Test
    void getVersionSCL_WhenCalled_ThenExpectedResponseIsRetrieved() throws Exception {
        var encoder = new GetVersionWsRequestEncoder();
        var sclFileTye = SclFileType.SCD;
        var id = UUID.randomUUID();
        var version = new Version("1.2.3");
        var sclData = readSCL();

        var request = new GetVersionWsRequest();
        request.setId(id);
        request.setVersion(version.toString());

        when(service.findByUUID(sclFileTye, id, version)).thenReturn(sclData);

        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
            session.getAsyncRemote().sendText(encoder.encode(request));

            assertSclData(sclData);
            verify(service).findByUUID(sclFileTye, id, version);
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
