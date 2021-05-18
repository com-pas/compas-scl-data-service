// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import java.lang.reflect.InvocationTargetException;

abstract class AbstractEnumTest {
    protected void enumCodeCoverage(Class<? extends Enum<?>> enumClass) {
        try {
            for (var o : (Enum<?>[]) enumClass.getMethod("values").invoke(null)) {
                enumClass.getMethod("valueOf", String.class).invoke(null, o.name());
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exp) {
            throw new RuntimeException(exp);
        }
    }
}
