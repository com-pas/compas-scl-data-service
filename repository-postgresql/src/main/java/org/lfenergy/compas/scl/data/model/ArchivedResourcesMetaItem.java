package org.lfenergy.compas.scl.data.model;

import java.util.List;

public class ArchivedResourcesMetaItem implements IArchivedResourcesMetaItem {

    List<IArchivedResourceMetaItem> archivedResources;

    public ArchivedResourcesMetaItem(List<IArchivedResourceMetaItem> archivedResources) {
        this.archivedResources = archivedResources;
    }

    @Override
    public List<IArchivedResourceMetaItem> getResources() {
        return archivedResources;
    }
}
