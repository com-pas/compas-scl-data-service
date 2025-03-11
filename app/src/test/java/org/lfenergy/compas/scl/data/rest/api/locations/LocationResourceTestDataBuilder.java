package org.lfenergy.compas.scl.data.rest.api.locations;

import org.lfenergy.compas.scl.data.model.ILocationMetaItem;
import org.lfenergy.compas.scl.data.model.LocationMetaItem;

import java.util.UUID;

public class LocationResourceTestDataBuilder {
    // Default values
    private String id = UUID.randomUUID().toString();
    private String name = "Name";
    private String key = "Key";
    private String description = "Description";
    private int assignedResources = 0;

    public LocationResourceTestDataBuilder() {
    }

    public LocationResourceTestDataBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public LocationResourceTestDataBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public LocationResourceTestDataBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public LocationResourceTestDataBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public LocationResourceTestDataBuilder setAssignedResources(int assignedResources) {
        this.assignedResources = assignedResources;
        return this;
    }

    public ILocationMetaItem build() {
        return new LocationMetaItem(id, key, name, description, assignedResources);
    }
}
