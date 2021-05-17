// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.scl.SCL;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.lfenergy.compas.scl.data.rest.Constants.SCL_NAMESPACE;

@XmlRootElement(name = "GetResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetResponse {
    @Schema(example = "SCL XML...")
    @XmlElement(name = "SCL", namespace = SCL_NAMESPACE, required = true)
    private SCL scl;

    public SCL getScl() {
        return scl;
    }

    public void setScl(SCL scl) {
        this.scl = scl;
    }
}
