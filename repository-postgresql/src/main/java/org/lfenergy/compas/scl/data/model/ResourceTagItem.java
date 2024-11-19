package org.lfenergy.compas.scl.data.model;

public class ResourceTagItem implements IResourceTagItem {
    String id;
    String key;
    String value;

    public ResourceTagItem(String id, String key, String value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
