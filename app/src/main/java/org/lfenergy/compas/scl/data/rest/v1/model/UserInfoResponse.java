// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@Schema(description = "Response with User information, like name and session timeouts.")
@XmlRootElement(name = "UserInfoResponse", namespace = SCL_DATA_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class UserInfoResponse {
    @Schema(description = "The name of the user retrieved from the JWT Claim.",
            example = "John Johnson")
    @XmlElement(name = "Name", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private String name;

    @Schema(description = "The number of minutes when the frontend isn't used and a warning message will be shown.",
            example = "20")
    @XmlElement(name = "SessionWarning", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private int sessionWarning;

    @Schema(description = "The number of minutes when the session will expire and a expired message will be shown if the frontend isn't used.",
            example = "30")
    @XmlElement(name = "SessionExpires", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private int sessionExpires;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSessionWarning() {
        return sessionWarning;
    }

    public void setSessionWarning(int sessionWarning) {
        this.sessionWarning = sessionWarning;
    }

    public int getSessionExpires() {
        return sessionExpires;
    }

    public void setSessionExpires(int sessionExpires) {
        this.sessionExpires = sessionExpires;
    }
}
