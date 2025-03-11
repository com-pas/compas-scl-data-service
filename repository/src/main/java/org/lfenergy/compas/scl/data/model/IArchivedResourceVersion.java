package org.lfenergy.compas.scl.data.model;

import java.time.OffsetDateTime;
import java.util.List;

public interface IArchivedResourceVersion extends IAbstractItem {
    String getLocation();

    String getNote();

    String getAuthor();

    String getApprover();

    String getType();

    String getContentType();

    String getVoltage();

    OffsetDateTime getModifiedAt();

    OffsetDateTime getArchivedAt();

    List<IResourceTagItem> getFields();

    String getComment();

    boolean isArchived();
}
