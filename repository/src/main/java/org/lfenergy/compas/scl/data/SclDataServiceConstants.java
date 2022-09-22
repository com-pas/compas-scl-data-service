// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data;

public class SclDataServiceConstants {
    SclDataServiceConstants() {
        throw new UnsupportedOperationException("SclDataServiceConstants class");
    }

    public static final String SCL_DATA_SERVICE_V1_NS_URI = "https://www.lfenergy.org/compas/SclDataService/v1";

    public static final String SCL_NS_URI = "http://www.iec.ch/61850/2003/SCL";
    public static final String SCL_NS_PREFIX = "";
    public static final String SCL_ELEMENT_NAME = "SCL";
    public static final String SCL_ID_ATTR = "id";
    public static final String SCL_VERSION_ATTR = "version";
    public static final String SCL_REVISION_ATTR = "revision";

    public static final String SCL_HEADER_ELEMENT_NAME = "Header";
    public static final String SCL_PRIVATE_ELEMENT_NAME = "Private";
    public static final String SCL_PRIVATE_TYPE_ATTR = "type";
    public static final String SCL_HISTORY_ELEMENT_NAME = "History";
    public static final String SCL_HITEM_ELEMENT_NAME = "Hitem";
    public static final String SCL_WHO_ATTR = "who";
    public static final String SCL_WHEN_ATTR = "when";
    public static final String SCL_WHAT_ATTR = "what";
}
