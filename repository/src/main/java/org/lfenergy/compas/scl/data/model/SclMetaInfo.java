// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Schema(description = "Meta Info found in the database with all basic information about a SCL.")
@XmlAccessorType(XmlAccessType.FIELD)
public class SclMetaInfo extends AbstractItem {
    public SclMetaInfo() {
    }

    public SclMetaInfo(String id, String name, String version) {
        super(id, name, version);
    }
}
