// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.model;

import org.lfenergy.compas.scl.SCL;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.lfenergy.compas.scl.data.rest.Constants.SCL_NAMESPACE;

@XmlRootElement(name = "CreateRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateRequest {
    @XmlElement(name = "SCL", namespace = SCL_NAMESPACE, required = true)
    private SCL scl;
    @XmlElement(name = "Name", required = true)
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
