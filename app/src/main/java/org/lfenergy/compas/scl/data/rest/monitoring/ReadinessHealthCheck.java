// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.monitoring;

import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import jakarta.enterprise.context.ApplicationScoped;
import org.lfenergy.compas.scl.data.config.FeatureFlagService;

@Readiness
@ApplicationScoped
public class ReadinessHealthCheck implements HealthCheck {

    @Inject
    FeatureFlagService featureFlagService;

    @Override
    public HealthCheckResponse call() {

        return HealthCheckResponse
                .named("System Ready")
                .up()
                .withData("isHistoryEnabled", featureFlagService.isHistoryEnabled())
                .build();
    }
}