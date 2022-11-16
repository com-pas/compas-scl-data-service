// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.event.model;

import org.lfenergy.compas.scl.extensions.model.SclFileType;

import javax.websocket.Session;
import java.util.UUID;

public class GetEventRequest {
    private final Session session;
    private final SclFileType type;
    private final UUID id;

    public GetEventRequest(Session session, SclFileType type, UUID id) {
        this.session = session;
        this.type = type;
        this.id = id;
    }

    public Session getSession() {
        return session;
    }

    public SclFileType getType() {
        return type;
    }

    public UUID getId() {
        return id;
    }
}
