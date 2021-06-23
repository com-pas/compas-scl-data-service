// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import org.junit.jupiter.api.Test;

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
        var filename = "Filename";
        var version = "1.0.0";

        var item = new Item(id, filename, version);

        assertEquals(id, item.getId());
        assertEquals(filename, item.getFilename());
        assertEquals(version, item.getVersion());
    }
}