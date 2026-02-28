// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.exception;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.model.ErrorResponse;
import org.lfenergy.compas.scl.data.exception.CompasInvalidInputException;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CompasInvalidInputExceptionHandlerTest {
    @Test
    void toResponse_WhenCalledWithException_ThenBadRequestReturnedWithBody() {
        var exception = new CompasInvalidInputException("Some message that will only be logged");
        var handler = new CompasInvalidInputExceptionHandler();

        var result = handler.toResponse(exception);
        assertEquals(BAD_REQUEST.getStatusCode(), result.getStatus());
        var errorMessage = ((ErrorResponse) result.getEntity()).getErrorMessages().get(0);
        assertEquals(exception.getErrorCode(), errorMessage.getCode());
        assertEquals(exception.getMessage(), errorMessage.getMessage());
        assertNull(errorMessage.getProperty());
    }
}
