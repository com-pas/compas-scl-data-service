// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

public class HistoryItem extends AbstractItem implements IHistoryItem {
    private final String who;
    private final String when;
    private final String what;

    public HistoryItem(final String id, final String name, final String version, final String who, final String when, final String what) {
        super(id, name, version);
        this.who = who;
        this.when = when;
        this.what = what;
    }

    public String getWho() {
        return who;
    }

    public String getWhen() {
        return when;
    }

    public String getWhat() {
        return what;
    }

}
