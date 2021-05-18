// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SclTypeTest extends AbstractEnumTest {
    @Override
    protected Class<? extends Enum<?>> getEnumClass() {
        return SclType.class;
    }

    @Test
    void testDescription() {
        for (var type : SclType.values()) {
            assertNotNull(type.getDescription());
        }
    }
}
