// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.core.commons.constraint.FilenameValid;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@Schema(description = "Request to create a new entry in the database containing the SCL Element content.")
@XmlRootElement(name = "CreateRequest", namespace = SCL_DATA_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateRequest {
    @FilenameValid
    @Schema(description = "The name that will be stored as Private Element and can later be used to determine the filename.",
            example = "STATION-0012312")
    @XmlElement(name = "Name", namespace = SCL_DATA_SERVICE_V1_NS_URI, required = true)
    private String name;

    @Schema(description = "Comment that will be added to the new history record.", example = "New substation configuration for X")
    @XmlElement(name = "Comment", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private String comment;

    @Schema(description = "The XML Content of the SCL added as CDATA. The content should contain a XML according to the IEC 61850 standard.",
            example = "<![CDATA[<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\">....</SCL>]]")
    @XmlElement(name = "SclData", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private String sclData;

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

    public String getSclData() {
        return sclData;
    }

    public void setSclData(String sclData) {
        this.sclData = sclData;
    }
}

