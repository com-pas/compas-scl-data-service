// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.exception;

import org.lfenergy.compas.core.commons.exception.CompasException;

public class CompasSclDataServiceException extends CompasException {
    public CompasSclDataServiceException(String errorCode, String message) {
        super(errorCode,message);
    }

    public CompasSclDataServiceException(String errorCode, String message, Throwable cause) {
        super(errorCode,message, cause);
    }
}
