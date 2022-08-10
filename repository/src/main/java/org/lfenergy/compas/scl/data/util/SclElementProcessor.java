// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.Version;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.*;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.HEADER_NOT_FOUND_ERROR_CODE;

/**
 * Support class to work with the SCL XML in a generic way as W3C Element/Node class.
 * This way multiple versions of the SCL XSD can easily be supported.
 */
public class SclElementProcessor {
    /**
     * Search for the SCL Header in the SCL Root Element and return that.
     *
     * @param scl The SCL Root Element
     * @return The Header Element if found or empty() if not.
     */
    public Optional<Element> getSclHeader(Element scl) {
        return getChildNodesByName(scl, SCL_HEADER_ELEMENT_NAME).stream()
                .findFirst();
    }

    /**
     * Add the SCL Header ot the SCL Root Element, because that element is important for the SCL Data Service
     * we want to make sure it's there.
     *
     * @param scl The SCL Root Element
     * @return The new created Header Element.
     */
    public Element addSclHeader(Element scl) {
        var header = scl.getOwnerDocument().createElementNS(SCL_NS_URI, SCL_HEADER_ELEMENT_NAME);
        header.setPrefix(SCL_NS_PREFIX);
        scl.insertBefore(header, scl.getFirstChild());
        return header;
    }

    /**
     * Search for the private element with type "#Constants.COMPAS_SCL_EXTENSION_TYPE" on the SCL Root Element.
     *
     * @param scl The SCL Root Element
     * @return The Private Element with the correct type if found or empty() if not.
     */
    public Optional<Element> getCompasPrivate(Element scl) {
        return getChildNodesByName(scl, SCL_PRIVATE_ELEMENT_NAME).stream()
                .filter(element -> element.hasAttribute(SCL_PRIVATE_TYPE_ATTR))
                .filter(element -> element.getAttribute(SCL_PRIVATE_TYPE_ATTR).equals(COMPAS_SCL_EXTENSION_TYPE))
                .findFirst();
    }

    /**
     * Create a Private Element with type "#COMPAS_SCL_EXTENSION_TYPE" on the SCL Root Element.
     *
     * @param scl The SCL Root Element
     * @return The new created Private Element with the correct type.
     */
    public Element addCompasPrivate(Element scl) {
        scl.setAttribute("xmlns:" + COMPAS_EXTENSION_NS_PREFIX, COMPAS_EXTENSION_NS_URI);

        var tPrivate = scl.getOwnerDocument().createElementNS(SCL_NS_URI, SCL_PRIVATE_ELEMENT_NAME);
        tPrivate.setPrefix(SCL_NS_PREFIX);
        tPrivate.setAttribute(SCL_PRIVATE_TYPE_ATTR, COMPAS_SCL_EXTENSION_TYPE);

        var header = getSclHeader(scl)
                .orElseThrow(() -> new CompasSclDataServiceException(HEADER_NOT_FOUND_ERROR_CODE, "Header not found in SCL!"));
        scl.insertBefore(tPrivate, header);
        return tPrivate;
    }

    /**
     * Add a new element with namespace "#COMPAS_EXTENSION_NS_URI" to the Private element of CoMPAS.
     *
     * @param compasPrivate The Private Element on which to add the new Element.
     * @param localName     The name of the Element to create.
     * @param value         The value set on the new Element.
     * @return The new created Element.
     */
    public Element addCompasElement(Element compasPrivate, String localName, String value) {
        Element element = compasPrivate.getOwnerDocument().createElementNS(COMPAS_EXTENSION_NS_URI, localName);
        element.setPrefix(COMPAS_EXTENSION_NS_PREFIX);
        element.setTextContent(value);
        compasPrivate.appendChild(element);
        return element;
    }

    /**
     * Add a Hitem to the History Element from the Header. If the Header doesn't contain a History Element
     * this Element will also be created. The revision attribute will be empty, the when will be set to the
     * current date.
     *
     * @param header      The Header Element from SCL under which the History Element can be found/added.
     * @param who         Teh name of the user that made the change (who).
     * @param fullmessage The message that will be set (what).
     * @param version     The version to be set (version).
     * @return The Hitem created and added to the History Element.
     */
    public Element addHistoryItem(Element header, String who, String fullmessage, Version version) {
        var formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        var document = header.getOwnerDocument();

        var history = getChildNodesByName(header, SCL_HISTORY_ELEMENT_NAME).stream().findFirst()
                .orElseGet(() -> {
                    Element newHistory = document.createElementNS(SCL_NS_URI, SCL_HISTORY_ELEMENT_NAME);
                    header.appendChild(newHistory);
                    return newHistory;
                });

        Element hItem = document.createElementNS(SCL_NS_URI, SCL_HITEM_ELEMENT_NAME);
        hItem.setAttribute(SCL_VERSION_ATTR, version.toString());
        hItem.setAttribute(SCL_REVISION_ATTR, "");
        hItem.setAttribute(SCL_WHEN_ATTR, formatter.format(new Date()));
        hItem.setAttribute(SCL_WHO_ATTR, who);
        hItem.setAttribute(SCL_WHAT_ATTR, fullmessage);
        history.appendChild(hItem);

        return hItem;
    }

    /**
     * Returns the value of a specific attribute from the passed Element.
     *
     * @param element       The Element to search for the attribute.
     * @param attributeName The name of the Attribute to search for.
     * @return The value if found or empty() if not.
     */
    public Optional<String> getAttributeValue(Element element, String attributeName) {
        var value = element.getAttribute(attributeName);
        return (value != null && !value.isBlank()) ? Optional.of(value) : Optional.empty();
    }

    /**
     * Search for a Child Node on the passed Element.
     *
     * @param root      The element on which to search for a child Node.
     * @param localName The name of the Child Node.
     * @return The Child Node if found or empty() if not.
     */
    public Optional<Element> getChildNodeByName(Element root, String localName) {
        return getChildNodesByName(root, localName)
                .stream()
                .findFirst();
    }

    /**
     * Search for all Child Node on the passed Element.
     *
     * @param root      The element on which to search for all child Node.
     * @param localName The name of the Child Node.
     * @return The list of Child Nodes found.
     */
    public List<Element> getChildNodesByName(Element root, String localName) {
        var foundElements = new ArrayList<Element>();
        var childNodes = root.getChildNodes();
        if (childNodes.getLength() > 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element element && localName.equals(element.getLocalName())) {
                    foundElements.add(element);
                }
            }
        }
        return foundElements;
    }
}
