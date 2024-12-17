package org.lfenergy.compas.scl.data.model;

import java.time.OffsetDateTime;
import java.util.List;

public interface IAbstractArchivedResourceMetaItem extends IAbstractItem {

    String getAuthor();

    String getApprover();

    String getType();

    String getContentType();

    String getLocation();

    default String getNote() {
        return null;
    }

    default String getVoltage() {
        return null;
    }

    List<IResourceTagItem> getFields();

    OffsetDateTime getModifiedAt();

    OffsetDateTime getArchivedAt();
}
