// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.xml;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.xml.Item;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTest extends AbstractPojoTester {
    @Override
    protected Class<?> getClassToBeTested() {
        return Item.class;
    }

    @Test
    void constructor_WhenCalledWithParameters_ThenValuesAreFilled() {
        var id = UUID.randomUUID().toString();
        var name = "Name";
        var version = "1.0.0";
        var labels = List.of("Label1");

        var item = new Item(id, name, version, labels);

        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(version, item.getVersion());
        assertEquals(labels, item.getLabels());
    }
}