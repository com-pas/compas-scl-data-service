// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.extensions.TSclFileType;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SclTypeTest extends AbstractEnumTest {
    @Override
    protected Class<? extends Enum<?>> getEnumClass() {
        return SclType.class;
    }

    @Test
    void getDescription_WhenCalledForEveryType_ThenEveryTypeHasADescription() {
        for (var type : SclType.values()) {
            assertNotNull(type.getDescription());
        }
    }

    @Test
    void convert_WhenCalledForEveryType_ThenEveryTypeCanBeConvertedToTSclFileType() {
        for (var type : SclType.values()) {
            TSclFileType tSclFileType = TSclFileType.valueOf(type.toString());
            assertNotNull(tSclFileType);
        }
    }

    @Test
    void convert_WhenCalledForEveryTSclFileType_ThenEveryTSclFileTypeCanBeConvertedToType() {
        for (var tSclFileType : TSclFileType.values()) {
            SclType type = SclType.valueOf(tSclFileType.toString());
            assertNotNull(type);
        }
    }
}
