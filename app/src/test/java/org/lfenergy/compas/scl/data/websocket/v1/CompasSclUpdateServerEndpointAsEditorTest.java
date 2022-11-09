// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.data.websocket.v1.decoder.UpdateResponseDecoder;
import org.lfenergy.compas.scl.data.websocket.v1.encoder.UpdateRequestEncoder;
import org.lfenergy.compas.scl.data.websocket.v1.model.UpdateRequest;
import org.lfenergy.compas.scl.data.websocket.v1.model.UpdateResponse;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lfenergy.compas.scl.data.rest.Constants.UPDATE_ROLE;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestSecurity(user = "test-editor", roles = {"SCD_" + UPDATE_ROLE})
@JwtSecurity(claims = {
        // Default the claim "name" is configured for Who, so we will set this claim for the test.
        @Claim(key = "name", value = CompasSclUpdateServerEndpointAsEditorTest.USERNAME)
})
class CompasSclUpdateServerEndpointAsEditorTest {
    private static final LinkedBlockingDeque<String> sclDataQueue = new LinkedBlockingDeque<>();

    public static final String USERNAME = "Test Editor";
    private static final UUID ID_VALUE = UUID.fromString("9bb30944-8c85-4509-97ec-365381c355ef");

    @InjectMock
    private CompasSclDataService service;

    @TestHTTPResource("/scl-ws/v1/SCD/update")
    private URI uri;

    @Test
    void updateSCL_WhenCalled_ThenExpectedResponseIsRetrieved() throws Exception {
        var encoder = new UpdateRequestEncoder();
        var sclFileTye = SclFileType.SCD;
        var cst = ChangeSetType.PATCH;
        var comment = "Some comment";
        var sclData = readSCL();

        var request = new UpdateRequest();
        request.setId(ID_VALUE);
        request.setChangeSetType(cst);
        request.setComment(comment);
        request.setSclData(sclData);

        when(service.update(sclFileTye, ID_VALUE, cst, USERNAME, comment, sclData))
                .thenReturn(sclData);

        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
            session.getAsyncRemote().sendText(encoder.encode(request));

            var updatedSclData = sclDataQueue.poll(10, TimeUnit.SECONDS);
            assertEquals(sclData, updatedSclData);
            assertEquals(0, sclDataQueue.size());
            verify(service, times(1)).update(sclFileTye, ID_VALUE, cst, USERNAME, comment, sclData);
        }
    }

    @ClientEndpoint(decoders = UpdateResponseDecoder.class)
    static class Client {
        @OnMessage
        public void onMessage(UpdateResponse response) {
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
