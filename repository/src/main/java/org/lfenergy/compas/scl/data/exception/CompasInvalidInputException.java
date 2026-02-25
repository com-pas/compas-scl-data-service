// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.exception;

import org.lfenergy.compas.core.commons.exception.CompasException;

import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.INVALID_INPUT_ERROR_CODE;

public class CompasInvalidInputException extends CompasException {
    public CompasInvalidInputException(String message) {
        super(INVALID_INPUT_ERROR_CODE, message);
    }
}
