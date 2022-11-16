// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.exception;

import org.lfenergy.compas.core.commons.model.ErrorResponse;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class CompasNoDataFoundExceptionHandler implements ExceptionMapper<CompasNoDataFoundException> {
    @Override
    public Response toResponse(CompasNoDataFoundException exception) {
        var response = new ErrorResponse();
        response.addErrorMessage(exception.getErrorCode(), exception.getMessage());
        return Response.status(Response.Status.NOT_FOUND).entity(response).build();
    }
}
