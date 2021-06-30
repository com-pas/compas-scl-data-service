// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

import static org.lfenergy.compas.scl.data.model.Constants.SDS_NAMESPACE;

@XmlRootElement(name = "TypeListResponse", namespace = SDS_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public class TypeListResponse {
    @XmlElement(name = "Type", namespace = SDS_NAMESPACE)
    private List<Type> types = new ArrayList<>();

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public List<Type> getTypes() {
        return types;
    }
}
