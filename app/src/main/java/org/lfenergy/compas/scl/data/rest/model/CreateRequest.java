// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.model;

import org.lfenergy.compas.scl.SCL;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateRequest {
    @XmlElement(name = "SCL")
    private SCL scl;
    @XmlElement(name = "Name")
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
