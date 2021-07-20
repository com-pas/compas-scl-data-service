// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;

import static org.lfenergy.compas.scl.data.Constants.SCL_DATA_SERVICE_NS_URI;

@XmlRootElement(name = "CreateRequest", namespace = SCL_DATA_SERVICE_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateRequest {
    @Schema(example = "STATION-0012312")
    @XmlElement(name = "Name", namespace = SCL_DATA_SERVICE_NS_URI, required = true)
    private String name;
    @Schema(example = "SCL XML...")
    @XmlAnyElement
    protected Element scl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Element getScl() {
        return scl;
    }

    public void setScl(Element scl) {
        this.scl = scl;
    }
}

