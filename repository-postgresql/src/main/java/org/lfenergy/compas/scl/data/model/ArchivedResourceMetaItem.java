package org.lfenergy.compas.scl.data.model;

import java.time.OffsetDateTime;
import java.util.List;

public class ArchivedResourceMetaItem extends AbstractItem implements IArchivedResourceMetaItem {

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

    public ArchivedResourceMetaItem(String id, String name, String version, String location, String note, String author, String approver, String type, String contentType, String voltage, OffsetDateTime modifiedAt, OffsetDateTime archivedAt, List<IResourceTagItem> fields) {
        super(id, name, version);
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
}
