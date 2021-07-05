// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.lfenergy.compas.scl.data.repository.SclDataRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import static org.lfenergy.compas.scl.data.Constants.SCL_NS_URI;

public class SclElementConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SclElementConverter.class);

    public String convertToString(Element element) {
        return convertToString(element, true);
    }

    public String convertToString(Element element, boolean omitXmlDeclaration) {
        try {
            StringWriter buffer = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            if (omitXmlDeclaration) {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }

            transformer.transform(new DOMSource(element), new StreamResult(buffer));
            return buffer.toString();
        } catch (TransformerException exp) {
            final var exceptionMessage = exp.getLocalizedMessage();
            LOGGER.error("Marshalling problem: {}", exceptionMessage);
            throw new SclDataRepositoryException("Error marshalling!", exp);
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
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setIgnoringElementContentWhitespace(true);
            // Create DocumentBuilder with default configuration
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the content to Document object
            Document doc = builder.parse(inputSource);
            return (Element) doc.getElementsByTagNameNS(SCL_NS_URI, "SCL").item(0);
        } catch (ParserConfigurationException | SAXException | IOException exp) {
            final var exceptionMessage = exp.getLocalizedMessage();
            LOGGER.error("Unmarshalling problem: {}", exceptionMessage);
            throw new SclDataRepositoryException("Error unmarshalling!", exp);
        }
    }
}
