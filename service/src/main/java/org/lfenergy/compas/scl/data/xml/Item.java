// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.xml;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import org.lfenergy.compas.scl.data.model.IItem;

import java.util.ArrayList;
import java.util.List;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@Schema(description = "Item found in the database with all basic information about a SCL.")
@XmlAccessorType(XmlAccessType.FIELD)
public class Item extends AbstractItem implements IItem {
    @Schema(description = "List of Labels linked to the SCL File.",
            example = "[Label-1, Label-2]")
    @XmlElement(name = "Label",
            namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private List<String> labels = new ArrayList<>();

    public Item() { }

    public Item(final String id, final String name, final String version, final List<String> labels) {
        super(id, name, version);
        this.labels = labels;
    }

    public void setLabels(final List<String> labels) {
        this.labels = labels;
    }

    public List<String> getLabels() {
        return labels;
    }

}
