package org.lfenergy.compas.scl.data.model;

import java.util.List;

public class ArchivedResourcesHistoryMetaItem implements IArchivedResourcesHistoryMetaItem {

    List<IArchivedResourceVersion> versions;

    public ArchivedResourcesHistoryMetaItem(List<IArchivedResourceVersion> versions) {
        this.versions = versions;
    }

    @Override
    public List<IArchivedResourceVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<IArchivedResourceVersion> versions) {
        this.versions = versions;
    }
}
