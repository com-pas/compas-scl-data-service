package org.lfenergy.compas.scl.data.model;

import java.time.OffsetDateTime;
import java.util.List;

public class ArchivedResourceVersion extends AbstractItem implements IArchivedResourceVersion {

    String location;
    String note;
    String author;
    String approver;
    String type;
    String contentType;
    String voltage;
    OffsetDateTime modifiedAt;
    OffsetDateTime archivedAt;
    List<IResourceTagItem> fields;
    String comment;
    boolean archived;

    public ArchivedResourceVersion(String id, String name, String version, String location, String note, String author, String approver, String type, String contentType, String voltage, List<IResourceTagItem> fields, OffsetDateTime modifiedAt, OffsetDateTime archivedAt, String comment, boolean archived) {
        super(id, name, version, null);
        this.location = location;
        this.note = note;
        this.author = author;
        this.approver = approver;
        this.type = type;
        this.contentType = contentType;
        this.voltage = voltage;
        this.modifiedAt = modifiedAt;
        this.archivedAt = archivedAt;
        this.fields = fields;
        this.comment = comment;
        this.archived = archived;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getApprover() {
        return approver;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getVoltage() {
        return voltage;
    }

    @Override
    public OffsetDateTime getModifiedAt() {
        return modifiedAt;
    }

    @Override
    public OffsetDateTime getArchivedAt() {
        return archivedAt;
    }

    @Override
    public List<IResourceTagItem> getFields() {
        return fields;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public boolean isArchived() {
        return archived;
    }
}
