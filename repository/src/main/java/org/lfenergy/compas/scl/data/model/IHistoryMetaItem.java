// SPDX-FileCopyrightText: 2023 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import java.time.OffsetDateTime;

public interface IHistoryMetaItem extends IAbstractItem {

    String getType();

    String getAuthor();

    String getComment();

    OffsetDateTime getChangedAt();

    boolean isArchived();

    boolean isAvailable();

    boolean isDeleted();

}
