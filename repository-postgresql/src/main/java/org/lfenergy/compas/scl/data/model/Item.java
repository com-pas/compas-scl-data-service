// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import java.util.List;

public class Item extends AbstractItem implements IItem {

    private final List<String> labels;

    public Item(final String id, final String name, final String version, final List<String> labels) {
        super(id, name, version);
        this.labels = labels;
    }

    public List<String> getLabels() {
        return labels;
    }

}
