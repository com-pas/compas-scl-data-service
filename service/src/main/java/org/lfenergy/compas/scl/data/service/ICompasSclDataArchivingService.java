package org.lfenergy.compas.scl.data.service;

import org.lfenergy.compas.scl.data.model.IAbstractArchivedResourceMetaItem;
import org.lfenergy.compas.scl.data.model.ILocationMetaItem;

import java.io.File;
import java.util.UUID;

public interface ICompasSclDataArchivingService {

    void createLocation(ILocationMetaItem location);

    void archiveData(String locationName, String filename, UUID uuid, File body, IAbstractArchivedResourceMetaItem archivedResource);

    void archiveSclData(UUID uuid, IAbstractArchivedResourceMetaItem archivedResource, String locationName, String data);
}
