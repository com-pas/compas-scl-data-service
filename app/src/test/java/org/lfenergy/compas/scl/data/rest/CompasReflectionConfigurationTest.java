// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CompasReflectionConfigurationTest {
    @Test
    void constructor_WhenCalled_ThenNoExceptions() {
        var config = new CompasReflectionConfiguration();
        assertNotNull(config);
    }
}