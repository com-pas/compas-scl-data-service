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
    public Optional<Element> getHeader(Element scl) {
        return getElementByName(scl, "Header").stream()
                .findFirst();
    }

    public Element addHeader(Element scl) {
        Element header = scl.getOwnerDocument().createElementNS(SCL_NS_URI, "Header");
        header.setPrefix("");
        scl.insertBefore(header, scl.getFirstChild());
        return header;
    }

    public String getAttributeValue(Element element, String attributeName) {
        String value = element.getAttribute(attributeName);
        return (value != null) ? value : "";
    }

    public Optional<Element> getCompasPrivate(Element scl) {
        return getElementByName(scl, "Private").stream()
                .filter(element -> element.hasAttribute("type"))
                .filter(element -> element.getAttribute("type").equals("compas_scl"))
                .findFirst();
    }

    public Element addCompasPrivate(Element scl) {
        scl.setAttribute("xmlns:compas", COMPAS_EXTENSION_NS_URI);

        Element tPrivate = scl.getOwnerDocument().createElementNS(SCL_NS_URI, "Private");
        tPrivate.setPrefix("");
        tPrivate.setAttribute("type", COMPAS_SCL_EXTENSION_TYPE);

        Element header = getHeader(scl)
                .orElseThrow(() -> new SclDataRepositoryException("Header not found in SCL!"));
        scl.insertBefore(tPrivate, header);
        return tPrivate;
    }

    public Optional<Element> getCompasElement(Element compasPrivate, String compasElementName) {
        return getElementByName(compasPrivate, compasElementName).stream()
                .findFirst();
    }

    public void addCompasElement(Element compasPrivate, String localName, String value) {
        Element sclNameElement = compasPrivate.getOwnerDocument().createElementNS(COMPAS_EXTENSION_NS_URI, localName);
        sclNameElement.setPrefix("compas");
        sclNameElement.setTextContent(value);
        compasPrivate.appendChild(sclNameElement);
    }

    private List<Element> getElementByName(Element root, String localName) {
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
