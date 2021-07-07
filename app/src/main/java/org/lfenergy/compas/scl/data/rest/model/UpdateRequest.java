// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;

import static org.lfenergy.compas.scl.data.Constants.SCL_DATA_SERVICE_NS_URI;

@XmlRootElement(name = "UpdateRequest", namespace = SCL_DATA_SERVICE_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateRequest {
    @Schema(example = "MAJOR")
    @XmlElement(name = "ChangeSet", namespace = SCL_DATA_SERVICE_NS_URI, required = true)
    private ChangeSetType changeSetType;
    @Schema(example = "SCL XML...")
    @XmlAnyElement
    protected Element scl;

    public ChangeSetType getChangeSetType() {
        return changeSetType;
    }

    public void setChangeSetType(ChangeSetType changeSetType) {
        this.changeSetType = changeSetType;
    }

    public Element getScl() {
        return scl;
    }

    public void setScl(Element scl) {
        this.scl = scl;
    }
}
