// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.core.commons.constraint.XmlAnyElementValid;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.w3c.dom.Element;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.util.List;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_NS_URI;

@Schema(description = "A request to update an existing entry in the database containing the SCL Element content. " +
        "A new version is created and the old version is also kept.")
@XmlRootElement(name = "UpdateRequest", namespace = SCL_DATA_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateRequest {
    @NotNull(message = "{org.lfenergy.compas.changeset.notnull.message}")
    @Schema(description = "Indicates what kind of change is to determine the new version.",
            example = "MAJOR")
    @XmlElement(name = "ChangeSet", namespace = SCL_DATA_SERVICE_V1_NS_URI, required = true)
    private ChangeSetType changeSetType;

    @Schema(description = "Comment that will be added to the new history record.")
    @XmlElement(name = "Comment", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private String comment;

    @XmlAnyElement
    @Size(min = 1, max = 1, message = "{org.lfenergy.compas.XmlAnyElementValid.moreElements.message}")
    @Schema(description = "Can contain one element, named 'SCL', containing a SCL XML Definition")
    @XmlAnyElementValid(elementName = "SCL", elementNamespace = SCL_NS_URI)
    protected List<Element> elements;

    public ChangeSetType getChangeSetType() {
        return changeSetType;
    }

    public void setChangeSetType(ChangeSetType changeSetType) {
        this.changeSetType = changeSetType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
}
