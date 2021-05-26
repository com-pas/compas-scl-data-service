// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.model.Item;

import javax.xml.bind.JAXBException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JaxbMarshallerTest {
    @Test
    void unmarshal_WhenItemXMLPassed_ThenItemObjectReturned() throws JAXBException {
        var id = "ID";
        var version = "VERSION";
        var xml = "<Item><Id>" + id + "</Id><Version>" + version + "</Version></Item>";

        JaxbMarshaller marshaller = new JaxbMarshaller();
        Item item = marshaller.unmarshal(xml);

        assertNotNull(item);
        assertEquals(id, item.getId());
        assertEquals(version, item.getVersion());
    }
}