// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@Schema(description = "Response with the List of SCL Types.")
@XmlRootElement(name = "TypeListResponse", namespace = SCL_DATA_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class TypeListResponse {
    @Schema(description = "List of SCL Types.")
    @XmlElement(name = "Type", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private List<Type> types = new ArrayList<>();

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public List<Type> getTypes() {
        return types;
    }
}
