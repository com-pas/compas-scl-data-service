// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1.decoder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.exception.CompasException;
import org.lfenergy.compas.scl.data.model.Version;

import jakarta.xml.bind.UnmarshalException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.core.commons.exception.CompasErrorCode.WEBSOCKET_DECODER_ERROR_CODE;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

class GetVersionWsRequestDecoderTest {
    private GetVersionWsRequestDecoder decoder;

    @BeforeEach
    void init() {
        decoder = new GetVersionWsRequestDecoder();
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
        var version = new Version("1.2.3");
        var message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<sds:GetVersionWsRequest xmlns:sds=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\">"
                + "<sds:Id>" + id + "</sds:Id>"
                + "<sds:Version>" + version + "</sds:Version>"
                + "</sds:GetVersionWsRequest>";

        var result = decoder.decode(message);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(version.toString(), result.getVersion());
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
