// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.websocket.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;
import java.util.UUID;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@Schema(description = "Request for retrieving a SCL from the database.")
@XmlType(name = "GetVersionWsRequest", namespace = SCL_DATA_SERVICE_V1_NS_URI)
@XmlRootElement(name = "GetVersionWsRequest", namespace = SCL_DATA_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetVersionWsRequest {
    @Schema(description = "The ID of the SCL File.",
            example = "f7b98f4d-3fe4-4df2-8533-d7f0c1800344")
    @NotNull
    @XmlElement(name = "Id", namespace = SCL_DATA_SERVICE_V1_NS_URI, required = true)
    private UUID id;

    @Schema(description = "The version of the SCL File.",
            example = "1.2.3")
    @NotBlank
    @XmlElement(name = "Version", namespace = SCL_DATA_SERVICE_V1_NS_URI, required = true)
    private String version;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
