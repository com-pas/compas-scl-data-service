// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import org.junit.jupiter.api.Test;

class SclTypeTest extends AbstractEnumTest {
    @Test
    void testEnumValues() {
        enumCodeCoverage(SclType.class);
    }
}
