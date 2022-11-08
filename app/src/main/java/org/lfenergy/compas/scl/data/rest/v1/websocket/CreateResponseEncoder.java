// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1.websocket;

import org.lfenergy.compas.core.websocket.AbstractEncoder;
import org.lfenergy.compas.core.websocket.WebsocketSupport;
import org.lfenergy.compas.scl.data.rest.v1.model.CreateResponse;

public class CreateResponseEncoder extends AbstractEncoder<CreateResponse> {
    @Override
    public String encode(CreateResponse jaxbObject) {
        return WebsocketSupport.encode(jaxbObject, CreateResponse.class);
    }
}
