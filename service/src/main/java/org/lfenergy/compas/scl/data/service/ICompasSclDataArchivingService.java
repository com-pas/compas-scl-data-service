package org.lfenergy.compas.scl.data.service;

import io.smallrye.mutiny.Uni;
import org.lfenergy.compas.scl.data.dto.LocationMetaData;
import org.lfenergy.compas.scl.data.dto.ResourceMetaData;
import org.lfenergy.compas.scl.data.model.IAbstractArchivedResourceMetaItem;
import org.lfenergy.compas.scl.data.model.ILocationMetaItem;

import java.io.File;
import java.util.UUID;

public interface ICompasSclDataArchivingService {

    Uni<LocationMetaData> createLocation(ILocationMetaItem location);

    Uni<ResourceMetaData> archiveData(String locationName, String filename, UUID uuid, File body, IAbstractArchivedResourceMetaItem archivedResource);

    Uni<ResourceMetaData> archiveSclData(UUID uuid, IAbstractArchivedResourceMetaItem archivedResource, String locationName, String data);
}
