// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository.postgresql;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;

import javax.sql.DataSource;

public class CompasSclDataPostgreSQLRepositoryConfiguration {

    private static final Logger LOGGER = LogManager.getLogger(CompasSclDataPostgreSQLRepositoryConfiguration.class);

    @ConfigProperty(name = "compas.scl-data-service.features.soft-delete-enabled", defaultValue = "false")
    private boolean softDeleteEnabled;

    @Produces
    @ApplicationScoped
    CompasSclDataRepository softDeleteCompasSclDataPostgreSQLRepository(DataSource dataSource) {
        if (!softDeleteEnabled) {
            LOGGER.warn("Soft delete feature is disabled, using default repository.");
            return new CompasSclDataPostgreSQLRepository(dataSource);
        }
        LOGGER.info("Soft delete feature is enabled. Using SoftDeleteCompasSclDataPostgreSQLRepository.");
        return new SoftDeleteCompasSclDataPostgreSQLRepository(dataSource);
    }
}
