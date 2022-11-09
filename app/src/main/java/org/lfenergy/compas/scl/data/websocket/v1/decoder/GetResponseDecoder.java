// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1.decoder;

import org.lfenergy.compas.core.websocket.AbstractDecoder;
import org.lfenergy.compas.core.websocket.WebsocketSupport;
import org.lfenergy.compas.scl.data.websocket.v1.model.GetResponse;

public class GetResponseDecoder extends AbstractDecoder<GetResponse> {
    @Override
    public boolean willDecode(String message) {
        return (message != null);
    }

    @Override
    public GetResponse decode(String message) {
        return WebsocketSupport.decode(message, GetResponse.class);
    }
}
