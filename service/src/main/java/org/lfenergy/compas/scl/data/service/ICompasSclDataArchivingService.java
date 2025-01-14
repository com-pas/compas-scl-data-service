package org.lfenergy.compas.scl.data.service;

import org.lfenergy.compas.scl.data.model.IAbstractArchivedResourceMetaItem;
import org.lfenergy.compas.scl.data.model.ILocationMetaItem;

import java.io.File;
import java.util.List;

public interface ICompasSclDataArchivingService {

    void createLocation(ILocationMetaItem location);

    void deleteLocation(ILocationMetaItem location);

    void archiveSclData(String filename, File body, IAbstractArchivedResourceMetaItem archivedResource);

    void archiveSclData(IAbstractArchivedResourceMetaItem archivedResource, String data);

    void deleteSclDataFromLocation(ILocationMetaItem location, List<String> resourceIds);

    void moveArchivedResourcesToLocation(ILocationMetaItem oldLocation, ILocationMetaItem newLocation, List<String> resourceIds);
}
