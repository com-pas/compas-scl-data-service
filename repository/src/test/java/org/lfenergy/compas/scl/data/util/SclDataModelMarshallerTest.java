// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.model.Item;

import javax.xml.bind.JAXBException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.lfenergy.compas.scl.data.Constants.SCL_DATA_SERVICE_NS_URI;

class SclDataModelMarshallerTest {
    @Test
    void unmarshal_WhenItemXMLPassed_ThenItemObjectReturned() throws JAXBException {
        var id = "ID";
        var name = "NAME";
        var version = "VERSION";
        var xml = "<Item xmlns=\"" + SCL_DATA_SERVICE_NS_URI + "\"><Id>" + id + "</Id><Name>" + name + "</Name><Version>" + version + "</Version></Item>";

        SclDataModelMarshaller marshaller = new SclDataModelMarshaller();
        Item item = marshaller.unmarshal(xml);

        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(version, item.getVersion());
    }
}