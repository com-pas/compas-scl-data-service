// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.core.commons.constraint.FilenameValid;
import org.lfenergy.compas.core.commons.constraint.XmlAnyElementValid;
import org.w3c.dom.Element;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.util.List;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_NS_URI;

@Schema(description = "A request to create a new entry in the database containing the SCL Element content.")
@XmlRootElement(name = "CreateRequest", namespace = SCL_DATA_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateRequest {
    @FilenameValid
    @Schema(description = "The name that will be stored as Private Element and can later be used to determine the filename.",
            example = "STATION-0012312")
    @XmlElement(name = "Name", namespace = SCL_DATA_SERVICE_V1_NS_URI, required = true)
    private String name;

    @Schema(description = "Comment that will be added to the new history record.")
    @XmlElement(name = "Comment", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private String comment;

    @Size(min = 1, max = 1, message = "{org.lfenergy.compas.XmlAnyElementValid.moreElements.message}")
    @XmlAnyElementValid(elementName = "SCL", elementNamespace = SCL_NS_URI)
    @Schema(description = "Can contain one element, named 'SCL', containing a SCL XML Definition")
    @XmlAnyElement
    private List<Element> elements;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

