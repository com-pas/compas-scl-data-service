// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.HistoryItem;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclMetaInfo;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.CREATION_ERROR_CODE;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.UNMARSHAL_ERROR_CODE;

public class SclDataModelMarshaller {
    private static final Logger LOGGER = LogManager.getLogger(SclDataModelMarshaller.class);

    private final Unmarshaller jaxbUnmarshaller;

    public SclDataModelMarshaller() {
        try {
            var jaxbContext = JAXBContext.newInstance("org.lfenergy.compas.scl.data.model");
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException exp) {
            LOGGER.error("Construction problem: {}", exp.getLocalizedMessage(), exp);
            throw new CompasSclDataServiceException(CREATION_ERROR_CODE, "Error constructing unmarshaller!");
        }
    }

    public Item unmarshalItem(String xml) {
        return unmarshal(xml, Item.class);
    }

    public HistoryItem unmarshalHistoryItem(String xml) {
        return unmarshal(xml, HistoryItem.class);
    }

    public SclMetaInfo unmarshalSclMetaInfo(String xml) {
        return unmarshal(xml, SclMetaInfo.class);
    }

    private <T> T unmarshal(String xml, Class<T> clazz) {
        try {
            var source = new StreamSource(new StringReader(xml));
            return jaxbUnmarshaller.unmarshal(source, clazz).getValue();
        } catch (JAXBException exp) {
            LOGGER.error("Unmarshalling problem to class '{}': {}", clazz.getName(), exp.getLocalizedMessage(), exp);
            throw new CompasSclDataServiceException(UNMARSHAL_ERROR_CODE, "Error unmarshalling to class '" +
                    clazz.getName() + "'!");
        }
    }
}
