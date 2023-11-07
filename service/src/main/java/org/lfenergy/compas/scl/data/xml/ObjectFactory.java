// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.xml;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
    public Item createItem() {
        return new Item();
    }

    public HistoryItem createHistoryItem() {
        return new HistoryItem();
    }

    @XmlElementDecl(namespace = "", name = "Item")
    public JAXBElement<Item> createItem(Item value) {
        return new JAXBElement<>(new QName("", ""), Item.class, value);
    }

    @XmlElementDecl(namespace = "", name = "HistoryItem")
    public JAXBElement<HistoryItem> createHistoryItem(HistoryItem value) {
        return new JAXBElement<>(new QName("", ""), HistoryItem.class, value);
    }

    public SclMetaInfo createSclMetaInfo() {
        return new SclMetaInfo();
    }

    @XmlElementDecl(namespace = "", name = "SclMetaInfo")
    public JAXBElement<SclMetaInfo> createSclMetaInfo(SclMetaInfo value) {
        return new JAXBElement<>(new QName("", ""), SclMetaInfo.class, value);
    }
}
