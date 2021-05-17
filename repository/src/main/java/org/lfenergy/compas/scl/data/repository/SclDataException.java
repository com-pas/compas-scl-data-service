// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import java.security.NoSuchAlgorithmException;

public class SclDataException extends RuntimeException {
    public SclDataException(Throwable cause) {
        super(cause);
    }

    public SclDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
