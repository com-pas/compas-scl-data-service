// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.event.model;

import org.lfenergy.compas.scl.extensions.model.SclFileType;

import javax.websocket.Session;

public class CreateEventRequest {
    private final Session session;
    private final SclFileType type;
    private final String name;
    private final String who;
    private final String comment;
    private final String sclData;

    public CreateEventRequest(Session session, SclFileType type, String name, String who, String comment, String sclData) {
        this.session = session;
        this.type = type;
        this.name = name;
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

    public String getName() {
        return name;
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
