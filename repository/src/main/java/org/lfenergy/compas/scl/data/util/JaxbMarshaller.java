// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.lfenergy.compas.scl.data.model.Item;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

public class JaxbMarshaller {
    private JAXBContext jaxbContext;
    private Unmarshaller jaxbUnmarshaller;

    public JaxbMarshaller() throws JAXBException {
        jaxbContext = JAXBContext.newInstance("org.lfenergy.compas.scl.data.model");
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    }

    public Item unmarshal(String xml) throws JAXBException {
        var source = new StreamSource(new StringReader(xml));
        return jaxbUnmarshaller.unmarshal(source, Item.class).getValue();
    }
}
