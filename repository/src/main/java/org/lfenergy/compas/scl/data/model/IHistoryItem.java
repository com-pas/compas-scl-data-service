// SPDX-FileCopyrightText: 2023 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

public interface IHistoryItem extends IAbstractItem {

    String getWho();
    String getWhat();
    String getWhen();

}
