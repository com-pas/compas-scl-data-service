package org.lfenergy.compas.scl.data.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SclDataExceptionTest {
    @Test
    void SclDataException_WhenCalledWithOnlyCause_ThenCauseCanBeRetrieved() {
        Exception expected = new RuntimeException();
        SclDataException exception = new SclDataException(expected);

        assertEquals(expected, exception.getCause());
    }

    @Test
    void SclDataException_WhenCalledWithCauseAndMessage_ThenCauseAndMessageCanBeRetrieved() {
        String message = "The message";
        Exception expected = new RuntimeException();
        SclDataException exception = new SclDataException(message, expected);

        assertEquals(message, exception.getMessage());
        assertEquals(expected, exception.getCause());
    }
}