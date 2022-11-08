// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1.websocket;

import org.lfenergy.compas.core.websocket.AbstractEncoder;
import org.lfenergy.compas.core.websocket.WebsocketSupport;
import org.lfenergy.compas.scl.data.rest.v1.model.UpdateRequest;

public class UpdateRequestEncoder extends AbstractEncoder<UpdateRequest> {
    @Override
    public String encode(UpdateRequest jaxbObject) {
        return WebsocketSupport.encode(jaxbObject, UpdateRequest.class);
    }
}
