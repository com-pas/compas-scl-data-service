// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository.postgres;

import org.flywaydb.core.Flyway;
import org.lfenergy.compas.scl.data.repository.CompasMigrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * Implementation of the Database Migrator to migrate the database to the latest version
 * to support the newest software implementation. For the PostgreSQL implementation we
 * are using Flyway to migrate the database.
 * <p>
 * To execute the migration a DataSource is needed and passed to the constructor.
 */
@ApplicationScoped
public class CompasFlywayMigrator implements CompasMigrator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasFlywayMigrator.class);

    private final Flyway flyway;

    @Inject
    public CompasFlywayMigrator(DataSource dataSource) {
        this(dataSource, "classpath:org/lfenergy/compas/scl/data/repository/postgres/db/migration");
    }

    public CompasFlywayMigrator(DataSource dataSource, String... locations) {
        LOGGER.info("Configuring Flyway.");
        flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(locations)
                .load();
    }

    @Override
    public void migrate() {
        // After the bean is created we will execute the migration to the latest version.
        LOGGER.info("Migrating database using Flyway.");
        flyway.migrate();
    }
}
