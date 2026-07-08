// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.exception;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.model.ErrorResponse;
import org.lfenergy.compas.scl.data.exception.CompasAssignedLocationException;

import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CompasAssignedLocationExceptionHandlerTest {

    @Test
    void toResponse_WhenCalledWithCompasAssignedLocationException_ThenConflictReturnedWithBody() {
        var exception = new CompasAssignedLocationException("Location is assigned to a SCL file.");
        var handler = new CompasAssignedLocationExceptionHandler();

        var result = handler.toResponse(exception);
        assertEquals(CONFLICT.getStatusCode(), result.getStatus());
        var errorMessage = ((ErrorResponse) result.getEntity()).getErrorMessages().get(0);
        assertEquals(exception.getErrorCode(), errorMessage.getCode());
        assertEquals(exception.getMessage(), errorMessage.getMessage());
        assertNull(errorMessage.getProperty());
    }
}
