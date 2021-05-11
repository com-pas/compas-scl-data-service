package org.lfenergy.compas.scl.data.rest.model;

import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.ChangeSetType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
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
