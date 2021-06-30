// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.UUID;

import static org.lfenergy.compas.scl.data.model.Constants.SDS_NAMESPACE;

@XmlRootElement(name = "CreateResponse", namespace = SDS_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateResponse {
    @XmlElement(name = "Id", namespace = SDS_NAMESPACE, required = true)
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
