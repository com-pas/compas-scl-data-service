// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonTypeName("PluginWithTypes")
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaJAXRSSpecServerCodegen", comments = "Generator version: 7.12.0")
public class PluginWithTypes {
    private String plugin;
    private @Valid List<String> types = new ArrayList<>();

    public PluginWithTypes() {
    }

    public PluginWithTypes plugin(String plugin) {
        this.plugin = plugin;
        return this;
    }

    @JsonProperty(required = true, value = "plugin")
    @NotNull
    public String getPlugin() {
        return plugin;
    }

    @JsonProperty(required = true, value = "plugin")
    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public PluginWithTypes types(List<String> types) {
        this.types = types;
        return this;
    }

    @JsonProperty(required = true, value = "types")
    @NotNull
    public List<String> getTypes() {
        return types;
    }

    @JsonProperty(required = true, value = "types")
    public void setTypes(List<String> types) {
        this.types = types;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PluginWithTypes that = (PluginWithTypes) o;
        return Objects.equals(plugin, that.plugin) && Objects.equals(types, that.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plugin, types);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PluginWithTypes {\n");
        sb.append("    plugin: ").append(toIndentedString(plugin)).append("\n");
        sb.append("    types: ").append(toIndentedString(types)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
