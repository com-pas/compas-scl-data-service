// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static org.lfenergy.compas.scl.data.model.Constants.SDS_NAMESPACE;

@XmlAccessorType(XmlAccessType.FIELD)
public class Type {
    @XmlElement(name = "Code", namespace = SDS_NAMESPACE)
    private String code;
    @XmlElement(name = "Description", namespace = SDS_NAMESPACE)
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
