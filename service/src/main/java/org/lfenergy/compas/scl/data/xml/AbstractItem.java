// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.xml;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import org.lfenergy.compas.scl.data.model.IAbstractItem;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractItem implements IAbstractItem {
    @Schema(description = "The ID of the SCL as stored in the database. Often a UUID.",
            example = "123e4567-e89b-12d3-a456-426614174000")
    @XmlElement(name = "Id",
            namespace = SCL_DATA_SERVICE_V1_NS_URI,
            required = true)
    private String id;

    @Schema(description = "The name of the SCL. This can also be used as part of the filename.",
            example = "STATION-0012312")
    @XmlElement(name = "Name",
            namespace = SCL_DATA_SERVICE_V1_NS_URI,
            required = true)
    private String name;

    @Schema(description = "The version of the SCL.",
            example = "1.2.4")
    @XmlElement(name = "Version",
            namespace = SCL_DATA_SERVICE_V1_NS_URI,
            required = true)
    private String version;

    public AbstractItem() { }

    protected AbstractItem(final String id, final String name, final String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setName(final String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

}
