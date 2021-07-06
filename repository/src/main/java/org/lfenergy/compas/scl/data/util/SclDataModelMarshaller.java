// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.repository.SclDataRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

public class SclDataModelMarshaller {
    private static final Logger LOGGER = LoggerFactory.getLogger(SclDataModelMarshaller.class);

    private final Unmarshaller jaxbUnmarshaller;

    public SclDataModelMarshaller() {
        try {
            var jaxbContext = JAXBContext.newInstance("org.lfenergy.compas.scl.data.model");
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException exp) {
            LOGGER.error("Construction problem: {}", exp.getLocalizedMessage());
            throw new SclDataRepositoryException("Error constructing unmarshaller!", exp);
        }
    }

    public Item unmarshal(String xml) {
        try {
            var source = new StreamSource(new StringReader(xml));
            return jaxbUnmarshaller.unmarshal(source, Item.class).getValue();
        } catch (JAXBException exp) {
            LOGGER.error("Unmarshalling problem: {}", exp.getLocalizedMessage());
            throw new SclDataRepositoryException("Error unmarshalling!", exp);
        }
    }
}
