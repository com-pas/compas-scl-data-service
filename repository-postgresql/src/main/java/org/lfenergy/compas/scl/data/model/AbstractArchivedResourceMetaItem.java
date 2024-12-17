package org.lfenergy.compas.scl.data.model;

import java.time.OffsetDateTime;
import java.util.List;

public abstract class AbstractArchivedResourceMetaItem implements IAbstractArchivedResourceMetaItem, IAbstractItem {

    private final String author;
    private final String approver;
    private final String type;
    private final String contentType;
    private final String location;
    private final List<IResourceTagItem> fields;
    private final OffsetDateTime modifiedAt;
    private final OffsetDateTime archivedAt;

    public AbstractArchivedResourceMetaItem(String author, String approver, String type, String contentType, String location, List<IResourceTagItem> fields, OffsetDateTime modifiedAt, OffsetDateTime archivedAt) {
        this.author = author;
        this.approver = approver;
        this.type = type;
        this.contentType = contentType;
        this.location = location;
        this.fields = fields;
        this.modifiedAt = modifiedAt;
        this.archivedAt = archivedAt;
    }

    public String getAuthor() {
        return author;
    }

    public String getApprover() {
        return approver;
    }

    public String getType() {
        return type;
    }

    public String getContentType() {
        return contentType;
    }

    public String getLocation() {
        return location;
    }

    public List<IResourceTagItem> getFields() {
        return fields;
    }

    public OffsetDateTime getModifiedAt() {
        return modifiedAt;
    }

    public OffsetDateTime getArchivedAt() {
        return archivedAt;
    }
}
