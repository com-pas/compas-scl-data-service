// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import io.quarkus.arc.profile.IfBuildProfile;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.repository.postgresql.CompasSclDataPostgreSQLRepository;

import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

/**
 * Create Beans from other dependencies that are used in the application.
 */
public class CompasSclDataServicePostgreSQLConfiguration {
    @Produces
    @IfBuildProfile("prod-postgresql")
    public CompasSclDataRepository creatCompasSclDataRepositoryProduction(DataSource dataSource) {
        return new CompasSclDataPostgreSQLRepository(dataSource);
    }

    @Produces
    @IfBuildProfile("dev-postgresql")
    public CompasSclDataRepository creatCompasSclDataRepositoryDevelopment(DataSource dataSource) {
        return creatCompasSclDataRepositoryProduction(dataSource);
    }
}
