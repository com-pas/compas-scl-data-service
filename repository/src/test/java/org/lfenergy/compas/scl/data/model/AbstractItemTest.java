// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractItemTest extends AbstractPojoTester {
    @Override
    protected Class<?> getClassToBeTested() {
        return SomeImplementation.class;
    }

    @Test
    void constructor_WhenCalledWithParameters_ThenValuesAreFilled() {
        var id = UUID.randomUUID().toString();
        var name = "Name";
        var version = "1.0.0";

        var item = new SomeImplementation(id, name, version);

        assertEquals(id, item.getId());
        assertEquals(name, item.getName());
        assertEquals(version, item.getVersion());
    }

    private static class SomeImplementation extends AbstractItem {
        public SomeImplementation() {
        }

        public SomeImplementation(String id, String name, String version) {
            super(id, name, version);
        }
    }
}