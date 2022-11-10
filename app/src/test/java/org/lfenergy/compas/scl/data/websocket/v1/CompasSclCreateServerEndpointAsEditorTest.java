// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.data.websocket.v1.decoder.CreateWsResponseDecoder;
import org.lfenergy.compas.scl.data.websocket.v1.encoder.CreateWsRequestEncoder;
import org.lfenergy.compas.scl.data.websocket.v1.model.CreateWsRequest;
import org.lfenergy.compas.scl.data.websocket.v1.model.CreateWsResponse;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import java.net.URI;

import static org.mockito.Mockito.*;

@QuarkusTest
class CompasSclCreateServerEndpointAsEditorTest extends AbstractServerEndpointAsEditorTest {
    @InjectMock
    private CompasSclDataService service;

    @TestHTTPResource("/scl-ws/v1/SCD/create")
    private URI uri;

    @Test
    void createSCL_WhenCalled_ThenExpectedResponseIsRetrieved() throws Exception {
        var encoder = new CreateWsRequestEncoder();
        var sclFileTye = SclFileType.SCD;
        var name = "Some name";
        var comment = "Some comment";
        var sclData = readSCL();

        var request = new CreateWsRequest();
        request.setName(name);
        request.setComment(comment);
        request.setSclData(sclData);

        when(service.create(sclFileTye, name, USERNAME, comment, sclData))
                .thenReturn(sclData);

        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
            session.getAsyncRemote().sendText(encoder.encode(request));

            assertSclData(sclData);
            verify(service, times(1)).create(sclFileTye, name, USERNAME, comment, sclData);
        }
    }

    @Test
    void createSCL_WhenCalledWithInvalidText_ThenExceptionThrown() throws Exception {
        testWhenCalledWithInvalidTextThenExceptionThrown(uri);
    }

    @ClientEndpoint(decoders = CreateWsResponseDecoder.class)
    static class Client {
        @OnMessage
        public void onMessage(CreateWsResponse response) {
            sclDataQueue.add(response.getSclData());
        }
    }
}
