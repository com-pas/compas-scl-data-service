// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

public class SclDataRepositoryException extends RuntimeException {
    public SclDataRepositoryException(String message) {
        super(message);
    }

    public SclDataRepositoryException(Throwable cause) {
        super(cause);
    }

    public SclDataRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
