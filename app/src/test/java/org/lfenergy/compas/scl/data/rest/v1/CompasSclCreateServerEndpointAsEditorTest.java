// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.rest.v1.model.CreateRequest;
import org.lfenergy.compas.scl.data.rest.v1.model.CreateResponse;
import org.lfenergy.compas.scl.data.rest.v1.websocket.CreateRequestEncoder;
import org.lfenergy.compas.scl.data.rest.v1.websocket.CreateResponseDecoder;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lfenergy.compas.scl.data.rest.Constants.CREATE_ROLE;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestSecurity(user = "test-editor", roles = {"SCD_" + CREATE_ROLE})
@JwtSecurity(claims = {
        // Default the claim "name" is configured for Who, so we will set this claim for the test.
        @Claim(key = "name", value = CompasSclDataResourceAsEditorTest.USERNAME)
})
class CompasSclCreateServerEndpointAsEditorTest {
    private static final LinkedBlockingDeque<String> sclDataQueue = new LinkedBlockingDeque<>();

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

        when(service.create(sclFileTye, name, CompasSclDataResourceAsEditorTest.USERNAME, comment, sclData))
                .thenReturn(sclData);

        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
            session.getAsyncRemote().sendText(encoder.encode(request));

            var updatedSclData = sclDataQueue.poll(10, TimeUnit.SECONDS);
            assertEquals(sclData, updatedSclData);
            assertEquals(0, sclDataQueue.size());
            verify(service, times(1)).create(sclFileTye, name, CompasSclDataResourceAsEditorTest.USERNAME, comment, sclData);
        }
    }

    @ClientEndpoint(decoders = CreateResponseDecoder.class)
    private static class Client {
        @OnMessage
        public void onMessage(CreateResponse response) {
            sclDataQueue.add(response.getSclData());
        }
    }

    private String readSCL() throws IOException {
        try (var inputStream = getClass().getResourceAsStream("/scl/icd_import_ied_test.scd")) {
            assert inputStream != null;

            return new String(inputStream.readAllBytes());
        }
    }
}
