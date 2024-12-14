package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class FeatureFlagsConfiguration {

    @ConfigProperty(name = "compas.scl-data-service.features.soft-delete-enabled", defaultValue = "false")
    boolean softDeleteEnabled;

    public boolean isSoftDeleteEnabled() {
        return softDeleteEnabled;
    }
}
