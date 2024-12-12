package org.lfenergy.compas.scl.data.model;

import java.util.List;

public class ArchivedResourcesMetaItem implements IArchivedResourcesMetaItem {

    List<IAbstractArchivedResourceMetaItem> archivedResources;

    public ArchivedResourcesMetaItem(List<IAbstractArchivedResourceMetaItem> archivedResources) {
        this.archivedResources = archivedResources;
    }

    @Override
    public List<IAbstractArchivedResourceMetaItem> getResources() {
        return archivedResources;
    }
}
