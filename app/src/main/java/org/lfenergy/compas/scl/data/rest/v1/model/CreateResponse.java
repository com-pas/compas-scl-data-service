// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@Schema(description = "Response from creating a new entry in the database containing the SCL Element content.")
@XmlRootElement(name = "CreateWsResponse", namespace = SCL_DATA_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateResponse {
    @Schema(description = "The XML Content of the updated SCL added to the database. The content contains a XML according to the IEC 61850 standard.",
            example = "<![CDATA[<SCL xmlns=\"http://www.iec.ch/61850/2003/SCL\">....</SCL>]]")
    @XmlElement(name = "SclData", namespace = SCL_DATA_SERVICE_V1_NS_URI, required = true)
    private String sclData;

    public String getSclData() {
        return sclData;
    }

    public void setSclData(String sclData) {
        this.sclData = sclData;
    }
}
