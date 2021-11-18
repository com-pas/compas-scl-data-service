// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import io.quarkus.runtime.Startup;
import org.lfenergy.compas.scl.data.service.CompasMigratorService;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Startup
@ApplicationScoped
public class CompasMigratorStartup {
    @Inject
    CompasMigratorService compasMigratorService;

    @PostConstruct
    public void migrate() {
        compasMigratorService.migrate();
    }
}
