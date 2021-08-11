// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.exception;

import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.NO_DATA_FOUND_ERROR_CODE;

public class CompasNoDataFoundException extends CompasSclDataServiceException {
    public CompasNoDataFoundException(String message) {
        super(NO_DATA_FOUND_ERROR_CODE, message);
    }
}
