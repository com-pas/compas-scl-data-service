// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1.websocket;

import org.lfenergy.compas.core.websocket.AbstractDecoder;
import org.lfenergy.compas.core.websocket.WebsocketSupport;
import org.lfenergy.compas.scl.data.rest.v1.model.UpdateResponse;

public class UpdateResponseDecoder extends AbstractDecoder<UpdateResponse> {
    @Override
    public boolean willDecode(String message) {
        return (message != null);
    }

    @Override
    public UpdateResponse decode(String message) {
        return WebsocketSupport.decode(message, UpdateResponse.class);
    }
}
