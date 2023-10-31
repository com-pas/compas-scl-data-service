// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.Version;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import jakarta.enterprise.context.ApplicationScoped;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.*;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.HEADER_NOT_FOUND_ERROR_CODE;
import static org.lfenergy.compas.scl.extensions.commons.CompasExtensionsConstants.*;

/**
 * Support class to work with the SCL XML in a generic way as W3C Element/Node class.
 * This way multiple versions of the SCL XSD can easily be supported.
 */
@ApplicationScoped
public class SclElementProcessor {
    /**
     * Search for the SCL Header in the SCL Root Element and return that.
     *
     * @param scl The SCL Root Element
     * @return The Header Element if found or empty() if not.
     */
    public Optional<Element> getSclHeader(Element scl) {
        return getChildNodesByName(scl, SCL_HEADER_ELEMENT_NAME, SCL_NS_URI).stream()
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
        return getChildNodesByName(scl, SCL_PRIVATE_ELEMENT_NAME, SCL_NS_URI).stream()
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
        scl.setAttribute("xmlns:" + XML_DEFAULT_NS_PREFIX, COMPAS_EXTENSION_NS_URI);

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
        element.setPrefix(XML_DEFAULT_NS_PREFIX);
        element.setTextContent(value);
        compasPrivate.appendChild(element);
        return element;
    }

    /**
     * The method will remove all newer Hitem Element, including the version passed from the History Element.
     * It will search for Hitem Element where the Revision Attribute is empty and the version has the
     * pattern "Major version"."Minor version"."Patch version".
     * <p>
     * If the version is the same or newer the Hitem will be removed from the History Element.
     *
     * @param headerElement The Header Element containing the History Items.
     * @param version       The version from which to remove.
     */
    public void cleanupHistoryItem(Element headerElement, Version version) {
        var history = getChildNodesByName(headerElement, SCL_HISTORY_ELEMENT_NAME, SCL_NS_URI).stream().findFirst();
        history.ifPresent(historyElement ->
                getChildNodesByName(historyElement, SCL_HITEM_ELEMENT_NAME, SCL_NS_URI)
                        .stream()
                        .filter(hItemElement -> hItemElement.getAttribute("revision").isBlank())
                        .forEach(hItemElement -> {
                            if (shouldRemoveHItem(hItemElement, version)) {
                                historyElement.removeChild(hItemElement);
                            }
                        })
        );
    }

    /**
     * Check if the version uses the pattern that matches the one used by CoMPAS and if this is the case
     * compare the two versions and determine if the HItem version is smaller or the same as the new one
     * being created.
     *
     * @param hItemElement The HItem Element to check the version attribute from.
     * @param version      The new version that will be created.
     * @return True if the HItem has a smaller or the same version and should be removed.
     */
    boolean shouldRemoveHItem(Element hItemElement, Version version) {
        var hItemVersion = hItemElement.getAttribute("version");
        if (hItemVersion.isBlank() || !hItemVersion.matches(Version.PATTERN)) {
            return false;
        }
        return version.compareTo(new Version(hItemVersion)) <= 0;
    }

    /**
     * Add a Hitem to the History Element from the Header. If the Header doesn't contain a History Element
     * this Element will also be created. The revision attribute will be empty, the "when" will be set to the
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

        var history = getChildNodesByName(header, SCL_HISTORY_ELEMENT_NAME, SCL_NS_URI).stream().findFirst()
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
        return (!value.isBlank()) ? Optional.of(value) : Optional.empty();
    }

    /**
     * Search for a Child Node on the passed Element.
     *
     * @param root      The element on which to search for a child Node.
     * @param localName The name of the Child Node.
     * @return The Child Node if found or empty() if not.
     */
    public Optional<Element> getChildNodeByName(Element root, String localName, String namespaceURI) {
        return getChildNodesByName(root, localName, namespaceURI)
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
    public List<Element> getChildNodesByName(Element root, String localName, String namespaceURI) {
        var foundElements = new ArrayList<Element>();
        var childNodes = root.getChildNodes();
        if (childNodes.getLength() > 0) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element element
                        && localName.equals(element.getLocalName())
                        && namespaceURI.equals(element.getNamespaceURI())) {
                    foundElements.add(element);
                }
            }
        }
        return foundElements;
    }
}
