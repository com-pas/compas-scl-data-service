// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
    public Item createItem() {
        return new Item();
    }

    @XmlElementDecl(namespace = "", name = "Item")
    public JAXBElement<Item> createItem(Item value) {
        return new JAXBElement<>(new QName("", ""), Item.class, value);
    }

    public SclMetaInfo createSclMetaInfo() {
        return new SclMetaInfo();
    }

    @XmlElementDecl(namespace = "", name = "SclMetaInfo")
    public JAXBElement<SclMetaInfo> createSclMetaInfo(SclMetaInfo value) {
        return new JAXBElement<>(new QName("", ""), SclMetaInfo.class, value);
    }
}
