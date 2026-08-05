// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.lfenergy.compas.core.commons.model.ErrorResponse;
import org.lfenergy.compas.scl.data.exception.CompasAssignedLocationException;

@Provider
public class CompasAssignedLocationExceptionHandler implements ExceptionMapper<CompasAssignedLocationException> {

    @Override
    public Response toResponse(CompasAssignedLocationException exception) {
        var response = new ErrorResponse();
        response.addErrorMessage(exception.getErrorCode(), exception.getMessage());
        return Response.status(Response.Status.CONFLICT).entity(response).build();
    }
}
