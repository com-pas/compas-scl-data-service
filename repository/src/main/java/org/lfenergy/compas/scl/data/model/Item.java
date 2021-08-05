// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@XmlAccessorType(XmlAccessType.FIELD)
public class Item {
    @XmlElement(
            name = "Id",
            namespace = SCL_DATA_SERVICE_V1_NS_URI,
            required = true
    )
    private String id;
    @XmlElement(
            name = "Name",
            namespace = SCL_DATA_SERVICE_V1_NS_URI,
            required = true
    )
    private String name;
    @XmlElement(
            name = "Version",
            namespace = SCL_DATA_SERVICE_V1_NS_URI,
            required = true
    )
    private String version;

    public Item() {
    }

    public Item(String id, String name, String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
