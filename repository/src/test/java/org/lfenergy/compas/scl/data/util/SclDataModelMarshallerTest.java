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
    void unmarshal_WhenItemXMLPassed_ThenItemObjectReturned() {
        var id = "ID";
        var name = "NAME";
        var version = "VERSION";
        var xml = "<Item xmlns=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\"><Id>" + id + "</Id><Name>" + name + "</Name><Version>" + version + "</Version></Item>";

        var marshaller = new SclDataModelMarshaller();
        var item = marshaller.unmarshal(xml);

        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(version, item.getVersion());
    }

    @Test
    void unmarshal_WhenInvalidXMLPassed_ThenExceptionThrown() {
        var xml = "<Item xmlns=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\"></Item Invalid>";

        var marshaller = new SclDataModelMarshaller();
        var exception = assertThrows(CompasSclDataServiceException.class, () -> {
            marshaller.unmarshal(xml);
        });
        assertEquals(UNMARSHAL_ERROR_CODE, exception.getErrorCode());
    }
}