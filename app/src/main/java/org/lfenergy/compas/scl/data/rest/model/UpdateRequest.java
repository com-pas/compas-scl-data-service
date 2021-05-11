// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.model;

import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.ChangeSetType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UpdateRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateRequest {
    @XmlElement(name = "SCL")
    private SCL scl;
    @XmlElement(name = "ChangeSet")
    private ChangeSetType changeSetType;

    public SCL getScl() {
        return scl;
    }

    public void setScl(SCL scl) {
        this.scl = scl;
    }

    public ChangeSetType getUpdateType() {
        return changeSetType;
    }

    public void setUpdateType(ChangeSetType changeSetType) {
        this.changeSetType = changeSetType;
    }
}
