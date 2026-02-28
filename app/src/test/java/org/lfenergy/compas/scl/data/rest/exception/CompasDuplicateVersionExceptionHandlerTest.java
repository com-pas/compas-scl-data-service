// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.exception;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.model.ErrorResponse;
import org.lfenergy.compas.scl.data.exception.CompasDuplicateVersionException;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CompasDuplicateVersionExceptionHandlerTest {
    @Test
    void toResponse_WhenCalledWithException_ThenConflictReturnedWithBody() {
        var exception = new CompasDuplicateVersionException("Some message that will only be logged");
        var handler = new CompasDuplicateVersionExceptionHandler();

        var result = handler.toResponse(exception);
        assertEquals(CONFLICT.getStatusCode(), result.getStatus());
        var errorMessage = ((ErrorResponse) result.getEntity()).getErrorMessages().get(0);
        assertEquals(exception.getErrorCode(), errorMessage.getCode());
        assertEquals(exception.getMessage(), errorMessage.getMessage());
        assertNull(errorMessage.getProperty());
    }
}
