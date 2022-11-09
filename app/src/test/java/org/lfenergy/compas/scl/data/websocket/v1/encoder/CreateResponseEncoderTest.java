// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1.encoder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.websocket.v1.model.CreateResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

class CreateResponseEncoderTest {
    private CreateResponseEncoder encoder;

    @BeforeEach
    void init() {
        encoder = new CreateResponseEncoder();
        encoder.init(null);
    }

    @Test
    void encode_WhenCalledWithRequest_ThenRequestConvertedToString() {
        var sclData = "Some SCL Data";

        var request = new CreateResponse();
        request.setSclData(sclData);

        var result = encoder.encode(request);

        var expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<sds:CreateWsResponse xmlns:sds=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\">" +
                "<sds:SclData>" + sclData + "</sds:SclData>" +
                "</sds:CreateWsResponse>";
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @AfterEach
    void destroy() {
        encoder.destroy();
    }
}
