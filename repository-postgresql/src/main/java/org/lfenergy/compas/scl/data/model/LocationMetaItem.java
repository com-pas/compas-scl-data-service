package org.lfenergy.compas.scl.data.model;

public class LocationMetaItem implements ILocationMetaItem {

    String id;
    String key;
    String name;
    String description;
    int assignedResources;

    public LocationMetaItem(String id, String key, String name, String description, int assignedResources) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.description = description;
        this.assignedResources = assignedResources;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public int getAssignedResources() {
        return assignedResources;
    }
}
