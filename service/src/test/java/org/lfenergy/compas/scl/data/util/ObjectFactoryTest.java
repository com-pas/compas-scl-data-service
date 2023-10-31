// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.xml.HistoryItem;
import org.lfenergy.compas.scl.data.xml.Item;
import org.lfenergy.compas.scl.data.xml.ObjectFactory;
import org.lfenergy.compas.scl.data.xml.SclMetaInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ObjectFactoryTest {
    @Test
    void createItem_WhenMethodsCalled_ThenObjectsAreCreated() {
        var objectFactory = new ObjectFactory();

        var item = objectFactory.createItem();
        assertNotNull(item);

        var itemElement = objectFactory.createItem(item);
        assertNotNull(itemElement);
        assertEquals(Item.class, itemElement.getDeclaredType());
        assertEquals(item, itemElement.getValue());
    }

    @Test
    void createHistoryItem_WhenMethodsCalled_ThenObjectsAreCreated() {
        var objectFactory = new ObjectFactory();

        var item = objectFactory.createHistoryItem();
        assertNotNull(item);

        var itemElement = objectFactory.createHistoryItem(item);
        assertNotNull(itemElement);
        assertEquals(HistoryItem.class, itemElement.getDeclaredType());
        assertEquals(item, itemElement.getValue());
    }

    @Test
    void createSclMetaInfo_WhenMethodsCalled_ThenObjectsAreCreated() {
        var objectFactory = new ObjectFactory();

        var metaInfo = objectFactory.createSclMetaInfo();
        assertNotNull(metaInfo);

        var sclMetaInfoElement = objectFactory.createSclMetaInfo(metaInfo);
        assertNotNull(sclMetaInfoElement);
        assertEquals(SclMetaInfo.class, sclMetaInfoElement.getDeclaredType());
        assertEquals(metaInfo, sclMetaInfoElement.getValue());
    }
}