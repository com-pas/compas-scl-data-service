// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.lfenergy.compas.scl.data.repository.SclDataRepositoryException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.lfenergy.compas.scl.data.Constants.*;

public class SclElementProcessor {
    public Optional<Element> getSclHeader(Element scl) {
        return getChildNodesByName(scl, SCL_HEADER_ELEMENT_NAME).stream()
                .findFirst();
    }

    public Element addSclHeader(Element scl) {
        var header = scl.getOwnerDocument().createElementNS(SCL_NS_URI, SCL_HEADER_ELEMENT_NAME);
        header.setPrefix(SCL_NS_PREFIX);
        scl.insertBefore(header, scl.getFirstChild());
        return header;
    }

    public Optional<Element> getCompasPrivate(Element scl) {
        return getChildNodesByName(scl, SCL_PRIVATE_ELEMENT_NAME).stream()
                .filter(element -> element.hasAttribute(SCL_PRIVATE_TYPE_ATTR))
                .filter(element -> element.getAttribute(SCL_PRIVATE_TYPE_ATTR).equals(COMPAS_SCL_EXTENSION_TYPE))
                .findFirst();
    }

    public Element addCompasPrivate(Element scl) {
        scl.setAttribute("xmlns:" + COMPAS_EXTENSION_NS_PREFIX, COMPAS_EXTENSION_NS_URI);

        var tPrivate = scl.getOwnerDocument().createElementNS(SCL_NS_URI, SCL_PRIVATE_ELEMENT_NAME);
        tPrivate.setPrefix(SCL_NS_PREFIX);
        tPrivate.setAttribute(SCL_PRIVATE_TYPE_ATTR, COMPAS_SCL_EXTENSION_TYPE);

        var header = getSclHeader(scl)
                .orElseThrow(() -> new SclDataRepositoryException("Header not found in SCL!"));
        scl.insertBefore(tPrivate, header);
        return tPrivate;
    }


    public Element addCompasElement(Element compasPrivate, String localName, String value) {
        Element element = compasPrivate.getOwnerDocument().createElementNS(COMPAS_EXTENSION_NS_URI, localName);
        element.setPrefix(COMPAS_EXTENSION_NS_PREFIX);
        element.setTextContent(value);
        compasPrivate.appendChild(element);
        return element;
    }

    public Optional<String> getAttributeValue(Element element, String attributeName) {
        var value = element.getAttribute(attributeName);
        return (value != null) ? Optional.of(value) : Optional.empty();
    }

    public Optional<Element> getFirstChildNodeByName(Element root, String localName) {
        return getChildNodesByName(root, localName)
                .stream()
                .findFirst();
    }

    public List<Element> getChildNodesByName(Element root, String localName) {
        var foundElements = new ArrayList<Element>();
        var childNodes = root.getChildNodes();
        if (childNodes.getLength() > 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element && localName.equals(node.getLocalName())) {
                    foundElements.add((Element) node);
                }
            }
        }
        return foundElements;
    }
}
