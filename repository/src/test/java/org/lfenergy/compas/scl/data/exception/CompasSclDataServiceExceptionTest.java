// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.CREATION_ERROR_CODE;

class CompasSclDataServiceExceptionTest {
    @Test
    void constructor_WhenCalledWithOnlyMessage_ThenMessageCanBeRetrieved() {
        String expectedMessage = "The message";
        CompasSclDataServiceException exception =
                new CompasSclDataServiceException(CREATION_ERROR_CODE, expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void constructor_WhenCalledWithCauseAndMessage_ThenCauseAndMessageCanBeRetrieved() {
        String expectedMessage = "The message";
        Exception expectedCause = new RuntimeException();
        CompasSclDataServiceException exception =
                new CompasSclDataServiceException(CREATION_ERROR_CODE, expectedMessage, expectedCause);

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(expectedCause, exception.getCause());
    }
}