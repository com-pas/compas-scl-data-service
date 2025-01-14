package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.lfenergy.compas.scl.data.model.IAbstractArchivedResourceMetaItem;
import org.lfenergy.compas.scl.data.model.ILocationMetaItem;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@ApplicationScoped
public class CompasSclDataArchivingServiceImpl implements ICompasSclDataArchivingService {

    private final String DEFAULT_PATH = System.getProperty("user.dir") + File.separator + "locations";

    @Override
    public void createLocation(ILocationMetaItem location) {
        File newLocationDirectory = new File(DEFAULT_PATH + File.separator + location.getName());
        newLocationDirectory.mkdirs();
    }

    @Override
    public void deleteLocation(ILocationMetaItem location) {
        File directory = new File(DEFAULT_PATH + File.separator + location.getName());
        directory.delete();
    }

    @Override
    public void archiveSclData(String filename, File body, IAbstractArchivedResourceMetaItem archivedResource) {
        String absolutePath = generateSclDataLocation(archivedResource);
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
    public void archiveSclData(IAbstractArchivedResourceMetaItem archivedResource, String data) {
        String absolutePath = generateSclDataLocation(archivedResource);
        File locationDir = new File(absolutePath);
        locationDir.mkdirs();
        File f = new File(locationDir + File.separator + archivedResource.getName() + "." + archivedResource.getContentType().toLowerCase());
        try (FileWriter fw = new FileWriter(f)) {
            fw.write(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateSclDataLocation(IAbstractArchivedResourceMetaItem archivedResource) {
        return DEFAULT_PATH + File.separator + archivedResource.getLocation() + File.separator + archivedResource.getId();
    }

    @Override
    public void deleteSclDataFromLocation(ILocationMetaItem location, List<String> resourceIds) {
        File locationDir = new File(DEFAULT_PATH + File.separator + location.getName() + File.separator);
        try (Stream<Path> paths = Files.walk(locationDir.toPath()).skip(1)) {
            paths.sorted(Comparator.reverseOrder()).map(Path::toFile).filter(f ->
                resourceIds.stream().anyMatch(id -> f.getAbsolutePath().contains(id))
            ).forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void moveArchivedResourcesToLocation(ILocationMetaItem oldLocation, ILocationMetaItem newLocation, List<String> resourceIds) {
        resourceIds.forEach(id -> {
            Path oldPath = new File(DEFAULT_PATH + File.separator + oldLocation.getName() + File.separator + id).toPath();
            Path newPath = new File(DEFAULT_PATH + File.separator + newLocation.getName() + File.separator + id).toPath();
            try {
                Files.move(oldPath, newPath, REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
