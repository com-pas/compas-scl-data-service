// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.repository.SclDataRepositoryException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

public class SclDataModelMarshaller {
    private final Unmarshaller jaxbUnmarshaller;

    public SclDataModelMarshaller() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance("org.lfenergy.compas.scl.data.model");
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException exp) {
            throw new SclDataRepositoryException(exp);
        }
    }

    public Item unmarshal(String xml) {
        try {
            var source = new StreamSource(new StringReader(xml));
            return jaxbUnmarshaller.unmarshal(source, Item.class).getValue();
        } catch (JAXBException exp) {
            throw new SclDataRepositoryException(exp);
        }
    }
}
