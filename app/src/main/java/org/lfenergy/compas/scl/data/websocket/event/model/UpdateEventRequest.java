// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.event.model;

import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import jakarta.websocket.Session;
import java.util.UUID;

public class UpdateEventRequest {
    private final Session session;
    private final SclFileType type;
    private final UUID id;
    private final ChangeSetType changeSetType;
    private final String who;
    private final String comment;
    private final String sclData;

    public UpdateEventRequest(Session session, SclFileType type, UUID id, ChangeSetType changeSetType, String who, String comment, String sclData) {
        this.session = session;
        this.type = type;
        this.id = id;
        this.changeSetType = changeSetType;
        this.who = who;
        this.comment = comment;
        this.sclData = sclData;
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

    public ChangeSetType getChangeSetType() {
        return changeSetType;
    }

    public String getWho() {
        return who;
    }

    public String getComment() {
        return comment;
    }

    public String getSclData() {
        return sclData;
    }
}
