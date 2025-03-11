package org.lfenergy.compas.scl.data.model;

import java.time.OffsetDateTime;
import java.util.List;

public class ArchivedReferencedResourceMetaItem extends AbstractArchivedResourceMetaItem implements IAbstractArchivedResourceMetaItem {

    String id;
    String name;
    String version;
    String comment;

    public ArchivedReferencedResourceMetaItem(String id, String name, String version, String author, String approver, String type, String contentType, String locationId, List<IResourceTagItem> fields, OffsetDateTime modifiedAt, OffsetDateTime archivedAt, String comment) {
        super(author, approver, type, contentType, locationId, fields, modifiedAt, archivedAt);
        this.id = id;
        this.name = name;
        this.version = version;
        this.comment = comment;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public String getComment() {
        return comment;
    }
}
