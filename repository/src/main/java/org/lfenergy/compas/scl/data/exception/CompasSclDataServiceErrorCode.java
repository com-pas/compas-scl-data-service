// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.exception;

public class CompasSclDataServiceErrorCode {
    CompasSclDataServiceErrorCode() {
        throw new UnsupportedOperationException("CompasSclDataRepositoryErrorCode class");
    }

    public static final String CREATION_ERROR_CODE = "SDS-0002";
    public static final String UNMARSHAL_ERROR_CODE = "SDS-0003";
    public static final String HEADER_NOT_FOUND_ERROR_CODE = "SDS-0004";
    public static final String NO_SCL_ELEMENT_FOUND_ERROR_CODE = "SDS-0005";
    public static final String NO_DATA_FOUND_ERROR_CODE = "SDS-0006";
    public static final String DUPLICATE_SCL_NAME_ERROR_CODE = "SDS-0007";
    public static final String INVALID_LABEL_ERROR_CODE = "SDS-0008";
    public static final String TOO_MANY_LABEL_ERROR_CODE = "SDS-0009";

    public static final String POSTGRES_SELECT_ERROR_CODE = "SDS-2000";
    public static final String POSTGRES_INSERT_ERROR_CODE = "SDS-2001";
    public static final String POSTGRES_DELETE_ERROR_CODE = "SDS-2002";
}
