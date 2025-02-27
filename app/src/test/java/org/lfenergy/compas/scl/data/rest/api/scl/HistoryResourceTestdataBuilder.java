package org.lfenergy.compas.scl.data.rest.api.scl;

import org.lfenergy.compas.scl.data.model.HistoryMetaItem;
import org.lfenergy.compas.scl.data.model.IHistoryMetaItem;

import java.time.OffsetDateTime;
import java.util.UUID;

public class HistoryResourceTestdataBuilder {

    // Default values
    private String id = UUID.randomUUID().toString();
    private String name = "Name";
    private String version = "1.0.0";
    private String type = "SSD";
    private String author = "Test";
    private String comment = "Created";
    private OffsetDateTime changedAt = OffsetDateTime.now();
    private boolean archived = false;
    private boolean available = false;
    private boolean deleted = false;

    public HistoryResourceTestdataBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public HistoryResourceTestdataBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public HistoryResourceTestdataBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public HistoryResourceTestdataBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public HistoryResourceTestdataBuilder setAuthor(String author) {
        this.author = author;
        return this;
    }

    public HistoryResourceTestdataBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public HistoryResourceTestdataBuilder setChangedAt(OffsetDateTime changedAt) {
        this.changedAt = changedAt;
        return this;
    }

    public HistoryResourceTestdataBuilder setArchived(boolean archived) {
        this.archived = archived;
        return this;
    }

    public HistoryResourceTestdataBuilder setAvailable(boolean available) {
        this.available = available;
        return this;
    }

    public HistoryResourceTestdataBuilder setDeleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    // Build method to create a new HistoryMetaItem object
    public IHistoryMetaItem build() {
        return new HistoryMetaItem(id, name, version, type, author, comment, changedAt, archived, available, deleted);
    }
}
