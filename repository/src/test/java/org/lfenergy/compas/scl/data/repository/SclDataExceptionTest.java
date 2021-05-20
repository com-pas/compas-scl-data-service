// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SclDataExceptionTest {
    @Test
    void constructor_WhenCalledWithOnlyMessage_ThenMessageCanBeRetrieved() {
        String expectedMessage = "The message";
        SclDataException exception = new SclDataException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void constructor_WhenCalledWithOnlyCause_ThenCauseCanBeRetrieved() {
        Exception expectedCause = new RuntimeException();
        SclDataException exception = new SclDataException(expectedCause);

        assertEquals(expectedCause, exception.getCause());
    }

    @Test
    void constructor_WhenCalledWithCauseAndMessage_ThenCauseAndMessageCanBeRetrieved() {
        String expectedMessage = "The message";
        Exception expectedCause = new RuntimeException();
        SclDataException exception = new SclDataException(expectedMessage, expectedCause);

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(expectedCause, exception.getCause());
    }
}