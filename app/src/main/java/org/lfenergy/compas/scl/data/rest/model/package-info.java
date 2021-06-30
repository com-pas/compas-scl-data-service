// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
@XmlSchema(
        namespace = SDS_NAMESPACE,
        xmlns = {
                @XmlNs(prefix = "scl", namespaceURI = "http://www.iec.ch/61850/2003/SCL"),
                @XmlNs(prefix = "compas", namespaceURI = "https://www.lfenergy.org/compas/v1"),
                @XmlNs(prefix = "sds", namespaceURI = SDS_NAMESPACE)
        })
package org.lfenergy.compas.scl.data.rest.model;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;

import static org.lfenergy.compas.scl.data.model.Constants.SDS_NAMESPACE;
