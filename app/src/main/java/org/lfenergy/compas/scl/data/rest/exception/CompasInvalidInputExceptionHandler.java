// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.exception;

import org.lfenergy.compas.core.commons.model.ErrorResponse;
import org.lfenergy.compas.scl.data.exception.CompasInvalidInputException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CompasInvalidInputExceptionHandler implements ExceptionMapper<CompasInvalidInputException> {
    @Override
    public Response toResponse(CompasInvalidInputException exception) {
        var response = new ErrorResponse();
        response.addErrorMessage(exception.getErrorCode(), exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }
}
