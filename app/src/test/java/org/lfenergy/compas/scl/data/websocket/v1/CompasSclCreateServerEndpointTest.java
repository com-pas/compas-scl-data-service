// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class CompasSclCreateServerEndpointTest extends AbstractServerEndpointOnOpenTestSupport {
    @Mock
    private JsonWebToken jsonWebToken;

    @Test
    void onOpen_WhenSessionCloseThrowsIOException_ThenExceptionIsHandledGracefully() throws IOException {
        var endpoint = new CompasSclCreateServerEndpoint(null, jsonWebToken, null);
        testOnOpenWhenSessionCloseThrowsIOException(jsonWebToken, endpoint::onOpen);
    }
}
