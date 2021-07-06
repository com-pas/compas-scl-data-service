// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.lfenergy.compas.scl.data.repository.SclDataRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import static org.lfenergy.compas.scl.data.Constants.SCL_ELEMENT_NAME;
import static org.lfenergy.compas.scl.data.Constants.SCL_NS_URI;

public class SclElementConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SclElementConverter.class);

    public String convertToString(Element element) {
        return convertToString(element, true);
    }

    public String convertToString(Element element, boolean omitXmlDeclaration) {
        try {
            var buffer = new StringWriter();
            var transformer = TransformerFactory.newInstance().newTransformer();
            if (omitXmlDeclaration) {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }

            transformer.transform(new DOMSource(element), new StreamResult(buffer));
            return buffer.toString();
        } catch (TransformerException exp) {
            LOGGER.error("Converting problem: {}", exp.getLocalizedMessage());
            throw new SclDataRepositoryException("Error converting to a String!", exp);
        }
    }

    public Element convertToElement(String xml) {
        return convertToElement(new InputSource(new StringReader(xml)));
    }

    public Element convertToElement(InputStream xml) {
        return convertToElement(new InputSource(xml));
    }

    private Element convertToElement(InputSource inputSource) {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setIgnoringElementContentWhitespace(true);
            // Create DocumentBuilder with default configuration
            var builder = factory.newDocumentBuilder();

            // Parse the content to Document object
            var doc = builder.parse(inputSource);
            return (Element) doc.getElementsByTagNameNS(SCL_NS_URI, SCL_ELEMENT_NAME).item(0);
        } catch (ParserConfigurationException | SAXException | IOException exp) {
            LOGGER.error("Converting problem: {}", exp.getLocalizedMessage());
            throw new SclDataRepositoryException("Error converting to a Element!", exp);
        }
    }
}
