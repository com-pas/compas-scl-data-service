// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.ChangeSetType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.lfenergy.compas.scl.data.model.Constants.SCL_NAMESPACE;
import static org.lfenergy.compas.scl.data.model.Constants.SDS_NAMESPACE;

@XmlRootElement(name = "UpdateRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateRequest {
    @Schema(example = "SCL XML...")
    @XmlElement(name = "SCL", namespace = SCL_NAMESPACE, required = true)
    private SCL scl;
    @Schema(example = "MAJOR")
    @XmlElement(name = "ChangeSet", namespace = SDS_NAMESPACE, required = true)
    private ChangeSetType changeSetType;

    public SCL getScl() {
        return scl;
    }

    public void setScl(SCL scl) {
        this.scl = scl;
    }

    public ChangeSetType getChangeSetType() {
        return changeSetType;
    }

    public void setChangeSetType(ChangeSetType changeSetType) {
        this.changeSetType = changeSetType;
    }
}
