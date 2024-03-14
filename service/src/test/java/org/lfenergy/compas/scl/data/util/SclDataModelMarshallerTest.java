// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.UNMARSHAL_ERROR_CODE;

class SclDataModelMarshallerTest {
    @Test
    void unmarshalItem_WhenItemXMLPassed_ThenItemObjectReturned() {
        var id = "ID";
        var name = "NAME";
        var version = "VERSION";
        var xml = "<Item xmlns=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\"><Id>" + id + "</Id><Name>" + name + "</Name><Version>" + version + "</Version></Item>";

        var marshaller = new SclDataModelMarshaller();
        var item = marshaller.unmarshalItem(xml);

        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(version, item.getVersion());
    }

    @Test
    void unmarshalItem_WhenInvalidXMLPassed_ThenExceptionThrown() {
        var xml = "<Item xmlns=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\"></Item Invalid>";

        var marshaller = new SclDataModelMarshaller();
        var exception = assertThrows(CompasSclDataServiceException.class, () -> {
            marshaller.unmarshalItem(xml);
        });
        assertEquals(UNMARSHAL_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void unmarshalSclMetaInfo_WhenItemXMLPassed_ThenItemObjectReturned() {
        var id = "ID";
        var name = "NAME";
        var version = "VERSION";
        var xml = "<SclMetaInfo xmlns=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\"><Id>" + id + "</Id><Name>" + name + "</Name><Version>" + version + "</Version></SclMetaInfo>";

        var marshaller = new SclDataModelMarshaller();
        var item = marshaller.unmarshalSclMetaInfo(xml);

        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(version, item.getVersion());
    }

    @Test
    void unmarshalSclMetaInfo_WhenInvalidXMLPassed_ThenExceptionThrown() {
        var xml = "<Item xmlns=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\"></Item Invalid>";

        var marshaller = new SclDataModelMarshaller();
        var exception = assertThrows(CompasSclDataServiceException.class, () -> {
            marshaller.unmarshalSclMetaInfo(xml);
        });
        assertEquals(UNMARSHAL_ERROR_CODE, exception.getErrorCode());
    }
}