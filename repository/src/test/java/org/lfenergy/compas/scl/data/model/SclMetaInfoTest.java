// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SclMetaInfoTest extends AbstractPojoTester {
    @Override
    protected Class<?> getClassToBeTested() {
        return SclMetaInfo.class;
    }

    @Test
    void constructor_WhenCalledWithParameters_ThenValuesAreFilled() {
        var id = UUID.randomUUID().toString();
        var name = "Name";
        var version = "1.0.0";

        var metaInfo = new SclMetaInfo(id, name, version);

        assertEquals(id, metaInfo.getId());
        assertEquals(name, metaInfo.getName());
        assertEquals(version, metaInfo.getVersion());
    }
}