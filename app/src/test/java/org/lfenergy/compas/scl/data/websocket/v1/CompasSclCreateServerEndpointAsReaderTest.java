// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.net.URI;

@QuarkusTest
class CompasSclCreateServerEndpointAsReaderTest extends AbstractServerEndpointAsReaderTestSupport {
    @TestHTTPResource("/scl-ws/v1/SCD/create")
    private URI uri;

    @Test
    void createSCL_WhenCalled_ThenExpectedResponseIsRetrieved() {
        testWhenForbiddenToExecute(uri);
    }
}
