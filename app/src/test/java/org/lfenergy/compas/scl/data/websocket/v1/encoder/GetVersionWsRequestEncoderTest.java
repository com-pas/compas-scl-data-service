// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1.encoder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.websocket.v1.model.GetVersionWsRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

class GetVersionWsRequestEncoderTest {
    private GetVersionWsRequestEncoder encoder;

    @BeforeEach
    void init() {
        encoder = new GetVersionWsRequestEncoder();
        encoder.init(null);
    }

    @Test
    void encode_WhenCalledWithRequest_ThenRequestConvertedToString() {
        var id = UUID.randomUUID();
        var version = new Version("1.2.3");

        var request = new GetVersionWsRequest();
        request.setId(id);
        request.setVersion(version.toString());

        var result = encoder.encode(request);

        var expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<sds:GetVersionWsRequest xmlns:sds=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\">" +
                "<sds:Id>" + id + "</sds:Id>" +
                "<sds:Version>" + version + "</sds:Version>" +
                "</sds:GetVersionWsRequest>";
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @AfterEach
    void destroy() {
        encoder.destroy();
    }
}
