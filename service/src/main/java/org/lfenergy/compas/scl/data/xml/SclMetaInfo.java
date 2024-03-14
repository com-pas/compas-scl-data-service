// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.xml;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@Schema(description = "Meta Info found in the database with all basic information about a SCL.")
@XmlAccessorType(XmlAccessType.FIELD)
public class SclMetaInfo extends AbstractItem {

    public SclMetaInfo() {
    }
    public SclMetaInfo(final String id, final String name, final String version) {
        super(id, name, version);
    }

}
