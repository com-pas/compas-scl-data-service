// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.lfenergy.compas.scl.data.model.HistoryItem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@Schema(description = "Response with a list of versions stored for a specific SCL.")
@XmlRootElement(name = "VersionsResponse", namespace = SCL_DATA_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class VersionsResponse {
    @Schema(description = "List of found Versions of a specific SCL in the database.")
    @XmlElement(name = "HistoryItem", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private List<HistoryItem> items;

    public List<HistoryItem> getItems() {
        return items;
    }

    public void setItems(List<HistoryItem> items) {
        this.items = items;
    }
}
