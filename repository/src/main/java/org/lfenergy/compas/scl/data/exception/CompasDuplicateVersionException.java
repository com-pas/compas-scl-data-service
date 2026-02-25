// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.exception;

import org.lfenergy.compas.core.commons.exception.CompasException;

import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.DUPLICATE_VERSION_ERROR_CODE;

public class CompasDuplicateVersionException extends CompasException {
    public CompasDuplicateVersionException(String message) {
        super(DUPLICATE_VERSION_ERROR_CODE, message);
    }
}
