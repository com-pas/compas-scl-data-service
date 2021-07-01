// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.scl.model.SCL;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.lfenergy.compas.scl.data.model.Constants.SCL_NAMESPACE;
import static org.lfenergy.compas.scl.data.model.Constants.SDS_NAMESPACE;

@XmlRootElement(name = "CreateRequest", namespace = SDS_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateRequest {
    @Schema(example = "SCL XML...")
    @XmlElement(name = "SCL", namespace = SCL_NAMESPACE, required = true)
    private SCL scl;
    @Schema(example = "STATION-0012312")
    @XmlElement(name = "Name", namespace = SDS_NAMESPACE, required = true)
    private String name;

    public SCL getScl() {
        return scl;
    }

    public void setScl(SCL scl) {
        this.scl = scl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
