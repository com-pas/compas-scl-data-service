// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1.encoder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.websocket.v1.model.UpdateWsRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

class UpdateWsRequestEncoderTest {
    private UpdateWsRequestEncoder encoder;

    @BeforeEach
    void init() {
        encoder = new UpdateWsRequestEncoder();
        encoder.init(null);
    }

    @Test
    void encode_WhenCalledWithRequest_ThenRequestConvertedToString() {
        var sclData = "Some SCL Data";
        var id = UUID.randomUUID();
        var cst = ChangeSetType.MAJOR;
        var comment = "Some comment";

        var request = new UpdateWsRequest();
        request.setId(id);
        request.setChangeSetType(cst);
        request.setComment(comment);
        request.setSclData(sclData);

        var result = encoder.encode(request);

        var expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<sds:UpdateWsRequest xmlns:sds=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\">" +
                "<sds:ChangeSet>" + cst + "</sds:ChangeSet>" +
                "<sds:Comment>" + comment + "</sds:Comment>" +
                "<sds:SclData>" + sclData + "</sds:SclData>" +
                "<sds:Id>" + id + "</sds:Id>" +
                "</sds:UpdateWsRequest>";
        assertNotNull(result);
        assertEquals(expectedResult, result);
    }

    @AfterEach
    void destroy() {
        encoder.destroy();
    }
}
