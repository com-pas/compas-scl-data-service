// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class SclMetaInfo extends Item {
    public SclMetaInfo() {
    }

    public SclMetaInfo(String id, String name, String version) {
        super(id, name, version);
    }
}
