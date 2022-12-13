package org.lfenergy.compas.scl.data.rest.v1.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;

@Schema(description = "Response from duplicate name check in the database.")
@XmlRootElement(name = "DuplicateNameCheckResponse", namespace = SCL_DATA_SERVICE_V1_NS_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class DuplicateNameCheckResponse {
    @Schema(description = "Boolean result for duplicate name check.")
    @XmlElement(name = "Duplicate", namespace = SCL_DATA_SERVICE_V1_NS_URI)
    private boolean duplicate;

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }
}
