package org.lfenergy.compas.scl.data.rest.api.archive;

import org.lfenergy.compas.scl.data.model.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArchivedResourceVersionTestDataBuilder {
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

    public ArchivedResourceVersionTestDataBuilder() {
    }

    public ArchivedResourceVersionTestDataBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public ArchivedResourceVersionTestDataBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ArchivedResourceVersionTestDataBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public ArchivedResourceVersionTestDataBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public ArchivedResourceVersionTestDataBuilder setNote(String note) {
        this.note = note;
        return this;
    }

    public ArchivedResourceVersionTestDataBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public ArchivedResourceVersionTestDataBuilder setApprover(String approver) {
        this.approver = approver;
        return this;
    }

    public ArchivedResourceVersionTestDataBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public ArchivedResourceVersionTestDataBuilder setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public ArchivedResourceVersionTestDataBuilder setVoltage(String voltage) {
        this.voltage = voltage;
        return this;
    }

    public ArchivedResourceVersionTestDataBuilder setModifiedAt(OffsetDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
        return this;
    }

    public ArchivedResourceVersionTestDataBuilder setArchivedAt(OffsetDateTime archivedAt) {
        this.archivedAt = archivedAt;
        return this;
    }

    public ArchivedResourceVersionTestDataBuilder setFields(List<IResourceTagItem> fields) {
        this.fields = fields;
        return this;
    }

    public IArchivedResourceVersion build() {
        return new ArchivedResourceVersion(id, name, version, location, note, author, approver, type, contentType, voltage, fields, modifiedAt, archivedAt, note, true);
    }
}
