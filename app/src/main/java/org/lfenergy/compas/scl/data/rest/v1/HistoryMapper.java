package org.lfenergy.compas.scl.data.rest.v1;

import org.lfenergy.compas.scl.data.model.IHistoryMetaItem;
import org.lfenergy.compas.scl.rest.api.beans.DataResource;
import org.lfenergy.compas.scl.rest.api.beans.DataResourceVersion;

import java.util.UUID;

public final class HistoryMapper {

    private HistoryMapper() {
    }

    public static DataResourceVersion convertToDataResourceVersion(IHistoryMetaItem item) {
        DataResourceVersion version = new DataResourceVersion();
        version.setVersion(item.getVersion());
        version.setAuthor(item.getAuthor());
        version.setName(item.getName());
        version.setDeleted(item.isDeleted());
        version.setUuid(UUID.fromString(item.getId()));
        version.setType(item.getType());
        version.setChangedAt(DateUtil.convertToDate(item.getChangedAt()));
        version.setComment(item.getComment());
        return version;
    }

    public static DataResource convertToDataResource(IHistoryMetaItem item) {
        DataResource version = new DataResource();
        version.setVersion(item.getVersion());
        version.setAuthor(item.getAuthor());
        version.setName(item.getName());
        version.setDeleted(item.isDeleted());
        version.setUuid(UUID.fromString(item.getId()));
        version.setType(item.getType());
        version.setChangedAt(DateUtil.convertToDate(item.getChangedAt()));
        return version;
    }

}
