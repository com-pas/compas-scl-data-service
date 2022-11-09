// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1.decoder;

import org.lfenergy.compas.core.websocket.AbstractDecoder;
import org.lfenergy.compas.core.websocket.WebsocketSupport;
import org.lfenergy.compas.scl.data.websocket.v1.model.UpdateRequest;

public class UpdateRequestDecoder extends AbstractDecoder<UpdateRequest> {
    @Override
    public boolean willDecode(String message) {
        return (message != null);
    }

    @Override
    public UpdateRequest decode(String message) {
        return WebsocketSupport.decode(message, UpdateRequest.class);
    }
}
