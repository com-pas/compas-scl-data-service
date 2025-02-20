package org.lfenergy.compas.scl.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;



@JsonTypeName("ResourceData")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", date = "2025-02-04T13:09:27.372199200+01:00[Europe/Vienna]", comments = "Generator version: 7.8.0")
public class ResourceData   {

    private TypeEnum type;
    private UUID uuid;
    private String location;
    private @Valid List<@Valid ResourceTag> tags = new ArrayList<>();
    private String name;
    private String contentType;
    private String version;
    private String extension;
    private String data;

    /**
     **/
    public ResourceData type(TypeEnum type) {
        this.type = type;
        return this;
    }


    @JsonProperty("type")
    @NotNull public TypeEnum getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(TypeEnum type) {
        this.type = type;
    }

    /**
     **/
    public ResourceData uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }


    @JsonProperty("uuid")
    @NotNull public UUID getUuid() {
        return uuid;
    }

    @JsonProperty("uuid")
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * required field when &#39;type &#x3D;&#x3D; RESOURCE&#39;
     **/
    public ResourceData location(String location) {
        this.location = location;
        return this;
    }


    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     **/
    public ResourceData tags(List<@Valid ResourceTag> tags) {
        this.tags = tags;
        return this;
    }


    @JsonProperty("tags")
    @Valid public List<@Valid ResourceTag> getTags() {
        return tags;
    }

    @JsonProperty("tags")
    public void setTags(List<@Valid ResourceTag> tags) {
        this.tags = tags;
    }

    public ResourceData addTagsItem(ResourceTag tagsItem) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }

        this.tags.add(tagsItem);
        return this;
    }

    public ResourceData removeTagsItem(ResourceTag tagsItem) {
        if (tagsItem != null && this.tags != null) {
            this.tags.remove(tagsItem);
        }

        return this;
    }
    /**
     **/
    public ResourceData name(String name) {
        this.name = name;
        return this;
    }


    @JsonProperty("name")
    @NotNull public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     **/
    public ResourceData contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }


    @JsonProperty("contentType")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("contentType")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     **/
    public ResourceData version(String version) {
        this.version = version;
        return this;
    }


    @JsonProperty("version")
    @NotNull public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     **/
    public ResourceData extension(String extension) {
        this.extension = extension;
        return this;
    }


    @JsonProperty("extension")
    public String getExtension() {
        return extension;
    }

    @JsonProperty("extension")
    public void setExtension(String extension) {
        this.extension = extension;
    }

    /**
     * UTF8 encoded input stream of the resource
     **/
    public ResourceData data(String data) {
        this.data = data;
        return this;
    }


    @JsonProperty("data")
    @NotNull public String getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(String data) {
        this.data = data;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourceData resourceData = (ResourceData) o;
        return Objects.equals(this.type, resourceData.type) &&
            Objects.equals(this.uuid, resourceData.uuid) &&
            Objects.equals(this.location, resourceData.location) &&
            Objects.equals(this.tags, resourceData.tags) &&
            Objects.equals(this.name, resourceData.name) &&
            Objects.equals(this.contentType, resourceData.contentType) &&
            Objects.equals(this.version, resourceData.version) &&
            Objects.equals(this.extension, resourceData.extension) &&
            Objects.equals(this.data, resourceData.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, uuid, location, tags, name, contentType, version, extension, data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ResourceData {\n");

        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("    uuid: ").append(toIndentedString(uuid)).append("\n");
        sb.append("    location: ").append(toIndentedString(location)).append("\n");
        sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
        sb.append("    name: ").append(toIndentedString(name)).append("\n");
        sb.append("    contentType: ").append(toIndentedString(contentType)).append("\n");
        sb.append("    version: ").append(toIndentedString(version)).append("\n");
        sb.append("    extension: ").append(toIndentedString(extension)).append("\n");
        sb.append("    data: ").append(toIndentedString(data)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }


}