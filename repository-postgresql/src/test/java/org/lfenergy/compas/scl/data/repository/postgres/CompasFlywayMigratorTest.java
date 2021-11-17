package org.lfenergy.compas.scl.data.repository.postgres;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({PostgreSQLServerJUnitExtension.class})
class CompasFlywayMigratorTest {
    private CompasFlywayMigrator compasFlywayMigrator;

    @BeforeEach
    void beforeEach() {
        compasFlywayMigrator = new CompasFlywayMigrator(PostgreSQLServerJUnitExtension.getDataSource());
    }

    @Test
    void migrate_WhenCalled_ThenFlywayMigratesDatabaseAndNoErrorShouldOccur() {
        compasFlywayMigrator.migrate();
    }
}