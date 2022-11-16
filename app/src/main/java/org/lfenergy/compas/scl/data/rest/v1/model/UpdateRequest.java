// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.scl.data.model.ChangeSetType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@Schema(description = "Request to update an existing entry in the database containing the SCL Element content. " +
        "A new version is created and the old version is also kept.")
@XmlRootElement(name = "UpdateRequest", namespace = SCL_DATA_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateRequest {
    @NotNull(message = "{org.lfenergy.compas.changeset.notnull.message}")
    @Schema(description = "Indicates what kind of change to determine the new version.",
            example = "MAJOR")
    @XmlElement(name = "ChangeSet", namespace = SCL_DATA_SERVICE_V1_NS_URI, required = true)
    private ChangeSetType changeSetType;

    @Schema(description = "Comment that will be added to the new history record.", example = "Renamed substation names")
    @XmlElement(name = "Comment", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private String comment;

    @Schema(description = "The XML Content of the SCL added as CDATA. The content should contain a XML according to the IEC 61850 standard.",
            example = "<![CDATA[<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\">....</SCL>]]")
    @NotBlank
    @XmlElement(name = "SclData", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private String sclData;

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

    public String getSclData() {
        return sclData;
    }

    public void setSclData(String sclData) {
        this.sclData = sclData;
    }
}
