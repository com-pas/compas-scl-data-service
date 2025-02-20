package org.lfenergy.compas.scl.data.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ResourceTag")
public class ResourceTag {
    private final String key;
    private final String value;

    @JsonCreator
    public ResourceTag(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }
}
