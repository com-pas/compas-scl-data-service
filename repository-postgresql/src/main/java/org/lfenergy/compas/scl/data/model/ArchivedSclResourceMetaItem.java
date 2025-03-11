package org.lfenergy.compas.scl.data.model;

import java.time.OffsetDateTime;
import java.util.List;

public class ArchivedSclResourceMetaItem extends AbstractArchivedResourceMetaItem implements IAbstractArchivedResourceMetaItem {
    String id;
    String name;
    String version;
    String note;
    String voltage;

    public ArchivedSclResourceMetaItem(String id, String name, String version, String author, String approver, String type, String contentType, String location, List<IResourceTagItem> fields, OffsetDateTime modifiedAt, OffsetDateTime archivedAt, String note, String voltage) {
        super(author, approver, type, contentType, location, fields, modifiedAt, archivedAt);
        this.id = id;
        this.name = name;
        this.version = version;
        this.note = note;
        this.voltage = voltage;
    }


    public String getNote() {
        return note;
    }

    public String getVoltage() {
        return voltage;
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
}
