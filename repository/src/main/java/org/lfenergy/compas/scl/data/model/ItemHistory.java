// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@Schema(description = "Item found in the database with all basic information including version info about a SCL.")
@XmlAccessorType(XmlAccessType.FIELD)
public class ItemHistory extends Item {
    @Schema(description = "Who created this version of the SCL.",
            example = "John Doe")
    @XmlElement(name = "Who",
            namespace = SCL_DATA_SERVICE_V1_NS_URI,
            required = true)
    private String who;

    @Schema(description = "When was this version of the SCL created.",
            example = "2022-06-23T12:24:32")
    @XmlElement(name = "When",
            namespace = SCL_DATA_SERVICE_V1_NS_URI,
            required = true)
    private String when;

    @Schema(description = "What changed in this version of the SCL.",
            example = "New substation added")
    @XmlElement(name = "What",
            namespace = SCL_DATA_SERVICE_V1_NS_URI,
            required = true)
    private String what;

    public ItemHistory() {
    }

    public ItemHistory(String id, String name, String version, String who, String when, String what) {
        super(id, name, version);
        this.who = who;
        this.when = when;
        this.what = what;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }
}
