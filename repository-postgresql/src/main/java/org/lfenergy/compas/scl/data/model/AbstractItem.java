// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

public abstract class AbstractItem implements IAbstractItem {

    private final String id;
    private final String name;
    private final String version;
    private final String locationId;
    protected AbstractItem(final String id, final String name, final String version, final String locationId) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.locationId = locationId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getLocationId() {
        return locationId;
    }

}
