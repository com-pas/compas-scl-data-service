// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
@XmlSchema(
        namespace = "https://www.lfenergy.org/compas/SclDataService",
        xmlns = {
                @XmlNs(prefix = "scl", namespaceURI = "http://www.iec.ch/61850/2003/SCL"),
                @XmlNs(prefix = "compas", namespaceURI = "https://www.lfenergy.org/compas/v1"),
                @XmlNs(prefix = "sds", namespaceURI = "https://www.lfenergy.org/compas/SclDataService")
        })
package org.lfenergy.compas.scl.data.rest.model;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;