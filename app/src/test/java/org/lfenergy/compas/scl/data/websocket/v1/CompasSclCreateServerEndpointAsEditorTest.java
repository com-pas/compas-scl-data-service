// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.data.websocket.v1.decoder.CreateResponseDecoder;
import org.lfenergy.compas.scl.data.websocket.v1.encoder.CreateRequestEncoder;
import org.lfenergy.compas.scl.data.websocket.v1.model.CreateRequest;
import org.lfenergy.compas.scl.data.websocket.v1.model.CreateResponse;
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
        var encoder = new CreateRequestEncoder();
        var sclFileTye = SclFileType.SCD;
        var name = "Some name";
        var comment = "Some comment";
        var sclData = readSCL();

        var request = new CreateRequest();
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

    @ClientEndpoint(decoders = CreateResponseDecoder.class)
    static class Client {
        @OnMessage
        public void onMessage(CreateResponse response) {
            sclDataQueue.add(response.getSclData());
        }
    }
}
