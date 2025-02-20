package org.lfenergy.compas.scl.data.dto;

import java.util.List;
import java.util.UUID;

public class ResourceMetaData {
    private final TypeEnum type;
    private final UUID uuid;
    private final String location;
    private final List<ResourceTag> tags;

    public ResourceMetaData(TypeEnum type, UUID uuid, String location, List<ResourceTag> tags) {
        this.type = type;
        this.uuid = uuid;
        this.location = location;
        this.tags = tags;
    }

    public TypeEnum getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getLocation() {
        return location;
    }

    public List<ResourceTag> getTags() {
        return tags;
    }
}
