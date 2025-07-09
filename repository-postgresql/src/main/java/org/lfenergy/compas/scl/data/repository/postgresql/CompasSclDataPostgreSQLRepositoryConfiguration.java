// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository.postgresql;

import io.quarkus.arc.DefaultBean;
import io.quarkus.arc.lookup.LookupIfProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;

import javax.sql.DataSource;

public class CompasSclDataPostgreSQLRepositoryConfiguration {

    @Produces
    @ApplicationScoped
    @LookupIfProperty(name = "compas.scl-data-service.features.soft-delete-enabled", stringValue = "true")
    CompasSclDataRepository softDeleteCompasSclDataPostgreSQLRepository(DataSource dataSource) {
        return new SoftDeleteCompasSclDataPostgreSQLRepository(dataSource);
    }

    @Produces
    @ApplicationScoped
    @DefaultBean
    CompasSclDataRepository defaultCompasSclDataPostgreSQLRepository(DataSource dataSource) {
        return new CompasSclDataPostgreSQLRepository(dataSource);
    }
}
