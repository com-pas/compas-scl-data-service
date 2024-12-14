package org.lfenergy.compas.scl.data.model;

import java.time.OffsetDateTime;

public class HistoryMetaItem extends AbstractItem implements IHistoryMetaItem {
    private final String type;
    private final String author;
    private final String comment;
    private final OffsetDateTime changedAt;
    private final boolean deleted;

    public HistoryMetaItem(String id, String name, String version, String type, String author, String comment, OffsetDateTime changedAt, boolean deleted) {
        super(id, name, version);
        this.type = type;
        this.author = author;
        this.comment = comment;
        this.changedAt = changedAt;
        this.deleted = deleted;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public OffsetDateTime getChangedAt() {
        return changedAt;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }
}
