// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.event.model;

import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import jakarta.websocket.Session;
import java.util.UUID;

public class GetVersionEventRequest {
    private final Session session;
    private final SclFileType type;
    private final UUID id;
    private final Version version;
    private final String tenant;

    public GetVersionEventRequest(Session session, SclFileType type, UUID id, Version version, String tenant) {
        this.session = session;
        this.type = type;
        this.id = id;
        this.version = version;
        this.tenant = tenant;
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

    public Version getVersion() {
        return version;
    }

    public String getTenant() {
        return tenant;
    }
}
