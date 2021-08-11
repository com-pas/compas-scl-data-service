// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.exception;

import org.lfenergy.compas.core.commons.exception.CompasException;

import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.NO_DATA_FOUND_ERROR_CODE;

public class CompasNoDataFoundException extends CompasException {
    public CompasNoDataFoundException(String message) {
        super(NO_DATA_FOUND_ERROR_CODE, message);
    }
}
