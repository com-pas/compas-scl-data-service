package org.lfenergy.compas.scl.data.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class FeatureFlagService {

    @ConfigProperty(name = "scl-data-service.features.is-history-enabled", defaultValue = "true")
    boolean isHistoryEnabled;
    @ConfigProperty(name = "scl-data-service.features.keep-deleted-files", defaultValue = "true")
    boolean keepDeletedFiles;

    public boolean isHistoryEnabled() {
        return isHistoryEnabled;
    }

    public boolean keepDeletedFiles() {
        return keepDeletedFiles;
    }
}