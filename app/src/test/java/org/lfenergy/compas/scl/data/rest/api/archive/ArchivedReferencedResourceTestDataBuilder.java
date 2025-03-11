package org.lfenergy.compas.scl.data.rest.api.archive;

import org.lfenergy.compas.scl.data.model.ArchivedReferencedResourceMetaItem;
import org.lfenergy.compas.scl.data.model.IAbstractArchivedResourceMetaItem;
import org.lfenergy.compas.scl.data.model.IResourceTagItem;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArchivedReferencedResourceTestDataBuilder {
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
    private OffsetDateTime modifiedAt = null;
    private OffsetDateTime archivedAt = Instant.now().atZone(ZoneId.systemDefault()).toOffsetDateTime();
    private List<IResourceTagItem> fields = new ArrayList<>();

    public ArchivedReferencedResourceTestDataBuilder() {
    }

    public ArchivedReferencedResourceTestDataBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public ArchivedReferencedResourceTestDataBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ArchivedReferencedResourceTestDataBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public ArchivedReferencedResourceTestDataBuilder setLocation(String location) {
        this.location = location;
        return this;
    }

    public ArchivedReferencedResourceTestDataBuilder setNote(String note) {
        this.note = note;
        return this;
    }

    public ArchivedReferencedResourceTestDataBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public ArchivedReferencedResourceTestDataBuilder setApprover(String approver) {
        this.approver = approver;
        return this;
    }

    public ArchivedReferencedResourceTestDataBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public ArchivedReferencedResourceTestDataBuilder setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public ArchivedReferencedResourceTestDataBuilder setModifiedAt(OffsetDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
        return this;
    }

    public ArchivedReferencedResourceTestDataBuilder setArchivedAt(OffsetDateTime archivedAt) {
        this.archivedAt = archivedAt;
        return this;
    }

    public ArchivedReferencedResourceTestDataBuilder setFields(List<IResourceTagItem> fields) {
        this.fields = fields;
        return this;
    }

    public IAbstractArchivedResourceMetaItem build() {
        return new ArchivedReferencedResourceMetaItem(id, name, version, author, approver, type, contentType, location, fields, modifiedAt, archivedAt, note);
    }
}
