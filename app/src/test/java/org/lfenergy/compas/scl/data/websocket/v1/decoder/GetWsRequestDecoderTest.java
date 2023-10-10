// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1.decoder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.exception.CompasException;

import jakarta.xml.bind.UnmarshalException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.core.commons.exception.CompasErrorCode.WEBSOCKET_DECODER_ERROR_CODE;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

class GetWsRequestDecoderTest {
    private GetWsRequestDecoder decoder;

    @BeforeEach
    void init() {
        decoder = new GetWsRequestDecoder();
        decoder.init(null);
    }

    @Test
    void willDecode_WhenCalledWithString_ThenTrueReturned() {
        assertTrue(decoder.willDecode(""));
        assertTrue(decoder.willDecode("Some text"));
    }

    @Test
    void willDecode_WhenCalledWithNull_ThenFalseReturned() {
        assertFalse(decoder.willDecode(null));
    }

    @Test
    void decode_WhenCalledWithCorrectRequestXML_ThenStringConvertedToObject() {
        var id = UUID.randomUUID();
        var message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<sds:GetWsRequest xmlns:sds=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\">"
                + "<sds:Id>" + id + "</sds:Id>"
                + "</sds:GetWsRequest>";

        var result = decoder.decode(message);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void decode_WhenCalledWithWrongXMLType_ThenExceptionThrown() {
        var message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<sds:InvalidRequest xmlns:sds=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\">"
                + "</sds:InvalidRequest>";

        var exception = assertThrows(CompasException.class, () -> decoder.decode(message));
        assertEquals(WEBSOCKET_DECODER_ERROR_CODE, exception.getErrorCode());
        assertEquals(UnmarshalException.class, exception.getCause().getClass());
    }

    @AfterEach
    void destroy() {
        decoder.destroy();
    }
}
