package org.lfenergy.compas.scl.data.model;

import java.time.OffsetDateTime;

public interface IHistoryMetaItem extends IAbstractItem {

    String getType();

    String getAuthor();

    String getComment();

    OffsetDateTime getChangedAt();

    boolean isDeleted();
}
