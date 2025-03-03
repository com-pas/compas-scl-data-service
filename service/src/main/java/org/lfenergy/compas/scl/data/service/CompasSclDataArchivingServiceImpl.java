package org.lfenergy.compas.scl.data.service;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.lfenergy.compas.scl.data.dto.LocationMetaData;
import org.lfenergy.compas.scl.data.dto.ResourceMetaData;
import org.lfenergy.compas.scl.data.dto.ResourceTag;
import org.lfenergy.compas.scl.data.dto.TypeEnum;
import org.lfenergy.compas.scl.data.model.IAbstractArchivedResourceMetaItem;
import org.lfenergy.compas.scl.data.model.ILocationMetaItem;

import java.io.*;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CompasSclDataArchivingServiceImpl implements ICompasSclDataArchivingService {

    @ConfigProperty(name = "scl-data-service.archiving.filesystem.location", defaultValue = "/work/locations")
    String locationPath;

    @Override
    public Uni<LocationMetaData> createLocation(ILocationMetaItem location) {
        File newLocationDirectory = new File(locationPath + File.separator + location.getName());
        if (!newLocationDirectory.exists()) {
            newLocationDirectory.mkdirs();
        }
        return Uni.createFrom()
            .item(new LocationMetaData()
                .uuid(UUID.fromString(location.getId()))
                .key(location.getKey())
                .name(location.getName())
                .description(location.getDescription())
                .assignedResources(location.getAssignedResources()));
    }

    @Override
    public Uni<ResourceMetaData> archiveData(String locationKey, String filename, UUID resourceId, File body, IAbstractArchivedResourceMetaItem archivedResource) {
        String absolutePath = generateSclDataLocation(resourceId, archivedResource, locationKey) + File.separator + "referenced_resources";
        File locationDir = new File(absolutePath);
        locationDir.mkdirs();
        File f = new File(absolutePath + File.separator + filename);
        if (f.exists() && !f.isDirectory()) {
            return Uni.createFrom().failure(new RuntimeException("File '"+filename+"' already exists"));
        }
        try (FileOutputStream fos = new FileOutputStream(f)) {
            try (FileInputStream fis = new FileInputStream(body)) {
                fos.write(fis.readAllBytes());
            }
        } catch (IOException e) {
            return Uni.createFrom().failure(new RuntimeException(e));
        }
        List<ResourceTag> archivedResourceTag = archivedResource.getFields().stream().map(field -> new ResourceTag(field.getKey(), field.getValue())).toList();
        return Uni.createFrom().item(new ResourceMetaData(TypeEnum.RESOURCE, resourceId, locationKey, archivedResourceTag));
    }

    @Override
    public Uni<ResourceMetaData> archiveSclData(UUID resourceId, IAbstractArchivedResourceMetaItem archivedResource, String locationKey, String data) {
        String absolutePath = generateSclDataLocation(resourceId, archivedResource, locationKey);
        File locationDir = new File(absolutePath);
        locationDir.mkdirs();
        File f = new File(locationDir + File.separator + archivedResource.getName() + "." + archivedResource.getType().toLowerCase());
        try (FileWriter fw = new FileWriter(f)) {
            fw.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<ResourceTag> archivedResourceTag = archivedResource.getFields().stream().map(field -> new ResourceTag(field.getKey(), field.getValue())).toList();
        return Uni.createFrom().item(new ResourceMetaData(TypeEnum.RESOURCE, resourceId, locationKey, archivedResourceTag));
    }

    private String generateSclDataLocation(UUID resourceId, IAbstractArchivedResourceMetaItem archivedResource, String locationKey) {
        return locationPath + File.separator + locationKey + File.separator + resourceId + File.separator + archivedResource.getVersion();
    }
}
