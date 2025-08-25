// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository.postgresql;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

/**
 * JUnit extension to start a Postgresql Server. This server should only be started and stopped once for all
 * JUnit Tests.
 */
public class PostgreSQLServerJUnitExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {
    private static final Logger LOGGER = LogManager.getLogger(PostgreSQLServerJUnitExtension.class);

    private static EmbeddedPostgres pg;

    // Gate keeper to prevent multiple Threads within the same routine
    private final static Lock lock = new ReentrantLock();

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        // lock the access so only one Thread has access to it
        lock.lock();
        if (pg == null) {
            pg = EmbeddedPostgres.builder()
                    .start();

            // We will also run Flyway to upgrade the database.
            Flyway flyway = Flyway.configure()
                    .dataSource(pg.getPostgresDatabase())
                    .locations("classpath:org/lfenergy/compas/scl/data/repository/postgresql/db/migration")
                    .load();
            flyway.migrate();

            // The following line registers a callback hook when the root test context is shut down
            context.getRoot().getStore(GLOBAL).put("PostgreSQLServerJUnitExtension", this);
        }
        // free the access
        lock.unlock();
    }

    @Override
    public void close() {
        try {
            pg.close();
        } catch (IOException exp) {
            LOGGER.error("Error closing the Embedded Database.", exp);
        }
    }

    public static DataSource getDataSource() {
        // It does not matter which database it will be after all. We just use the default.
        return pg.getPostgresDatabase();
    }
}