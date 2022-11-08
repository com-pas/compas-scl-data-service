// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.net.URI;

@QuarkusTest
class CompasSclUpdateServerEndpointAsReaderTest extends AbstractServerEndpointAsReaderTest {
    private static final String ID_VALUE = "9bb30944-8c85-4509-97ec-365381c355ef";

    @TestHTTPResource("/scl-ws/v1/SCD/update/" + ID_VALUE)
    private URI uri;

    @Test
    void createSCL_WhenCalled_ThenExpectedResponseIsRetrieved() {
        testWhenForbiddenToExecute(uri);
    }
}
