// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompasInvalidInputExceptionTest {
    @Test
    void constructor_WhenCalledWithOnlyMessage_ThenMessageCanBeRetrieved() {
        String expectedMessage = "The message";
        var exception = new CompasInvalidInputException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }
}
