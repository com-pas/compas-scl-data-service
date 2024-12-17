package org.lfenergy.compas.scl.data.rest.api.archive;

import org.lfenergy.compas.scl.data.model.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArchivedSclResourceTestDataBuilder {
    // Default values
    private String id = UUID.randomUUID().toString();
    private String name = "Name";
    private String version = "1.0.0";
    private String location = "some location";
    private String note = "some note";
    private String author = "user1";
    private String approver = "user2";
    private String type = "some type";
    private String contentType = "contentType1";
    private String voltage = "100";
    private OffsetDateTime modifiedAt = null;
    private OffsetDateTime archivedAt = Instant.now().atZone(ZoneId.systemDefault()).toOffsetDateTime();
    private List<IResourceTagItem> fields = new ArrayList<>();

    public ArchivedSclResourceTestDataBuilder() {
    }

    public ArchivedSclResourceTestDataBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public ArchivedSclResourceTestDataBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ArchivedSclResourceTestDataBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public ArchivedSclResourceTestDataBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public ArchivedSclResourceTestDataBuilder setNote(String note) {
        this.note = note;
        return this;
    }

    public ArchivedSclResourceTestDataBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public ArchivedSclResourceTestDataBuilder setApprover(String approver) {
        this.approver = approver;
        return this;
    }

    public ArchivedSclResourceTestDataBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public ArchivedSclResourceTestDataBuilder setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public ArchivedSclResourceTestDataBuilder setVoltage(String voltage) {
        this.voltage = voltage;
        return this;
    }

    public ArchivedSclResourceTestDataBuilder setModifiedAt(OffsetDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
        return this;
    }

    public ArchivedSclResourceTestDataBuilder setArchivedAt(OffsetDateTime archivedAt) {
        this.archivedAt = archivedAt;
        return this;
    }

    public ArchivedSclResourceTestDataBuilder setFields(List<IResourceTagItem> fields) {
        this.fields = fields;
        return this;
    }

    public IAbstractArchivedResourceMetaItem build() {
        return new ArchivedSclResourceMetaItem(id, name, version, author, approver, type, contentType, location, fields, modifiedAt, archivedAt, note, voltage);
    }
}
