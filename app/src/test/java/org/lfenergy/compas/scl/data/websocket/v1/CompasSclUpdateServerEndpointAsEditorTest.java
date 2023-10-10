// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.data.websocket.v1.decoder.UpdateWsResponseDecoder;
import org.lfenergy.compas.scl.data.websocket.v1.encoder.UpdateWsRequestEncoder;
import org.lfenergy.compas.scl.data.websocket.v1.model.UpdateWsRequest;
import org.lfenergy.compas.scl.data.websocket.v1.model.UpdateWsResponse;
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
class CompasSclUpdateServerEndpointAsEditorTest extends AbstractServerEndpointAsEditorTestSupport {
    @InjectMock
    private CompasSclDataService service;

    @TestHTTPResource("/scl-ws/v1/SCD/update")
    private URI uri;

    @Test
    void updateSCL_WhenCalled_ThenExpectedResponseIsRetrieved() throws Exception {
        var encoder = new UpdateWsRequestEncoder();
        var sclFileTye = SclFileType.SCD;
        var id = UUID.randomUUID();
        var cst = ChangeSetType.PATCH;
        var comment = "Some comment";
        var sclData = readSCL();

        var request = new UpdateWsRequest();
        request.setId(id);
        request.setChangeSetType(cst);
        request.setComment(comment);
        request.setSclData(sclData);

        when(service.update(sclFileTye, id, cst, USERNAME, comment, sclData))
                .thenReturn(sclData);

        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
            session.getAsyncRemote().sendText(encoder.encode(request));

            assertSclData(sclData);
            verify(service).update(sclFileTye, id, cst, USERNAME, comment, sclData);
        }
    }

    @Test
    void createSCL_WhenCalledWithInvalidText_ThenExceptionThrown() throws Exception {
        testWhenCalledWithInvalidTextThenExceptionThrown(uri);
    }

    @ClientEndpoint(decoders = UpdateWsResponseDecoder.class)
    static class Client {
        @OnMessage
        public void onMessage(UpdateWsResponse response) {
            sclDataQueue.add(response.getSclData());
        }
    }
}
