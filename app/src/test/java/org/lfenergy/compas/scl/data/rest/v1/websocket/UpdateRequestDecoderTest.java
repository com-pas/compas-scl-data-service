// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1.websocket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.exception.CompasException;
import org.lfenergy.compas.scl.data.model.ChangeSetType;

import javax.xml.bind.UnmarshalException;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.core.commons.exception.CompasErrorCode.WEBSOCKET_DECODER_ERROR_CODE;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

class UpdateRequestDecoderTest {
    private UpdateRequestDecoder decoder;

    @BeforeEach
    void init() {
        decoder = new UpdateRequestDecoder();
        decoder.init(null);
    }

    @Test
    void decode_WhenCalledWithCorrectRequestXML_ThenStringConvertedToObject() {
        var sclData = "Some SCL Data";
        var cst = ChangeSetType.MAJOR;
        var comment = "Some comment";
        var message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<sds:UpdateRequest xmlns:sds=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\">"
                + "<sds:ChangeSet>" + cst + "</sds:ChangeSet>"
                + "<sds:Comment>" + comment + "</sds:Comment>"
                + "<sds:SclData><![CDATA[" + sclData + "]]></sds:SclData>"
                + "</sds:UpdateRequest>";

        var result = decoder.decode(message);

        assertNotNull(result);
        assertEquals(cst, result.getChangeSetType());
        assertEquals(comment, result.getComment());
        assertEquals(sclData, result.getSclData());
    }

    @Test
    void decode_WhenCalledWithWrongXMLType_ThenExceptionThrown() {
        var message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<sds:UpdateResponse xmlns:sds=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\">"
                + "</sds:UpdateResponse>";

        var exception = assertThrows(CompasException.class, () -> decoder.decode(message));
        assertEquals(WEBSOCKET_DECODER_ERROR_CODE, exception.getErrorCode());
        assertEquals(UnmarshalException.class, exception.getCause().getClass());
    }

    @AfterEach
    void destroy() {
        decoder.destroy();
    }
}
