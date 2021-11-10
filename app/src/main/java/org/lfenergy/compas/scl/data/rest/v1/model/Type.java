// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@Schema(description = "Describing a type of SCL.")
@XmlAccessorType(XmlAccessType.FIELD)
public class Type {
    @Schema(description = "The code of the SCL Type.", example = "SCD")
    @XmlElement(name = "Code", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private String code;

    @Schema(description = "The description of the SCL Type.", example = "Substation Configuration Description")
    @XmlElement(name = "Description", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private String description;

    public Type(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
