// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HistoryItemTest extends AbstractPojoTester {
    @Override
    protected Class<?> getClassToBeTested() {
        return HistoryItem.class;
    }

    @Test
    void constructor_WhenCalledWithParameters_ThenValuesAreFilled() {
        var id = UUID.randomUUID().toString();
        var name = "Name";
        var version = "1.0.0";
        var who = "who";
        var when = "when";
        var what = "what";

        var item = new HistoryItem(id, name, version, who, when, what);

        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(version, item.getVersion());
        assertEquals(who, item.getWho());
        assertEquals(when, item.getWhen());
        assertEquals(what, item.getWhat());
    }
}