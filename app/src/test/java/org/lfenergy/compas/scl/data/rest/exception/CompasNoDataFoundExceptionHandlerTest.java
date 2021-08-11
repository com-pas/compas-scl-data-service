// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.exception;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.rest.model.ErrorResponse;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CompasNoDataFoundExceptionHandlerTest {
    @Test
    void toResponse_WhenCalledWithException_ThenNotFoundReturnedWithBody() {
        var exception = new CompasNoDataFoundException("Some message that will only be logged");
        var handler = new CompasNoDataFoundExceptionHandler();

        var result = handler.toResponse(exception);
        assertEquals(NOT_FOUND.getStatusCode(), result.getStatus());
        var errorMessage = ((ErrorResponse) result.getEntity()).getErrorMessages().get(0);
        assertEquals(exception.getErrorCode(), errorMessage.getCode());
        assertEquals(exception.getMessage(), errorMessage.getMessage());
        assertNull(errorMessage.getProperty());
    }
}