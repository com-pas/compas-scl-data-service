// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.xml;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import org.lfenergy.compas.scl.data.model.IHistoryItem;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@Schema(description = "Item found in the database with all basic information including version info about a SCL.")
@XmlAccessorType(XmlAccessType.FIELD)
public class HistoryItem extends AbstractItem implements IHistoryItem {
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

    public HistoryItem() { 
        super();
    }
    
    public HistoryItem(final String id, final String name, final String version, final String who, final String when, final String what) {
        super(id, name, version);
        this.who = who;
        this.when = when;
        this.what = what;
    }

    public void setWho(final String who) {
        this.who = who;
    }

    public String getWho() {
        return who;
    }

    public void setWhen(final String when) {
        this.when = when;
    }

    public String getWhen() {
        return when;
    }

    public void setWhat(final String what) {
        this.what = what;
    }

    public String getWhat() {
        return what;
    }

}
