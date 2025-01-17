package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.lfenergy.compas.scl.data.model.IAbstractArchivedResourceMetaItem;
import org.lfenergy.compas.scl.data.model.ILocationMetaItem;

import java.io.*;
import java.util.UUID;

@ApplicationScoped
public class CompasSclDataArchivingServiceImpl implements ICompasSclDataArchivingService {

    private final String DEFAULT_PATH = System.getProperty("user.dir") + File.separator + "locations";

    @Override
    public void createLocation(ILocationMetaItem location) {
        File newLocationDirectory = new File(DEFAULT_PATH + File.separator + location.getName());
        newLocationDirectory.mkdirs();
    }

    @Override
    public void archiveSclData(String locationName, String filename, UUID resourceId, File body, IAbstractArchivedResourceMetaItem archivedResource) {
        String absolutePath = generateSclDataLocation(resourceId, archivedResource, locationName) + File.separator + "referenced_resources";
        File locationDir = new File(absolutePath);
        locationDir.mkdirs();
        File f = new File(absolutePath + File.separator + filename);
        try (FileOutputStream fos = new FileOutputStream(f)) {
            try (FileInputStream fis = new FileInputStream(body)) {
                fos.write(fis.readAllBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void archiveSclData(UUID resourceId, IAbstractArchivedResourceMetaItem archivedResource, String locationName, String data) {
        String absolutePath = generateSclDataLocation(resourceId, archivedResource, locationName);
        File locationDir = new File(absolutePath);
        locationDir.mkdirs();
        File f = new File(locationDir + File.separator + archivedResource.getName() + "." + archivedResource.getContentType().toLowerCase());
        try (FileWriter fw = new FileWriter(f)) {
            fw.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateSclDataLocation(UUID resourceId, IAbstractArchivedResourceMetaItem archivedResource, String locationName) {
        return DEFAULT_PATH + File.separator + locationName + File.separator + resourceId + File.separator + archivedResource.getVersion();
    }
}
